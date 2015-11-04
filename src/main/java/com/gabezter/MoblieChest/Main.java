package com.gabezter.MoblieChest;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	File users = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "User's Chest");
	File chests = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "Chests");

	Listen listen = new Listen(this);
	Methods method;

	FileConfiguration chestsConfig = null;
	File chestsFile;

	@Override
	public void onEnable() {
		chestsFile = new File(this.getDataFolder(), "Chests.yml");
		chestsConfig = YamlConfiguration.loadConfiguration(chestsFile);

		getServer().getPluginManager().registerEvents(listen, this);

		if (!users.exists()) {
			users.mkdirs();
		}
		if (!chests.exists()) {
			chests.mkdirs();
		}
	}

	FileConfiguration userconfig = null;
	File userFile;

	FileConfiguration chestconfig = null;
	File chestFile;

	@Override
	public void onDisable() {
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("chestaccess")) {
			if ((args[0] != null) &&
			        !(listen.getChest((Player) sender, args[0]) == null)) {
				String chest = listen.getChest((Player) sender, args[0]);
				String[] loc = chest.split("`");
				sender.sendMessage(loc[0]);
				sender.sendMessage(loc[1]);
				sender.sendMessage(loc[2]);
				sender.sendMessage(loc[3]);
				sender.sendMessage(loc[4]);

				int x = Integer.parseInt(loc[0]);
				int y = Integer.parseInt(loc[1]);
				int z = Integer.parseInt(loc[2]);
				World world = Bukkit.getWorld(loc[3]);
				
				Inventory inv = listen.getChestInventory(x, y, z, world);
				Player player = (Player) sender;
				player.openInventory(inv);
				
				return true;
			}
			return true;
		}

		return false;
	}
}