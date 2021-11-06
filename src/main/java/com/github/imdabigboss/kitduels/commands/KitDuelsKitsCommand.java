package com.github.imdabigboss.kitduels.commands;

import com.github.imdabigboss.kitduels.managers.GameManager;
import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.YMLUtils;
import com.github.imdabigboss.kitduels.managers.TextManager;
import com.github.imdabigboss.kitduels.util.InventorySerialization;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitDuelsKitsCommand implements CommandExecutor, TabExecutor {
    private KitDuels plugin;
    private YMLUtils kitsYML;
    public TextManager textManager;

    public KitDuelsKitsCommand(KitDuels plugin) {
        super();
        this.plugin = plugin;
        this.kitsYML = KitDuels.getKitsYML();
        this.textManager = KitDuels.getTextManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(textManager.get("general.errors.notPlayer"));
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equals("add")) {
                if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("random")) {
                        sender.sendMessage(textManager.get("general.errors.kitNameReserved"));
                        return true;
                    }
                    if (KitDuels.allKits.contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitAlreadyExists"));
                        return true;
                    }

                    KitDuels.allKits.add(args[1]);
                    kitsYML.getConfig().set("allKits", KitDuels.allKits);

                    String content;
                    String armor;
                    try {
                        String[] out = InventorySerialization.playerInventoryToBase64(player.getInventory());
                        content = out[0];
                        armor = out[1];
                    } catch (IllegalStateException e) {
                        sender.sendMessage(textManager.get("general.errors.unableToSaveItemStacks"));
                        return true;
                    }

                    kitsYML.getConfig().set(args[1] + ".content", content);
                    kitsYML.getConfig().set(args[1] + ".armor", armor);

                    sender.sendMessage(textManager.get("general.info.kitAdded"));
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("delete")) {
                if (args.length == 2) {
                    if (!KitDuels.allKits.contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                        return true;
                    }

                    KitDuels.allKits.remove(args[1]);
                    kitsYML.getConfig().set("allKits", KitDuels.allKits);

                    kitsYML.getConfig().set(args[1], null);

                    sender.sendMessage(textManager.get("general.info.kitDeleted"));
                } else {
                    sendHelp(sender);
                }
            } else if (args[0].equals("load")) {
                if (args.length == 2) {
                    if (!KitDuels.allKits.contains(args[1])) {
                        sender.sendMessage(textManager.get("general.errors.kitDoesntExist"));
                        return true;
                    }

                    if (GameManager.loadKitToPlayer(player, args[1])) {
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
                if (!KitDuels.allKits.contains(args[1])) {
                    KitDuels.allKits.add(args[1]);
                    kitsYML.getConfig().set("allKits", KitDuels.allKits);
                }

                String content = "";
                String armor = "";
                boolean didCatch = false;
                try {
                    String[] out = InventorySerialization.playerInventoryToBase64(player.getInventory());
                    content = out[0];
                    armor = out[1];
                } catch (IllegalStateException e) {
                    didCatch = true;
                }
                if (didCatch) {
                    sender.sendMessage(textManager.get("general.errors.unableToSaveItemStacks"));
                    return true;
                }

                kitsYML.getConfig().set(args[1] + ".content", content);
                kitsYML.getConfig().set(args[1] + ".armor", armor);

                sender.sendMessage(textManager.get("general.info.kitSaved"));
            } else if (args[0].equals("list")) {
                String kitList = textManager.get("general.info.kitList");
                for (String kit : KitDuels.allKits) {
                    kitList += "\n - " + kit;
                }
                player.sendMessage(kitList);
            }
        }

        plugin.saveConfig();
        kitsYML.saveConfig();
        return true;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("The correct usage is:\n - add <name>\n - delete <name>\n - load <name>\n - saveAs <name>\n - list");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.add("add");
            cmds.add("delete");
            cmds.add("load");
            cmds.add("saveAs");
            cmds.add("list");

        } else if (args.length == 2) {
            if (args[0].equals("load") || args[0].equals("delete")) {
                cmds.addAll(KitDuels.allKits);
            }
        }
        return cmds;
    }
}
