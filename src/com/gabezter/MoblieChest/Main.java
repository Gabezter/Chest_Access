package com.gabezter.MoblieChest;

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
import org.bukkit.configuration.file.YamlConfiguration;
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
		} else {
			return null;
		}

	}

	public Inventory getChestInventory(Block chest) {
		if (chest.getType() == Material.CHEST) {
			Chest c = (Chest) chest.getState();
			if (c.getInventory().getHolder() instanceof DoubleChest) {
				DoubleChest dc = (DoubleChest) c.getInventory().getHolder();
				return dc.getInventory();
			} else {
				return c.getBlockInventory();
			}
		} else {
			return null;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("chestaccess")) {
			if (sender.hasPermission("ca.main")) {
				String name = args[0];
				if (args[0].equalsIgnoreCase("")) {
					userFile = new File(folder, sender.getName() + ".yml");
					userconfig = YamlConfiguration.loadConfiguration(userFile);
					if (userconfig.getConfigurationSection("Chests")
							.getKeys(false).size() == 0) {
						sender.sendMessage(ChatColor.GOLD
								+ "You have 0 chests!!!");
					} else if (userconfig
							.getConfigurationSection("Chests").getKeys(false)
							.size() > 0) {
						sender.sendMessage(ChatColor.GOLD
								+ "You have "
								+ userconfig
										.getKeys(true).size() + "chests.");
						sender.sendMessage(ChatColor.GOLD
								+ "You have "
								+ userconfig.getKeys(
										false));
						return true;
					}

					else if (userconfig.contains(name)) {
						Block chest = (Block) getChest(name);
						Inventory i = getChestInventory(chest);
						Player player = (Player) sender;
						player.openInventory(i);
						return true;
					}
				}
				return true;
			}
		}
		return false;
	}
}