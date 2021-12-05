package com.github.imdabigboss.kitduels.spigot.interfaces;

public class Logger implements com.github.imdabigboss.kitduels.common.interfaces.Logger {
    private java.util.logging.Logger logger;
    private String pluginName;

    public Logger(java.util.logging.Logger logger, String pluginName) {
        this.logger = logger;
        this.pluginName = pluginName;
    }

    private String formatMessage(String message) {
        return "[" + this.pluginName + "] " + message;
    }

    @Override
    public void info(String message) {
        this.logger.info(formatMessage(message));
    }

    @Override
    public void warning(String message) {
        this.logger.warning(formatMessage(message));
    }

    @Override
    public void error(String message) {
        this.logger.severe(formatMessage(message));
    }
}
