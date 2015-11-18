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
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
			String name = "_" + e.getLine(2) + "_";
			String chestLocation = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName();
			String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

			e.getPlayer().sendMessage(fileName);

			plugin.chestFile = new File(plugin.chests, fileName + ".yml");
			File chestConfig = plugin.chestFile;
			plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
			FileConfiguration chest = plugin.chestconfig;
			File chests1 = plugin.chestsFile;
			FileConfiguration chests2 = plugin.chestsConfig;
			if (!chests2.getStringList("Chests").contains(chestLocation)) {
				/*
				 * chest.set("Name", name); chest.set("X", x); chest.set("Y",
				 * y); chest.set("Z", z); chest.set("World",
				 * e.getBlock().getWorld()); chest.set("Users",
				 * e.getPlayer().getUniqueId().toString()); chest.set("Users." +
				 * e.getPlayer().getUniqueId().toString(),
				 * e.getPlayer().getName());
				 */

				List<String> chests = userConfig.getStringList("Chests");
				chests.add(fileName);
				userConfig.set("Chests", chests);

				List<String> chestes = chests2.getStringList("Chests");
				chestes.add(fileName);
				chests2.set("Chests", chestes);

				try {
					chests2.save(chests1);
					userConfig.save(user);
					// chest.save(chestConfig);
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
			else {
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "This chest is already claimed.");
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.SIGN_POST) || e.getBlock().getType().equals(Material.WALL_SIGN) || e.getBlock().getType().equals(Material.SIGN)) {
			Sign sign = (Sign) e.getBlock().getState();
			if (sign.getLine(0).equals("-----Chest-----")
			        && sign.getLine(3).equals("----Access----")) {
				if (sign.getLine(1).equals(e.getPlayer().getName())) {
					int x = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getX();
					int y = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getY();
					int z = e.getBlock().getRelative(getAttachedBlock(e.getBlock())).getZ();
					String sx = Integer.toString(x);
					String sy = Integer.toString(y);
					String sz = Integer.toString(z);
					plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
					File user = plugin.userFile;
					plugin.userconfig = YamlConfiguration.loadConfiguration(user);
					String name = "_" + sign.getLine(2) + "_";
					FileConfiguration userConfig = plugin.userconfig;
					/*
					 * plugin.chestFile = new File(plugin.chests, sx + "`" + sy
					 * + "`" + sz + "`" + e.getBlock().getWorld().getName() +
					 * "`" + name + ".yml"); File chestConfig =
					 * plugin.chestFile; plugin.chestconfig =
					 * YamlConfiguration.loadConfiguration(chestConfig);
					 * FileConfiguration chest = plugin.chestconfig;
					 */
					File chests1 = plugin.chestsFile;
					FileConfiguration chests2 = plugin.chestsConfig;
					String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

					List<String> chests = userConfig.getStringList("Chests");
					chests.remove(fileName);
					userConfig.set("Chests", chests);

					List<String> chestes = chests2.getStringList("Chests");
					chestes.remove(fileName);
					chests2.set("Chests", chestes);
					/*
					 * if (chestConfig.exists()) { chestConfig.delete(); } else
					 * { Bukkit.getServer().getLogger().log(Level.WARNING,
					 * "[Chest_Access] Config file tried to be deleted but it didn't exist in the first place!"
					 * ); Bukkit.getServer().getLogger().log(Level.WARNING,
					 * "[Chest_Access] Config file name: " + sx + "`" + sy + "`"
					 * + sz + "`" + e.getBlock().getWorld().getName() + "`" +
					 * name); }
					 */
					try {
						chests2.save(chests1);
						userConfig.save(user);

					}
					catch (IOException e1) {
					}
					e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
					e.setCancelled(false);
				}
				else {
					e.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't break this Sign!!!");
					e.setCancelled(true);
				}
			}
			else {
				e.setCancelled(false);
			}
		}
		else {
			e.setCancelled(false);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (e.getInventory().getName().contains("Chest Access - ")) {
			Inventory inv = e.getInventory();
			String[] losc = inv.getName().split("`");
			int x = Integer.parseInt(losc[1]);
			int y = Integer.parseInt(losc[2]);
			int z = Integer.parseInt(losc[3]);

			World world = Bukkit.getWorld(losc[4]);
			Location loc = new Location(world, x, y, z);

			Block block = loc.getBlock();
			Chest chest1 = (Chest) block.getState();
			Block block2 = block.getRelative(getAttachedBlock(block));
			Chest chest2 = (Chest) block2.getState();

			for (ItemStack i : inv.getContents()) {
				chest1.getInventory().addItem(i);
				inv.removeItem(i);
			}
			for(ItemStack i : inv.getContents()){
				chest2.getInventory().addItem(i);
			}
		}
	}

	public String getChest(Player player, String string) {
		String uuid = player.getUniqueId().toString();

		plugin.userFile = new File(plugin.users, uuid + ".yml");
		File user = plugin.userFile;
		plugin.userconfig = YamlConfiguration.loadConfiguration(user);
		FileConfiguration userConfig = plugin.userconfig;

		List<String> chests = userConfig.getStringList("Chests");
		for (String i : chests) {
			if (i.contains(string)) {
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

	public Inventory getChestInventory(int x, int y, int z, World world) {
		Inventory inv = null;
		Location loc = new Location(world, x, y, z);
		Block block = loc.getBlock();
		Chest chest = (Chest) block.getState();

		if (block.getType().equals(Material.CHEST)) {
			Inventory chestInventory = ((Chest) block.getState()).getInventory();

			if (chestInventory instanceof DoubleChestInventory) {
				DoubleChest c = new DoubleChest((DoubleChestInventory) chestInventory);
				inv = c.getInventory();
			}

			else {
				inv = chest.getBlockInventory();
			}
			return inv;
		}

		return inv;
	}

	public Block getDoubleUnloadedChest(int x, int y, int z, World world) {
		Location loc = new Location(world, x, y, z);
		Block block = loc.getBlock();
		BlockFace face = getAttachedBlock(block);
		if (face == null) {
			return null;
		}
		else {
			Block dc = block.getRelative(face);
			return dc;
		}
	}

}