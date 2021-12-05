package com.github.imdabigboss.kitduels.nukkit.interfaces;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;

public class NukkitCommandSender implements com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender {
    private CommandSender sender;

    public NukkitCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public CommonPlayer getPlayer() {
        if (isConsole()) {
            return null;
        } else {
            return new NukkitPlayer((Player) sender);
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
