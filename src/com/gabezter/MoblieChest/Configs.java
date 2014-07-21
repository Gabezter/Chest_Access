package com.gabezter.MoblieChest;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configs {
	private Configs() {
	}

	private static Configs instance = null;
	Main plugin;

	public static Configs getInstance() {
		if (instance == null) {
			instance = new Configs();
		}

		return instance;
	}

	private HashMap<JavaPlugin, HashMap<String, FileConfiguration>> configs = new HashMap<JavaPlugin, HashMap<String, FileConfiguration>>();

	public FileConfiguration reloadConfig(String id) {
		String g = plugin.getDataFolder() + "/User Chests";
		if (!configs.containsKey(plugin))
			configs.put(plugin, new HashMap<String, FileConfiguration>());
		File customConfigFile = new File(g, id + ".yml");
		FileConfiguration customConfig = YamlConfiguration
				.loadConfiguration(customConfigFile);

		InputStream defConfigStream = plugin.getResource(id + ".yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			customConfig.setDefaults(defConfig);
		}

		configs.get(plugin).put(id, customConfig);

		return customConfig;
	}

	public FileConfiguration getConfig(String id) {
		if (configs.containsKey(plugin) && configs.get(plugin).containsKey(id)) {
			return configs.get(plugin).get(id);
		}
		return reloadConfig(id);
	}

	public void saveConfig(String id) {
		String g = plugin.getDataFolder() + "/User Chests";
		try {
			getConfig(id).save(new File(g, id + ".yml"));
		} catch (Exception ex) {
			plugin.getLogger().severe("Could not save config: " + id);
		}
	}
}
