package com.github.imdabigboss.kitduels.common.util;

import com.github.imdabigboss.kitduels.common.interfaces.Location;

import java.util.List;
import java.util.Set;

public interface YMLUtils {
    void saveConfig();

    boolean contains(String path);

    Object get(String path);
    String getString(String path);
    boolean getBoolean(String path);
    int getInt(String path);
    Location getLocation(String path);
    List<String> getStringList(String path);

    Set<String> getKeys(boolean deep);

    void set(String path, Object value);
    void setLocation(String path, Location location);
}
