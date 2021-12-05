package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.TextManager;

public class KitSelectCommand {
    private KitDuels plugin;
    private TextManager textManager;

    public KitSelectCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.textManager = plugin.getTextManager();
    }

    public boolean runCommand(CommonCommandSender sender, String label, String[] args) {
        if (sender.isConsole()) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }

        CommonPlayer player = sender.getPlayer();
        if (args.length != 1) {
            plugin.getGUIManager().showKitSelector(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("random")) {
            plugin.getKitManager().getPlayerKits().remove(player.getName());
        } else {
            if (!plugin.getKitManager().getKits().contains(args[0])) {
                sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                return true;
            }
            plugin.getKitManager().getPlayerKits().put(player.getName(), args[0]);
        }

        sender.sendMessage(textManager.get("general.info.kitSelected", args[0]));
        return true;
    }

    public void sendHelp(CommonCommandSender sender) {
        sender.sendMessage("The correct usage is: /kitselect <name>");
    }
}
