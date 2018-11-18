package facemywrath.cowcannon.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import facemywrath.cowcannon.main.Main;
import facemywrath.cowcannon.util.Animation;
import facemywrath.cowcannon.util.ItemCreator;
import net.md_5.bungee.api.ChatColor;

public class EventListener {
	
	private Long cooldown = 8000L;

	private HashMap<Player, Long> cooldowns = new HashMap<>();
	
	public EventListener(Main main, long cooldown) {
		this.cooldown = cooldown;
		animation = new Animation(main).addFrame(ent -> {
			Cow cow = (Cow) ent;
			if(!cow.isOnGround()) return;
			cow.remove();
			cow.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, cow.getLocation(), 3);
			for(int i = 0; i < ThreadLocalRandom.current().nextInt(3, 5); i++) 
			{
				double x = ThreadLocalRandom.current().nextDouble(-2, 2);
				double z = ThreadLocalRandom.current().nextDouble(-2, 2);
				Location loc = cow.getLocation().add(new Vector(x,0,z));
				if(loc.getBlock().getType() != Material.AIR) continue;
				Cow baby = (Cow) cow.getWorld().spawnEntity(cow.getLocation().add(new Vector(x,0,z)), EntityType.COW);
				baby.setBaby();
			}
			animation.stop(cow);
		}, 3L).setLooping(true, 0L);
		cannonShot(main);
	}

	private Animation<Cow> animation;
	private List<Cow> cowCannons = new ArrayList<>();

	public ItemStack getCannonItem() {
		return new ItemCreator(Material.GOLD_HOE).name("&9Cow Cannon").lore("Turn eggs into cows").build();
	}

	private void cannonShot(Main main) {
		Events.listen(main, PlayerInteractEvent.class, event -> {
			if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
			Player player = event.getPlayer();
 			if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) return;
			ItemStack hand = player.getInventory().getItemInMainHand().clone();
			hand.setDurability(getCannonItem().getDurability());
			if(!hand.isSimilar(getCannonItem())) return;
			event.setCancelled(true);
			if(!player.getInventory().contains(Material.EGG))
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Requires chicken eggs to shoot"));
				return;
			}
			if(cooldowns.containsKey(player) && cooldowns.get(player) > System.currentTimeMillis()) {
				double seconds = ((cooldowns.get(player) - System.currentTimeMillis())/1000.0);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Can not shoot for another " + seconds + " seconds."));
				return;
			}
			player.getInventory().removeItem(new ItemStack(Material.EGG));
			launchCow(player);
		});
	}

	private void launchCow(Player player) {
		cooldowns.put(player, System.currentTimeMillis() + cooldown);
		Cow cow = (Cow) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.COW);
		cow.setInvulnerable(true);
		cow.setVelocity(player.getLocation().getDirection().multiply(2));
		cowCannons.add(cow);
		animation.animate(cow);
	}


}
