package facemywrath.cowcannon.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CannonCommand implements CommandExecutor {

	private ItemStack cannon;
	
	public CannonCommand(ItemStack cannon) {
		this.cannon = cannon;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player player = (Player) sender;
		if(!player.hasPermission("cowcannon.give"))
		{
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You don't have permission for that!"));
			return true;
		}
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bYou've been given a cow cannon."));
		player.getInventory().addItem(cannon);
		return true;
	}

}
