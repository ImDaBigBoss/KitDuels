package com.github.imdabigboss.kitduels.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.List;

public class EntityUtils {
    public static void killEntities(Location pos1, Location pos2) {
        List<Entity> entities = pos1.getWorld().getEntities();

        for(Entity entity : entities) {
            Location loc = entity.getLocation();
            if(isIn(loc, pos1, pos2)) {
                entity.remove();
            }
        }
    }

    private static boolean isIn(Location loc, Location locA, Location locB) {
        double minX = Math.min(locA.getX(), locB.getX());
        double maxX = Math.max(locA.getX(), locB.getX());

        double minY = Math.min(locA.getY(), locB.getY());
        double maxY = Math.max(locA.getY(), locB.getY());

        double minZ = Math.min(locA.getZ(), locB.getZ());
        double maxZ = Math.max(locA.getZ(), locB.getZ());

        if(loc.getX() > minX && loc.getX() < maxX) {
            if(loc.getY() > minY && loc.getY() < maxY) {
                if(loc.getZ() > minZ && loc.getZ() < maxZ) {
                    return true;
                }
            }
        }
        return false;
    }
}
