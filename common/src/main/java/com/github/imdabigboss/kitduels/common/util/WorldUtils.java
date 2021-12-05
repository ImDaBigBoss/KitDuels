package com.github.imdabigboss.kitduels.common.util;

import com.github.imdabigboss.kitduels.common.interfaces.Location;

import java.io.File;

public interface WorldUtils {
    boolean deleteWorld(String world);
    boolean deleteWorld(File path);
    void unloadWorld(String world);

    Location getSpawnLocation();
    Location getSpawnLocation(String world);

    void createWorld(String name);
    void loadWorld(String name);

    void resetMap(Location pos1, Location pos2);
    void resetClone(Location pos1, Location pos2);
    void removeClone(Location pos1, Location pos2);
}
