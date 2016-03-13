package com.gabezter.MoblieChest;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	File users = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "User's Chest");
	File chests = new File(this.getDataFolder().getAbsolutePath()
	        + File.separator + "Chests");

	Listen listen = new Listen(this);

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
			if (args[0].equalsIgnoreCase("open")) {
				if (!(listen.getChest((Player) sender, args[1]) == null)) {
					String chest = listen.getChest((Player) sender, args[1]);
					String[] loc = chest.split("`");
					int x = Integer.parseInt(loc[0]);
					int y = Integer.parseInt(loc[1]);
					int z = Integer.parseInt(loc[2]);
					String sx = loc[0];
					String sy = loc[1];
					String sz = loc[0];
					World world = Bukkit.getWorld(loc[3]);

					Location loc1 = new Location(world, x, y, z);
					loc1.getChunk().load(true);
					if (loc1.getChunk().isLoaded() == false) {
						Block block = loc1.getBlock();
						Block block2 = listen.getDoubleUnloadedChest(x, y, z, world);

						Inventory invs = Bukkit.getServer().createInventory(null, 54, "Chest Access - `" + sx + "," + sy + "," + sz + "," + world.toString() + "` " + args[0]);
						Chest chest1 = (Chest) block.getState();
						Chest chest2 = (Chest) block2.getState();

						for (ItemStack i : chest1.getInventory().getContents()) {
							invs.addItem(i);
						}
						for (ItemStack i : chest2.getInventory().getContents()) {
							invs.addItem(i);
						}

						Player player = (Player) sender;
						player.openInventory(invs);
						return true;
					}
					Inventory inv = listen.getChestInventory(x, y, z, world);
					Player player = (Player) sender;
					player.openInventory(inv);

					return true;
				}
				else {
					sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not a valid chest for you to open.");
					return true;
				}
			}
			Player player = (Player) sender;
			if (args[0].equalsIgnoreCase("list")) {
				userFile = new File(users, player.getUniqueId().toString() + ".yml");
				File user = userFile;
				userconfig = YamlConfiguration.loadConfiguration(user);

				List<String> chests = userconfig.getStringList("Chests");
				List<String> list = null;
				for (String s : chests) {
					String[] cs = s.split("`");
					cs[4].replace("_", "");
					String[] st;
					st = {st,cs[4]};
				}
				
				player.sendMessage(st);
				return true;
			}
		}

		return false;
	}
}