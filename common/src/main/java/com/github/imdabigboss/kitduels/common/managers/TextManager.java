package com.github.imdabigboss.kitduels.common.managers;

import com.github.imdabigboss.kitduels.common.util.YMLUtils;

import java.util.HashMap;
import java.util.Map;

public class TextManager {
    private YMLUtils yml;
    private Map<String, String> messagesCache = new HashMap<>();

    public TextManager(YMLUtils messagesYML) {
        this.yml = messagesYML;
    }

    public String get(String key) {
        if (messagesCache.containsKey(key)) {
            return messagesCache.get(key);
        }

        String value;

        if (yml.contains(key)) {
            value = yml.getString(key);
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
