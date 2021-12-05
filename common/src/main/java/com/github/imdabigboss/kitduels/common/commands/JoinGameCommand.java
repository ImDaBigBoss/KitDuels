package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.GameManager;
import com.github.imdabigboss.kitduels.common.managers.TextManager;

public class JoinGameCommand {
    private GameManager gameManager;
    private TextManager textManager;
    private KitDuels plugin;

    public JoinGameCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        this.textManager = plugin.getTextManager();
    }

    public boolean runCommand(CommonCommandSender sender, String label, String[] args) {
        if (sender.isConsole()) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }

        CommonPlayer player = sender.getPlayer();

        if (gameManager.editModePlayers.containsKey(player.getName())) {
            sender.sendMessage(textManager.get("general.errors.editingMap"));
        }
        if (gameManager.playerMaps.containsKey(player.getName())) {
            sender.sendMessage(textManager.get("general.errors.alreadyInGame"));
            return true;
        }

        boolean kit = false;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                String map = gameManager.getNextMap();
                if (map.equals("")) {
                    sender.sendMessage(textManager.get("general.errors.noMapsAvailable"));
                } else {
                    gameManager.queuePlayerToMap(player, map);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("kit")) {
                kit = true;
            }
        }

        plugin.getGUIManager().showMapSelector(player, kit);

        return true;
    }
}
