package com.github.imdabigboss.kitduels.common.interfaces;

public interface CommonCommandSender {
    CommonPlayer getPlayer();
    void sendMessage(String message);
    boolean isConsole();
}
