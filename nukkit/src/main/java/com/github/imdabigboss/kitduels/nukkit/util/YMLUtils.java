package com.github.imdabigboss.kitduels.nukkit.util;

import com.github.imdabigboss.kitduels.nukkit.KitDuels;
import com.github.imdabigboss.kitduels.nukkit.interfaces.Location;

import cn.nukkit.utils.Config;

import java.io.File;
import java.util.List;
import java.util.Set;

public class YMLUtils implements com.github.imdabigboss.kitduels.common.util.YMLUtils {
    private String configName;
    private Config configuration;
    private File file;
    private KitDuels plugin;

    public YMLUtils(KitDuels plugin, String configName) {
        this.configName = configName;
        this.plugin = plugin;

        this.file = new File(plugin.getDataFolder(), this.configName);
        if (!this.file.exists()) {
            plugin.saveResource(configName, false);
        }

        this.configuration = new Config(this.file, Config.YAML);
    }

    @Override
    public void saveConfig() {
        configuration.save(file);
    }

    @Override
    public boolean contains(String path) {
        return this.configuration.exists(path);
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
        String world = null;
        if (this.configuration.exists(path + ".world")) {
            world = this.configuration.getString(path + ".world");
        }

        double x = this.configuration.getDouble(path + ".x");
        double y = this.configuration.getDouble(path + ".y");
        double z = this.configuration.getDouble(path + ".z");

        double yaw = this.configuration.getDouble(path + ".yaw");
        double pitch = this.configuration.getDouble(path + ".pitch");

        cn.nukkit.level.Location nukkitLoc = new cn.nukkit.level.Location(x, y, z, yaw, pitch);
        if (world != null) {
            nukkitLoc.setLevel(this.plugin.getServer().getLevelByName(world));
        }
        return new Location(nukkitLoc, this.plugin.getServer());
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

    public void setLocation(String path, Location location) {
        if (location == null) {
            return;
        }

        if (location.hasWorld()) {
            this.configuration.set(path + ".world", location.getWorld());
        }

        this.configuration.set(path + ".x", location.getX());
        this.configuration.set(path + ".y", location.getY());
        this.configuration.set(path + ".z", location.getZ());

        this.configuration.set(path + ".yaw", location.getYaw());
        this.configuration.set(path + ".pitch", location.getPitch());
    }
}
