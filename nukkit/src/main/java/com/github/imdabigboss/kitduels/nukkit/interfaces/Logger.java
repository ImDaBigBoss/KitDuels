package com.github.imdabigboss.kitduels.nukkit.interfaces;

import cn.nukkit.plugin.PluginLogger;

public class Logger implements com.github.imdabigboss.kitduels.common.interfaces.Logger {
    private PluginLogger logger;

    public Logger(PluginLogger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warning(String message) {
        this.logger.warning(message);
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }
}
