package com.gabezter.MoblieChest;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;

public class Listen implements Listener {

	Main plugin;

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		plugin.userFile = new File(plugin.folder, e.getPlayer().getName()
				+ ".yml");
		if (!plugin.userFile.exists()) {
			try {
				plugin.userFile.createNewFile();
				plugin.userconfig = YamlConfiguration
						.loadConfiguration(plugin.userFile);
				plugin.userconfig.addDefault("Chests", null);
				plugin.userconfig.save(plugin.userFile);
			} catch (IOException ec) {
				Bukkit.getServer()
						.getLogger()
						.info("Could not create or save"
								+ e.getPlayer().getName() + ".yml");
			}
		}
		plugin.config.getConfig(e.getPlayer().getName()).addDefault("Chests.",null);
	}

	@EventHandler
	public void signRead(SignChangeEvent e) {
		Sign sign = (Sign) e.getBlock().getState();
		Block attached = e.getBlock().getRelative(
				getAttachedBlock(e.getBlock()));
		if (attached.getType() == Material.CHEST) {
			if (sign.getLine(1).contains(e.getPlayer().getName())
					&& !(sign.getLine(2).contains(""))) {
				plugin.config.getConfig(e.getPlayer().getName()).set("Chests.",
						sign.getLine(2));
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + sign.getLine(2) + ".X",
						e.getBlock().getLocation().getBlockX());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + sign.getLine(2) + ".Y",
						e.getBlock().getLocation().getBlockY());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + sign.getLine(2) + ".Z",
						e.getBlock().getLocation().getBlockZ());
				plugin.config.getConfig(e.getPlayer().getName()).set(
						"Chests." + sign.getLine(2) + ".World",
						e.getBlock().getLocation().getWorld());
				plugin.config.saveConfig(e.getPlayer().getName());
			}
		}
	}

	public static BlockFace getAttachedBlock(Block b) {
		MaterialData m = b.getState().getData();
		BlockFace face = BlockFace.DOWN;
		if (m instanceof Attachable) {
			face = ((Attachable) m).getAttachedFace();
		}
		return face;
	}
}