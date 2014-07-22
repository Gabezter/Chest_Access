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
				plugin.userconfig.addDefault("Chests", "");
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
		Block block = e.getBlock();
		org.bukkit.block.Sign s = (org.bukkit.block.Sign)block.getState();
		org.bukkit.material.Sign data = (org.bukkit.material.Sign)s.getData();
		Block b = block.getRelative(data.getAttachedFace());
		if (b.getType() == Material.CHEST) {
			if (s.getLine(1).contains(e.getPlayer().getName())
					&& !(s.getLine(2).contains(""))) {
				e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest linked Successful!!"); 
				plugin.config.getConfig(e.getPlayer().getName()).set("Chests.",
						s.getLine(2));
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + s.getLine(2) + ".X",
						e.getBlock().getLocation().getBlockX());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + s.getLine(2) + ".Y",
						e.getBlock().getLocation().getBlockY());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + s.getLine(2) + ".Z",
						e.getBlock().getLocation().getBlockZ());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + s.getLine(2) + ".World",
						e.getBlock().getLocation().getWorld());
				plugin.config.saveConfig(e.getPlayer().getName());
			}
		}
	}

}