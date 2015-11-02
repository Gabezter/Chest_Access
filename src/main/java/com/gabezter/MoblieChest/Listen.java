package com.gabezter.MoblieChest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listen implements Listener {

	Main plugin;

	public Listen(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
		File user = plugin.userFile;
		FileConfiguration userConfig = plugin.userconfig;
		if (!user.exists()) {
			try {
				user.createNewFile();
				userConfig = YamlConfiguration
				        .loadConfiguration(user);
				userConfig.addDefault("Chests", " ");
				userConfig.options().copyDefaults(true);
				userConfig.set("Name", e.getPlayer().getName());
				userConfig.save(user);
			}
			catch (IOException ec) {
				Bukkit.getServer()
				        .getLogger()
				        .info("Could not create or save"
				                + e.getPlayer().getUniqueId().toString() + ".yml");
			}
		}
	}

	@EventHandler
	public void signRead(SignChangeEvent e) {
		int x = e.getBlock().getX();
		int y = e.getBlock().getY();
		int z = e.getBlock().getZ();
		String sx = Integer.toString(x);
		String sy = Integer.toString(y);
		String sz = Integer.toString(z);

		plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
		File user = plugin.userFile;
		plugin.userconfig = YamlConfiguration.loadConfiguration(user);

		FileConfiguration userConfig = plugin.userconfig;

		if (e.getLine(0).equalsIgnoreCase("Chest Access")
		        && e.getLine(1).equalsIgnoreCase(e.getPlayer().getName())
		        && !(e.getLine(2).equals(""))) {
			String name = e.getLine(2);
			plugin.chestFile = new File(plugin.chests, sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name + ".yml");
			File chestConfig = plugin.chestFile;
			plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
			FileConfiguration chest = plugin.chestconfig;
			chest.set("Name", name);
			chest.set("X", x);
			chest.set("Y", y);
			chest.set("Z", z);
			chest.set("World", e.getBlock().getWorld());
			chest.set("Users", e.getPlayer().getUniqueId().toString());
			chest.set("Users." + e.getPlayer().getUniqueId().toString(), e.getPlayer().getName());
			try {
				chest.save(chestConfig);
			}
			catch (IOException e2) {
			}

			List<String> chests = userConfig.getStringList("Chests");
			chests.add(sx + "`" + sy + "`" + sz + "`" + name);
			userConfig.set("Chests", chests);
			try {
				userConfig.save(user);
			}
			catch (IOException e1) {
				Bukkit.getServer()
				        .getLogger()
				        .info("Could not save"
				                + e.getPlayer().getUniqueId().toString() + ".yml");
			}

			e.setLine(0, "-----Chest-----");
			e.setLine(1, e.getPlayer().getName());
			e.setLine(3, "----Access----");
			e.getPlayer().sendMessage(
			        ChatColor.DARK_GREEN + "Chest linked Successful!!");
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		int x = e.getBlock().getX();
		int y = e.getBlock().getY();
		int z = e.getBlock().getZ();
		String sx = Integer.toString(x);
		String sy = Integer.toString(y);
		String sz = Integer.toString(z);
		e.getPlayer().sendMessage(e.getBlock().toString());
		if (e.getBlock().getType().equals(Material.SIGN_POST)) {
			Sign sign = (Sign) e.getBlock();
			e.getPlayer().sendMessage(sign.getLine(1));
			if (sign.getLine(1).equals(e.getPlayer().getName())) {
				plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
				File user = plugin.userFile;
				plugin.userconfig = YamlConfiguration.loadConfiguration(user);

				String name = sign.getLine(2);
				FileConfiguration userConfig = plugin.userconfig;
				plugin.chestFile = new File(plugin.chests, sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name + ".yml");
				File chestConfig = plugin.chestFile;
				plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
				FileConfiguration chest = plugin.chestconfig;

				userConfig.getStringList("Chests").remove(sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
				chestConfig.delete();
				try {
					userConfig.save(user);
					chest.save(chestConfig);
				}
				catch (IOException e1) {
				}
				e.getPlayer().sendMessage(
				        ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
				e.setCancelled(false);
			}
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't break this Sign!!!");
			e.setCancelled(true);
		}
		e.setCancelled(false);
	}

	public String chestName(int x, int y, int z, String sworld, Player player) {

		return null;
	}
}