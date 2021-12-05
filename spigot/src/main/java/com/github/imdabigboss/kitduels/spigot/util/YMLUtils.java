package com.github.imdabigboss.kitduels.spigot.util;

import com.github.imdabigboss.kitduels.spigot.KitDuels;
import com.github.imdabigboss.kitduels.spigot.interfaces.Location;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class YMLUtils implements com.github.imdabigboss.kitduels.common.util.YMLUtils {
    private String configName;
    private FileConfiguration configuration;
    private File file;
    private KitDuels plugin;

    public YMLUtils(KitDuels plugin, String configName) {
        this.configName = configName;
        this.plugin = plugin;

        this.file = new File(plugin.getDataFolder(), this.configName);
        if (!this.file.exists()) {
            plugin.saveResource(configName, false);
        }

        this.configuration = new YamlConfiguration();

        try {
            this.configuration.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveConfig() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            plugin.getLog().warning("Unable to save " + configName); // shouldn't really happen, but save throws the exception
        }
    }

    @Override
    public boolean contains(String path) {
        return this.configuration.contains(path);
    }

    @Override
    public Object get(String path) {
        return this.configuration.get(path);
    }

    @Override
    public String getString(String path) {
        return this.configuration.getString(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return this.configuration.getBoolean(path);
    }

    @Override
    public int getInt(String path) {
        return this.configuration.getInt(path);
    }

    @Override
    public Location getLocation(String path) {
        return new Location(this.configuration.getLocation(path), plugin.getServer());
    }

    @Override
    public List<String> getStringList(String path) {
        return this.configuration.getStringList(path);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return this.configuration.getKeys(false);
    }

    @Override
    public void set(String path, Object value) {
        this.configuration.set(path, value);
    }
}
