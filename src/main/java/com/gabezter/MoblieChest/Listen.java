package com.gabezter.MoblieChest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

public class Listen implements Listener {

	Main plugin;
	Methods method;

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

		plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
		File user = plugin.userFile;
		plugin.userconfig = YamlConfiguration.loadConfiguration(user);

		FileConfiguration userConfig = plugin.userconfig;

		if ((getAttachedBlock(e.getBlock()) != null)
		        && e.getLine(0).equalsIgnoreCase("Chest Access")
		        && e.getLine(1).equalsIgnoreCase(e.getPlayer().getName())
		        && !(e.getLine(2).equals(""))) {

			int x = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getX();
			int y = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getY();
			int z = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getZ();
			String sx = Integer.toString(x);
			String sy = Integer.toString(y);
			String sz = Integer.toString(z);
			String name = e.getLine(2);
			String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

			e.getPlayer().sendMessage(fileName);

			plugin.chestFile = new File(plugin.chests, fileName + ".yml");
			File chestConfig = plugin.chestFile;
			plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
			FileConfiguration chest = plugin.chestconfig;
			File chests1 = plugin.chestsFile;
			FileConfiguration chests2 = plugin.chestsConfig;

			chest.set("Name", name);
			chest.set("X", x);
			chest.set("Y", y);
			chest.set("Z", z);
			chest.set("World", e.getBlock().getWorld());
			chest.set("Users", e.getPlayer().getUniqueId().toString());
			chest.set("Users." + e.getPlayer().getUniqueId().toString(), e.getPlayer().getName());

			List<String> chests = userConfig.getStringList("Chests");
			chests.add(fileName);
			userConfig.set("Chests", chests);

			List<String> chestes = chests2.getStringList("Chests");
			chestes.add(fileName);
			chests2.set("Chests", chestes);

			try {
				chests2.save(chests1);
				userConfig.save(user);
				chest.save(chestConfig);
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
		if (e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.WALL_SIGN) || e.getBlock().getType().equals(Material.SIGN)) {
			Sign sign = (Sign) e.getBlock().getState();
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

				File chests1 = plugin.chestsFile;
				FileConfiguration chests2 = plugin.chestsConfig;

				userConfig.getStringList("Chests").remove(sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
				chests2.getStringList("Chests").remove(sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);

				if (chestConfig.exists()) {
					chestConfig.delete();
				}
				else {
					Bukkit.getServer().getLogger().log(null, "Config file tried to be deleted but it didn't exist in the first place!");
					Bukkit.getServer().getLogger().log(null, "Config file name: " + sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
				}
				try {
					chests2.save(chests1);
					userConfig.save(user);
					chest.save(chestConfig);
				}
				catch (IOException e1) {
				}
				e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
				e.setCancelled(false);
			}
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't break this Sign!!!");
			e.setCancelled(true);
		}
		e.setCancelled(false);
	}

	public String getChest(Player player, String string) {
		String uuid = player.getUniqueId().toString();
		
		plugin.userFile = new File(plugin.users, uuid + ".yml");
		File user = plugin.userFile;
		plugin.userconfig = YamlConfiguration.loadConfiguration(user);
		FileConfiguration userConfig = plugin.userconfig;
		
		List<String> chests = userConfig.getStringList("Chests");
		for(String i : chests){
			if(i.contains(string)){
				return i;
			}
		}
		return null;
	}

	public BlockFace getAttachedBlock(Block sb) {
		BlockFace[] blockFaces = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
		for (BlockFace bf : blockFaces) {
			Block bu = sb.getRelative(bf);
			if ((bu.getType().equals(Material.CHEST))) {
				return bf;
			}
		}
		return null;
	}

	public Inventory getChestInventory(int x, int y, int z, World world){
		Inventory inv = null;
		Location loc = new Location(world, x, y, z);
		if(loc.getBlock().getType().equals(Material.CHEST)){
			Block block = loc.getBlock();
			Chest chest = (Chest) block.getState();
			inv = chest.getBlockInventory();
			return inv;
		}
		
		return inv;
	}

}