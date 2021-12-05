package com.github.imdabigboss.kitduels.common.managers;

import com.github.imdabigboss.kitduels.common.interfaces.Location;

public interface HologramManager {
    void setLeaderboardLines(int lines);
    void updateHolo(Location location);
    void deleteAllHolos();
    void registerPlaceholders();
}
