package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.YMLUtils;

import java.util.HashMap;
import java.util.Map;

public class TextManager {
    private YMLUtils yml;
    private Map<String, String> messagesCache = new HashMap<>();

    public TextManager() {
        this.yml = KitDuels.getMessagesYML();
    }

    public String get(String key) {
        if (messagesCache.containsKey(key)) {
            return messagesCache.get(key);
        }

        String value;

        if (yml.getConfig().contains(key)) {
            value = yml.getConfig().getString(key);
            messagesCache.put(key, value);
        } else {
            value = "ERROR, UNABLE TO GET STRING!";
            messagesCache.put(key, value);
        }
        return value;
    }

    public String get(String key, Object... args) {
        return String.format(get(key), args);
    }
}
