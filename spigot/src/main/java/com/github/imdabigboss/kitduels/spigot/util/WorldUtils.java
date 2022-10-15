package com.github.imdabigboss.kitduels.spigot.util;

import com.github.imdabigboss.kitduels.spigot.interfaces.Location;
import com.github.imdabigboss.kitduels.spigot.KitDuels;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.*;

public class WorldUtils implements com.github.imdabigboss.kitduels.common.util.WorldUtils {
    private KitDuels plugin;

    public WorldUtils(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean deleteWorld(String world) {
        File path = plugin.getServer().getWorld(world).getWorldFolder();
        return deleteWorld(path);
    }

    @Override
    public boolean deleteWorld(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return false;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    if (!deleteWorld(file)) {
                        return false;
                    }
                } else {
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }
        return path.delete();
    }

    @Override
    public void unloadWorld(String world) {
        plugin.getServer().unloadWorld(plugin.getServer().getWorld(world), true);
    }

    @Override
    public Location getSpawnLocation() {
        return getSpawnLocation("world");
    }

    @Override
    public Location getSpawnLocation(String world) {
        return new Location(plugin.getServer().getWorld(world).getSpawnLocation(), plugin.getServer());
    }

    @Override
    public void createWorld(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGen");
        wc.type(WorldType.NORMAL);
        World out = wc.createWorld();

        setGameRules(out);
    }

    @Override
    public void loadWorld(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGen");
        wc.type(WorldType.NORMAL);
        World world = wc.createWorld();

        setGameRules(world);
    }

    @Override
    public void resetMap(com.github.imdabigboss.kitduels.common.interfaces.Location pos1, com.github.imdabigboss.kitduels.common.interfaces.Location pos2) {
        org.bukkit.Location spigotPos1 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos1, plugin.getServer()).toBukkit();
        org.bukkit.Location spigotPos2 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos2, plugin.getServer()).toBukkit();

        org.bukkit.Location[] region = WorldEditUtils.getCloneRegion(spigotPos1, spigotPos2);
        WorldEditUtils.removeRegion(spigotPos1, spigotPos2);
        WorldEditUtils.cloneRegion(region[0], region[1], spigotPos1);
    }

    @Override
    public void resetClone(com.github.imdabigboss.kitduels.common.interfaces.Location pos1, com.github.imdabigboss.kitduels.common.interfaces.Location pos2) {
        org.bukkit.Location spigotPos1 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos1, plugin.getServer()).toBukkit();
        org.bukkit.Location spigotPos2 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos2, plugin.getServer()).toBukkit();

        WorldEditUtils.removeMapClone(spigotPos1, spigotPos2);
        WorldEditUtils.cloneRegion(spigotPos1, spigotPos2, WorldEditUtils.getCloneRegion(spigotPos1, spigotPos2)[0]);
    }

    @Override
    public void removeClone(com.github.imdabigboss.kitduels.common.interfaces.Location pos1, com.github.imdabigboss.kitduels.common.interfaces.Location pos2) {
        org.bukkit.Location spigotPos1 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos1, plugin.getServer()).toBukkit();
        org.bukkit.Location spigotPos2 = new com.github.imdabigboss.kitduels.spigot.interfaces.Location(pos2, plugin.getServer()).toBukkit();

        WorldEditUtils.removeMapClone(spigotPos1, spigotPos2);
    }

    private void setGameRules(World world) {
        if (world == null) {
            return;
        }

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
    }
}
