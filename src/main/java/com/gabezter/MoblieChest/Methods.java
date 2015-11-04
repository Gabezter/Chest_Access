package com.gabezter.MoblieChest;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class Methods {
	Main plugin;
	
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

	public DoubleChestInventory getDoubleChest(int x, int y, int z, World world){
		return null;
		
	}
}
