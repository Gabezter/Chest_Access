package com.gabezter.MoblieChest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

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
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listen implements Listener {

	Main plugin;

	public Listen(Main plugin) {
		this.plugin = plugin;
	}

	// TODO Listeners

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
		File user = plugin.userFile;
		FileConfiguration userConfig = plugin.userconfig;
		if (!user.exists()) {
			try {
				user.createNewFile();
				userConfig = YamlConfiguration.loadConfiguration(user);
				userConfig.addDefault("Chests", " ");
				userConfig.options().copyDefaults(true);
				userConfig.set("Name", e.getPlayer().getName());
				userConfig.save(user);
			}
			catch (IOException ec) {
				Bukkit.getServer().getLogger().info("Could not create or save" + e.getPlayer().getUniqueId().toString() + ".yml");
			}
		}
		else if (user.exists()) {
			userConfig = YamlConfiguration.loadConfiguration(user);
			if (!userConfig.get("Name").equals(e.getPlayer().getName())) {
				try {
					userConfig.set("Name", e.getPlayer().getName());
					plugin.signNameReplace(e.getPlayer().getName(), e.getPlayer().getUniqueId());
					userConfig.save(user);
				}
				catch (IOException e1) {
					Bukkit.getServer().getLogger().info("Could not save" + e.getPlayer().getUniqueId().toString() + ".yml");
				}
			}
		}
	}

	@EventHandler
	public void signRead(SignChangeEvent e) {

		plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
		File user = plugin.userFile;
		plugin.userconfig = YamlConfiguration.loadConfiguration(user);

		FileConfiguration userConfig = plugin.userconfig;

		Block block = e.getBlock();
		Material bm = block.getType();

		if (bm.equals(Material.WALL_SIGN)) {
			Block block2 = block.getRelative(getAttachedBlock(block));
			if ((getAttachedBlock(block) != null) && e.getLine(0).equalsIgnoreCase("Chest Access") && e.getLine(1).equalsIgnoreCase(e.getPlayer().getName()) && !(e.getLine(2).equals("")) && (isChestClaimed(block.getRelative(getAttachedBlock(block))) == false) && (getAttachedBlock(block2) == null || isChestClaimed(block2.getRelative(getAttachedBlock(block2))) == false)) {

				int x = block.getRelative(getAttachedBlock(block)).getX();
				int y = block.getRelative(getAttachedBlock(block)).getY();
				int z = block.getRelative(getAttachedBlock(block)).getZ();
				String sx = Integer.toString(x);
				String sy = Integer.toString(y);
				String sz = Integer.toString(z);
				String name = "_" + e.getLine(2) + "_";
				String chestLocation = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName();
				String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

				plugin.chestFile = new File(plugin.chests, fileName + ".yml");
				File chestConfig = plugin.chestFile;
				plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
				// FileConfiguration chest = plugin.chestconfig;
				File chests1 = plugin.chestsFile;
				FileConfiguration chests2 = plugin.chestsConfig;
				if (!chests2.getStringList("Chests").contains(chestLocation)) {
					/*
					 * chest.set("Name", name); chest.set("X", x);
					 * chest.set("Y", y); chest.set("Z", z); chest.set("World",
					 * e.getBlock().getWorld()); chest.set("Users",
					 * e.getPlayer().getUniqueId().toString());
					 * chest.set("Users." +
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
						Bukkit.getServer().getLogger().info("Could not save" + e.getPlayer().getUniqueId().toString() + ".yml");
					}

					e.setLine(0, "-----Chest-----");
					e.setLine(1, e.getPlayer().getName());
					e.setLine(3, "----Access----");
					e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest linked Successful!!");
				}
				else {
					e.getPlayer().sendMessage(ChatColor.DARK_RED + "This chest is already claimed.");
				}
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType().equals(Material.WALL_SIGN)) {
			Sign sign = (Sign) e.getBlock().getState();
			e.setCancelled(true);
			if (sign.getLine(0).equals("-----Chest-----") && sign.getLine(3).equals("----Access----")) {
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

					plugin.chestFile = new File(plugin.chests, sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name + ".yml");
					File chestConfig = plugin.chestFile;
					plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
					// FileConfiguration chest = plugin.chestconfig;

					File chests1 = plugin.chestsFile;
					FileConfiguration chests2 = plugin.chestsConfig;
					String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

					List<String> chests = userConfig.getStringList("Chests");
					chests.remove(fileName);
					userConfig.set("Chests", chests);

					List<String> chestes = chests2.getStringList("Chests");
					chestes.remove(fileName);
					chests2.set("Chests", chestes);

					if (chestConfig.exists()) {
						chestConfig.delete();
					}
					else {
						Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file tried to be deleted but it didn't exist in the first place!");
						Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file name: " + sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
					}

					try {
						chests2.save(chests1);
						userConfig.save(user);

					}
					catch (IOException e1) {
					}
					e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
					e.getBlock().breakNaturally();
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
		else if (e.getBlock().getType().equals(Material.CHEST)) {
			e.setCancelled(true);
			Location loc = e.getBlock().getLocation();
			for (int j = 0; j >= 3; j++) {
				Location loc2 = loc;
				if (j == 0) {
					loc2.add(1, 0, 0);
					if (loc2.getBlock().getType().equals(Material.WALL_SIGN)) {
						Sign sign = (Sign) loc2.getBlock();
						if (sign.getLine(0).equals("-----Chest-----") && sign.getLine(3).equals("----Access----")) {
							if (sign.getLine(1).equals(e.getPlayer().getName())) {
								int x = loc2.getBlockX();
								int y = loc2.getBlockY();
								int z = loc2.getBlockZ();

								String sx = Integer.toString(x);
								String sy = Integer.toString(y);
								String sz = Integer.toString(z);

								plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
								File user = plugin.userFile;
								plugin.userconfig = YamlConfiguration.loadConfiguration(user);
								String name = "_" + sign.getLine(2) + "_";
								FileConfiguration userConfig = plugin.userconfig;

								plugin.chestFile = new File(plugin.chests, sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name + ".yml");
								File chestConfig = plugin.chestFile;
								plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
								// FileConfiguration chest = plugin.chestconfig;

								File chests1 = plugin.chestsFile;
								FileConfiguration chests2 = plugin.chestsConfig;
								String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

								List<String> chests = userConfig.getStringList("Chests");
								chests.remove(fileName);
								userConfig.set("Chests", chests);

								List<String> chestes = chests2.getStringList("Chests");
								chestes.remove(fileName);
								chests2.set("Chests", chestes);

								if (chestConfig.exists()) {
									chestConfig.delete();
								}
								else {
									Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file tried to be deleted but it didn't exist in the first place!");
									Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file name: " + sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
								}

								try {
									chests2.save(chests1);
									userConfig.save(user);

								}
								catch (IOException e1) {
								}
								e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
								e.getBlock().breakNaturally();
							}
							else {
								e.getPlayer().sendMessage(plugin.logo + ChatColor.DARK_RED + "This chest does not belong to you!!!!!");
								e.setCancelled(true);
							}

							// TODO Build Notes
							// TODO Deleting config chest upon chest break
							// TODO Only designed to test for one chest
							// currently.

						}
						else if (loc2.getBlock().getType().equals(Material.CHEST)) {
							for (int k = 0; k >= 3; k++) {
								Location loc3 = loc;
								if (k == 0) {
									loc3.add(1, 0, 0);
									if (loc3.getBlock().getType().equals(Material.WALL_SIGN)) {
										Sign sign2 = (Sign) loc3.getBlock();
										if (sign2.getLine(0).equals("-----Chest-----") && sign2.getLine(3).equals("----Access----")) {
											if (sign2.getLine(1).equals(e.getPlayer().getName())) {

												int x = loc3.getBlockX();
												int y = loc3.getBlockY();
												int z = loc3.getBlockZ();

												String sx = Integer.toString(x);
												String sy = Integer.toString(y);
												String sz = Integer.toString(z);

												plugin.userFile = new File(plugin.users, e.getPlayer().getUniqueId().toString() + ".yml");
												File user = plugin.userFile;
												plugin.userconfig = YamlConfiguration.loadConfiguration(user);
												String name = "_" + sign2.getLine(2) + "_";
												FileConfiguration userConfig = plugin.userconfig;

												plugin.chestFile = new File(plugin.chests, sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name + ".yml");
												File chestConfig = plugin.chestFile;
												plugin.chestconfig = YamlConfiguration.loadConfiguration(chestConfig);
												// FileConfiguration chest =
												// plugin.chestconfig;

												File chests1 = plugin.chestsFile;
												FileConfiguration chests2 = plugin.chestsConfig;
												String fileName = sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name;

												List<String> chests = userConfig.getStringList("Chests");
												chests.remove(fileName);
												userConfig.set("Chests", chests);

												List<String> chestes = chests2.getStringList("Chests");
												chestes.remove(fileName);
												chests2.set("Chests", chestes);

												if (chestConfig.exists()) {
													chestConfig.delete();
												}
												else {
													Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file tried to be deleted but it didn't exist in the first place!");
													Bukkit.getServer().getLogger().log(Level.WARNING, "[Chest_Access] Config file name: " + sx + "`" + sy + "`" + sz + "`" + e.getBlock().getWorld().getName() + "`" + name);
												}

												try {
													chests2.save(chests1);
													userConfig.save(user);

												}
												catch (IOException e1) {
												}
												e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Chest unlinked Successful!!");
												e.getBlock().breakNaturally();
											}
											else {
												e.getPlayer().sendMessage(plugin.logo + ChatColor.DARK_RED + "This chest does not belong to you!!!!!");
												e.setCancelled(true);
											}
										}
									}
								}
							}
						}
					}
				}
				if (j == 1) {
					loc2.add(-1, 0, 0);
				}
				if (j == 2) {
					loc2.add(0, 0, 1);
				}
				if (j == 3) {
					loc2.add(0, 0, -1);
				}

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
			for (ItemStack i : inv.getContents()) {
				chest2.getInventory().addItem(i);
			}
		}
	}

	@EventHandler
	public void ChestOpen(InventoryOpenEvent e) {
		if (e.getInventory().getHolder() instanceof Chest) {
			Chest chest = (Chest) e.getInventory().getHolder();
			Block block = chest.getBlock();
			Location loc = block.getLocation();
			Player player = (Player) e.getPlayer();
			UUID uuid = player.getUniqueId();

			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			World world = loc.getWorld();

			String sx = Integer.toString(x);
			String sy = Integer.toString(y);
			String sz = Integer.toString(z);
			String sworld = world.toString();

			String chestLocation = sx + "`" + sy + "`" + sz + "`" + sworld;

			plugin.userFile = new File(plugin.users, uuid.toString() + ".yml");
			File user = plugin.userFile;
			plugin.userconfig = YamlConfiguration.loadConfiguration(user);
			FileConfiguration userConfig = plugin.userconfig;
			FileConfiguration chests2 = plugin.chestsConfig;

			if (chests2.getStringList("Chests").contains(chestLocation)) {
				if (userConfig.getStringList("Chests").contains(chestLocation)) {
					e.setCancelled(false);
				}
				else {
					e.setCancelled(true);
					player.sendMessage(plugin.logo + ChatColor.DARK_RED + "This is not your chest!!!");
				}
			}

		}
		else if (e.getInventory().getHolder() instanceof DoubleChest) {
			DoubleChest chest = (DoubleChest) e.getInventory().getHolder();
			Chest chestL = (Chest) chest.getLeftSide();
			Chest chestR = (Chest) chest.getRightSide();
			Block blockL = chestL.getBlock();
			Block blockR = chestR.getBlock();
			Location locL = blockL.getLocation();
			Location locR = blockR.getLocation();
			Player player = (Player) e.getPlayer();
			UUID uuid = player.getUniqueId();

			int xR = locR.getBlockX();
			int yR = locR.getBlockY();
			int zR = locR.getBlockZ();
			World worldR = locR.getWorld();
			String sxR = Integer.toString(xR);
			String syR = Integer.toString(yR);
			String szR = Integer.toString(zR);
			String sworldR = worldR.toString();
			String chestLocationR = sxR + "`" + syR + "`" + szR + "`" + sworldR;

			int xL = locL.getBlockX();
			int yL = locL.getBlockY();
			int zL = locL.getBlockZ();
			World worldL = locL.getWorld();
			String sxL = Integer.toString(xL);
			String syL = Integer.toString(yL);
			String szL = Integer.toString(zL);
			String sworldL = worldL.toString();
			String chestLocationL = sxL + "`" + syL + "`" + szL + "`" + sworldL;

			plugin.userFile = new File(plugin.users, uuid.toString() + ".yml");
			File user = plugin.userFile;
			plugin.userconfig = YamlConfiguration.loadConfiguration(user);
			FileConfiguration userConfig = plugin.userconfig;
			FileConfiguration chests2 = plugin.chestsConfig;

			// TODO RightChest
			if (chests2.getStringList("Chests").contains(chestLocationR)) {
				if (userConfig.getStringList("Chests").contains(chestLocationR)) {
					e.setCancelled(false);
				}
				else {
					e.setCancelled(true);
					player.sendMessage(plugin.logo + ChatColor.DARK_RED + "This is not your chest!!!");

				}
			}
			// TODO LeftChest
			if (chests2.getStringList("Chests").contains(chestLocationL)) {
				if (userConfig.getStringList("Chests").contains(chestLocationL)) {
					e.setCancelled(false);
				}
				else {
					e.setCancelled(true);
					player.sendMessage(plugin.logo + ChatColor.DARK_RED + "This is not your chest!!!");
				}

			}

		}
	}

	// TODO Methods

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

		if (block.getType().equals(Material.CHEST)) {
			Chest chest = (Chest) block.getState();
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

	public boolean isChestClaimed(Block block) {
		FileConfiguration chests2 = plugin.chestsConfig;

		String sx = Integer.toString(block.getX());
		String sy = Integer.toString(block.getY());
		String sz = Integer.toString(block.getZ());
		String sworld = block.getWorld().toString();

		List<String> chests = chests2.getStringList("Chests");

		for (String s : chests) {
			String[] chest;
			chest = s.split("`");
			if (chest[0].equals(sx) && chest[1].equals(sy) && chest[2].equals(sz) && chest[3].equals(sworld)) {
				return true;
			}
			else {
				return false;
			}

		}

		return false;
	}
}