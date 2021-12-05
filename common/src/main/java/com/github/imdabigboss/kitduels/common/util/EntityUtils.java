package com.github.imdabigboss.kitduels.common.util;

import com.github.imdabigboss.kitduels.common.interfaces.Location;

public interface EntityUtils {
    void killEntities(Location pos1, Location pos2);
    boolean isIn(Location loc, Location locA, Location locB);
}
