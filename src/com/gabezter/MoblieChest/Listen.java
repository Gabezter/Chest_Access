package com.gabezter.MoblieChest;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listen implements Listener {

	Main plugin;

	public Listen(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		plugin.userFile = new File(plugin.folder, e.getPlayer().getName()
				+ ".yml");
		if (!plugin.userFile.exists()) {
			try {
				plugin.userFile.createNewFile();
				plugin.userconfig = YamlConfiguration
						.loadConfiguration(plugin.userFile);
				plugin.userconfig.addDefault("Chests", " ");
				plugin.userconfig.options().copyDefaults(true);
				plugin.userconfig.save(plugin.userFile);
			} catch (IOException ec) {
				Bukkit.getServer()
						.getLogger()
						.info("Could not create or save"
								+ e.getPlayer().getName() + ".yml");
			}
		}
	}

	@EventHandler
	public void signRead(SignChangeEvent e) {
		plugin.userFile = new File(plugin.folder, e.getPlayer().getName()
				+ ".yml");
		Block block = e.getBlock();
		org.bukkit.block.Sign s = (org.bukkit.block.Sign) block.getState();
		org.bukkit.material.Sign data = (org.bukkit.material.Sign) s.getData();
		Block b = block.getRelative(data.getAttachedFace());
		if (b.getType() == Material.CHEST) {
			if (e.getLine(1).equalsIgnoreCase(e.getPlayer().getName())
					&& !(e.getLine(2).equals(""))) {
				String var = "Chests." + e.getLine(2);
				plugin.userconfig = YamlConfiguration
						.loadConfiguration(plugin.userFile);
				plugin.userconfig.set(var + ".X",
						e.getBlock().getLocation().getBlockX());
				plugin.userconfig.set(var + ".Y",
						e.getBlock().getLocation().getBlockY());
				plugin.userconfig.set(var + ".Z",
						e.getBlock().getLocation().getBlockZ());
				plugin.userconfig.set(var + ".World",
						e.getBlock().getWorld().getName());
				try {
					plugin.userconfig.save(plugin.userFile);
				} catch (IOException e1) {}
				e.setLine(0, "~~~~~~~~~~~~~");
				e.setLine(1, e.getPlayer().getName());
				e.setLine(3, "~~~~~~~~~~~~~");
				e.getPlayer().sendMessage(
						ChatColor.DARK_GREEN + "Chest linked Successful!!");
			}
		}
	}

}