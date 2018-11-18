package facemywrath.cowcannon.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import facemywrath.cowcannon.commands.CannonCommand;
import facemywrath.cowcannon.events.EventListener;

public class Main extends JavaPlugin {

	public void onEnable() {
		this.saveResource("config.yml", false);
		EventListener listener = new EventListener(this, this.getConfig().contains("cooldown-in-milliseconds")?this.getConfig().getLong("cooldown-in-milliseconds"):8000L);
		this.getCommand("cannon").setExecutor(new CannonCommand(listener.getCannonItem()));
		if(this.getConfig().contains("use-recipe") && this.getConfig().getBoolean("use-recipe"))
		{
			ShapedRecipe recipe = new ShapedRecipe(listener.getCannonItem());
			recipe.shape("xxo", "olo", "olo");
			recipe.setIngredient('x', Material.MONSTER_EGG);
			recipe.setIngredient('o', Material.AIR);
			recipe.setIngredient('l', Material.STICK);
			Bukkit.addRecipe(recipe);
		}
	}

}
