package com.github.imdabigboss.kitduels.nukkit.util;

import cn.nukkit.entity.Entity;
import com.github.imdabigboss.kitduels.common.interfaces.Location;
import com.github.imdabigboss.kitduels.nukkit.KitDuels;

public class EntityUtils implements com.github.imdabigboss.kitduels.common.util.EntityUtils {
    private KitDuels plugin;

    public EntityUtils(KitDuels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void killEntities(Location pos1, Location pos2) {
        Entity[] entities = plugin.getServer().getLevelByName(pos1.getWorld()).getEntities();

        for(Entity entity : entities) {
            Location loc = new com.github.imdabigboss.kitduels.nukkit.interfaces.Location(entity.getLocation(), plugin.getServer());
            if(isIn(loc, pos1, pos2)) {
                entity.kill();
            }
        }
    }

    @Override
    public boolean isIn(Location loc, Location locA, Location locB) {
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
