package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.TextManager;

public class LeaveGameCommand {
    private KitDuels plugin;
    private TextManager textManager;

    public LeaveGameCommand(KitDuels plugin) {
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

        boolean out = plugin.getGameManager().removePlayerFromMap(player);
        if (!out) {
            sender.sendMessage(textManager.get("general.errors.notInGame"));
        }
        return true;
    }
}
