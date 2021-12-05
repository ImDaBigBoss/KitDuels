package com.github.imdabigboss.kitduels.common.commands;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonCommandSender;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.managers.TextManager;
import com.github.imdabigboss.kitduels.common.util.YMLUtils;

public class KitDuelsKitsCommand {
    private KitDuels plugin;
    private YMLUtils kitsYML;
    public TextManager textManager;

    public KitDuelsKitsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.kitsYML = plugin.getKitsYML();
        this.textManager = plugin.getTextManager();
    }

    public boolean runCommand(CommonCommandSender sender, String label, String[] args) {
        if (sender.isConsole()) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }

        CommonPlayer player = sender.getPlayer();

        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equals("add")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("random")) {
                        sender.sendMessage(textManager.get("general.errors.kitNameReserved"));
                        return true;
                    }
                    if (plugin.getKitManager().getKits().contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitAlreadyExists"));
                        return true;
                    }

                    plugin.getKitManager().getKits().add(args[1]);
                    kitsYML.set("allKits", plugin.getKitManager().getKits());

                    String content;
                    String armor;
                    try {
                        String[] out = plugin.getInventorySerialization().playerInventoryToBase64(player);
                        content = out[0];
                        armor = out[1];
                    } catch (IllegalStateException e) {
                        sender.sendMessage(textManager.get("general.errors.unableToSaveItemStacks"));
                        return true;
                    }

                    kitsYML.set(args[1] + ".content", content);
                    kitsYML.set(args[1] + ".armor", armor);

                    sender.sendMessage(textManager.get("general.info.kitAdded"));
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("delete")) {
                if (args.length == 2) {
                    if (!plugin.getKitManager().getKits().contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                        return true;
                    }

                    plugin.getKitManager().getKits().remove(args[1]);
                    kitsYML.set("allKits", plugin.getKitManager().getKits());

                    kitsYML.set(args[1], null);

                    sender.sendMessage(textManager.get("general.info.kitDeleted"));
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("load")) {
                if (args.length == 2) {
                    if (!plugin.getKitManager().getKits().contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                        return true;
                    }

                    if (plugin.getKitManager().loadKitToPlayer(player, args[1])) {
                        sender.sendMessage(textManager.get("general.info.kitLoaded"));
                    } else {
                        sender.sendMessage(textManager.get("general.errors.unableToDecodeClassType"));
                    }
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equalsIgnoreCase("saveAs")) {
                if (args[1].equalsIgnoreCase("random")) {
                    sender.sendMessage(textManager.get("general.errors.kitNameReserved"));
                    return true;
                }
                if (!plugin.getKitManager().getKits().contains(args[1])) {
                    plugin.getKitManager().getKits().add(args[1]);
                    kitsYML.set("allKits", plugin.getKitManager().getKits());
                }

                String content = "";
                String armor = "";
                boolean didCatch = false;
                try {
                    String[] out = plugin.getInventorySerialization().playerInventoryToBase64(player);
                    content = out[0];
                    armor = out[1];
                } catch (IllegalStateException e) {
                    didCatch = true;
                }
                if (didCatch) {
                    sender.sendMessage(textManager.get("general.errors.unableToSaveItemStacks"));
                    return true;
                }

                kitsYML.set(args[1] + ".content", content);
                kitsYML.set(args[1] + ".armor", armor);

                sender.sendMessage(textManager.get("general.info.kitSaved"));
            } else if (args[0].equals("list")) {
                String kitList = textManager.get("general.info.kitList");
                for (String kit : plugin.getKitManager().getKits()) {
                    kitList += "\n - " + kit;
                }
                player.sendMessage(kitList);
            }
        }

        plugin.getConfigYML().saveConfig();
        kitsYML.saveConfig();
        return true;
    }

    public void sendHelp(CommonCommandSender sender) {
        sender.sendMessage("The correct usage is:\n - add <name>\n - delete <name>\n - load <name>\n - saveAs <name>\n - list");
    }
}
