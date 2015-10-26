package com.gbezter.MoblieChest;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	File folder = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "User's Chest");
	File chests;
	Listen listen = new Listen(this);

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(listen, this);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	@Override
	public void onDisable() {
	}

	FileConfiguration userconfig = null;
	File userFile;

	public Location getChest(String name) {
		Location loc = null;
		if (userconfig.contains(name)) {
			double x = (double) userconfig.getInt("Chests." + name + ".X");
			double y = (double) userconfig.getInt("Chests." + name + ".Y");
			double z = (double) userconfig.getInt("Chests." + name + ".Z");
			World w = (World) userconfig.get("Chests." + name + ".World");
			loc.setX(x);
			loc.setY(y);
			loc.setZ(z);
			loc.setWorld(w);
			return loc;
		}
		else {
			return null;
		}

	}

	public Inventory getChestInventory(Block chest) {
		if (chest.getType() == Material.CHEST) {
			Chest c = (Chest) chest.getState();
			if (c.getInventory().getHolder() instanceof DoubleChest) {
				DoubleChest dc = (DoubleChest) c.getInventory().getHolder();
				return dc.getInventory();
			}
			else {
				return c.getBlockInventory();
			}
		}
		else {
			return null;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
	        String[] args) {
		if (cmd.getName().equalsIgnoreCase("chestaccess")) {
			if (sender.hasPermission("ca.main")) {
				String name = args[0];
				Player player = (Player) sender;
				String playerT = player.getPlayer().getName();
				if (args[0] == null) {
					sender.sendMessage(ChatColor.DARK_GREEN + "Command WIP");
					sender.sendMessage(ChatColor.DARK_GREEN
					        + "Use /ca [Chest Name]");
					return true;
				}
				if (!(args[0] == null)) {
					try {
						userFile = new File(folder, playerT + ".yml");
						if (userconfig.contains(name)) {
							sender.sendMessage(ChatColor.DARK_GREEN + "Opening"
							        + name);
							Block chest = (Block) getChest(name);
							Inventory i = getChestInventory(chest);
							player.openInventory(i);
							return true;
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (!(userFile == null)) {

						sender.sendMessage(ChatColor.RED + userFile.getPath());
						return true;
					}
				}
			}
		}
		return false;
	}
}