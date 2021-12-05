package com.github.imdabigboss.kitduels.spigot.interfaces;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpigotCommandSender implements com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender {
    private CommandSender sender;

    public SpigotCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public CommonPlayer getPlayer() {
        if (isConsole()) {
            return null;
        } else {
            return new SpigotPlayer((Player) sender);
        }
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    @Override
    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }
}
