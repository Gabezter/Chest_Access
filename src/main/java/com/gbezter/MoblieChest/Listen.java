package com.gbezter.MoblieChest;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
		plugin.userFile = new File(plugin.folder, e.getPlayer().getName()
		        + ".yml");
		if (!plugin.userFile.exists()) {
			try {
				plugin.userFile.createNewFile();
				plugin.userconfig = YamlConfiguration
				        .loadConfiguration(plugin.userFile);
				plugin.userconfig.addDefault("Chests", " ");
				plugin.userconfig.options().copyDefaults(true);
				plugin.userconfig.createSection("Chests");
				plugin.userconfig.save(plugin.userFile);
			}
			catch (IOException ec) {
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
				plugin.userconfig.set(var + ".X", e.getBlock().getLocation()
				        .getBlockX());
				plugin.userconfig.set(var + ".Y", e.getBlock().getLocation()
				        .getBlockY());
				plugin.userconfig.set(var + ".Z", e.getBlock().getLocation()
				        .getBlockZ());
				plugin.userconfig.set(var + ".World", e.getBlock().getWorld()
				        .getName());
				try {
					plugin.userconfig.save(plugin.userFile);
				}
				catch (IOException e1) {
				}
				e.setLine(0, "~~~~~~~~~~~~~");
				e.setLine(1, e.getPlayer().getName());
				e.setLine(3, "~~~~~~~~~~~~~");
				e.getPlayer().sendMessage(
				        ChatColor.DARK_GREEN + "Chest linked Successful!!");
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		Block block = e.getBlock();
		World world = block.getWorld();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		String sworld = world.getName();
		plugin.userFile = new File(plugin.folder, player.getName() + ".yml");
		plugin.userconfig = YamlConfiguration
		        .loadConfiguration(plugin.userFile);
		if (this.chestName(x, y, z, sworld, player) == null) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.DARK_RED
			        + "Chest not find to be yours!!!! You may not destory it!!!!");
		}
		else if (!(this.chestName(x, y, z, sworld, player) == null)) {
			String name = chestName(x, y, z, sworld, player);
			plugin.userconfig.set(name, null);
			try {
				plugin.userconfig.save(plugin.userFile);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			e.setCancelled(false);
		}
		e.setCancelled(false);
	}

	public String chestName(int x, int y, int z, String sworld, Player player) {
		plugin.userFile = new File(plugin.folder, player.getName() + ".yml");
		plugin.userconfig = YamlConfiguration
		        .loadConfiguration(plugin.userFile);
		for (Object elm : plugin.userconfig.getList("Chests")) {
			String name = (String) elm;
			int cx = plugin.userconfig.getInt("Chests." + name + ".x");
			int cy = plugin.userconfig.getInt("Chests." + name + ".y");
			int cz = plugin.userconfig.getInt("Chests." + name + ".z");
			String cworld = plugin.userconfig.getString("Chests." + name
			        + ".World");
			if ((cx == x) && (cz == z) && (cy == y) && (cworld == sworld)) {
				return name;
			}
		}
		return null;
	}
}