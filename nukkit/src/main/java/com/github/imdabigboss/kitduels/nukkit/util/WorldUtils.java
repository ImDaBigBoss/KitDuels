package com.github.imdabigboss.kitduels.nukkit.util;

import com.github.imdabigboss.kitduels.common.interfaces.Location;

import com.github.imdabigboss.kitduels.nukkit.KitDuels;

import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldUtils implements com.github.imdabigboss.kitduels.common.util.WorldUtils {
    private KitDuels plugin;

    public WorldUtils(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean copyWorld(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists()) {
                        target.mkdirs();
                    }

                    String[] files = source.list();
                    if (files == null) {
                        return false;
                    }

                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean deleteWorld(String world) {
        File path = new File(plugin.getServer().getDataPath() + "/worlds/" + world);
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
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }

    @Override
    public void unloadWorld(String world) {
        plugin.getServer().unloadLevel(plugin.getServer().getLevelByName(world), true);
    }

    @Override
    public Location getSpawnLocation() {
        return getSpawnLocation("world");
    }

    @Override
    public Location getSpawnLocation(String world) {
        return new com.github.imdabigboss.kitduels.nukkit.interfaces.Location(plugin.getServer().getLevelByName(world).getSpawnLocation().getLocation(), plugin.getServer());
    }

    @Override
    public void createWorld(String name) {
        plugin.getServer().generateLevel(name, 9999999L, EmptyWorldGen.class);
        plugin.getServer().loadLevel(name);

        setGameRules(plugin.getServer().getLevelByName(name));

    }

    @Override
    public void loadWorld(String name) {
        plugin.getServer().loadLevel(name);

        setGameRules(plugin.getServer().getLevelByName(name));
    }

    @Override
    public void resetMap(Location pos1, Location pos2) {
        //TODO: Implement this
    }

    @Override
    public void resetClone(Location pos1, Location pos2) {
        //TODO: Implement this
    }

    @Override
    public void removeClone(Location pos1, Location pos2) {
        //TODO: Implement this
    }

    private void setGameRules(Level level) {
        level.gameRules.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        level.gameRules.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        level.gameRules.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        level.gameRules.setGameRule(GameRule.DO_FIRE_TICK, false);
        level.gameRules.setGameRule(GameRule.KEEP_INVENTORY, false);
    }
}
