package com.gabezter.MoblieChest;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	File users = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "User's Chest");
	File chests = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "Chests");

	Listen listen = new Listen(this);
	ChestFile chest;
	UserFile user;

	@Override
	public void onEnable() {
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

	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {

		return false;
	}
}