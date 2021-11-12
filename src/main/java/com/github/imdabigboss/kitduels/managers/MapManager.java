package com.github.imdabigboss.kitduels.managers;

import com.github.imdabigboss.kitduels.KitDuels;
import com.github.imdabigboss.kitduels.util.CountdownTimer;
import com.github.imdabigboss.kitduels.util.EntityUtils;
import com.github.imdabigboss.kitduels.util.WorldEditUtils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    public static void createMap(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGen");
        wc.type(WorldType.NORMAL);
        World out = wc.createWorld();
        setMapGameRules(out);
        KitDuels.allMaps.put(name, "");
        KitDuels.getInstance().getConfig().set("allMaps", KitDuels.allMaps);
        KitDuels.getInstance().saveConfig();
    }

    public static int getMapMaxPlayers(String map) {
        if (KitDuels.getMapsYML().getConfig().contains((map + ".maxPlayers"))) {
            return KitDuels.getMapsYML().getConfig().getInt(map + ".maxPlayers");
        } else {
            return 0;
        }
    }

    public static String getNextMap() {
        int currentNumPlayers = -1;
        String currentMap = "";

        for (String map : KitDuels.enabledMaps.keySet()) {
            if (KitDuels.inUseMaps.containsKey(map)) {
                continue;
            }

            int num = KitDuels.enabledMaps.get(map).size();
            if (num > currentNumPlayers && num < getMapMaxPlayers(map)) {
                currentNumPlayers = num;
                currentMap = map;
            }
        }

        if (currentNumPlayers == 0) {
            int index = new Random().nextInt(KitDuels.enabledMaps.size());
            currentMap = new ArrayList<>(KitDuels.enabledMaps.keySet()).get(index);
        }

        return currentMap;
    }

    public static String getNextMap(int players) {
        int currentNumPlayers = -1;
        String currentMap = "";
        List<String> maps = new ArrayList<>();

        for (String map : KitDuels.enabledMaps.keySet()) {
            if (getMapMaxPlayers(map) == players) {
                maps.add(map);

                int num = KitDuels.enabledMaps.get(map).size();
                if (num > currentNumPlayers && num < getMapMaxPlayers(map)) {
                    currentNumPlayers = num;
                    currentMap = map;
                }
            }
        }

        if (maps.size() == 0) {
            return "";
        }

        if (currentNumPlayers == 0) {
            int index = new Random().nextInt(maps.size());
            currentMap = maps.get(index);
        }

        return currentMap;
    }

    public static void queuePlayerToMap(Player player, String map) {
        if (!KitDuels.enabledMaps.containsKey(map)) {
            player.sendMessage(KitDuels.getTextManager().get("general.errors.notAMap"));
            return;
        }
        if (KitDuels.enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            player.sendMessage(KitDuels.getTextManager().get("general.errors.noMapSlotsAvailable"));
            return;
        }
        if (KitDuels.inUseMaps.containsKey(map) || KitDuels.ongoingMaps.contains(map)) {
            player.sendMessage(KitDuels.getTextManager().get("general.errors.mapInUse"));
            return;
        }

        if (KitDuels.enabledMaps.get(map).contains(player)) {
            player.sendMessage(KitDuels.getTextManager().get("general.errors.alreadyInQueue"));
            return;
        }

        KitDuels.enabledMaps.get(map).add(player);
        int playerNum = KitDuels.enabledMaps.get(map).size();
        KitDuels.playerMaps.put(player, map);

        if (KitDuels.getMapsYML().getConfig().contains(map + ".spawn" + playerNum)) {
            Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".spawn" + playerNum);
            player.teleport(location);
        } else {
            player.sendMessage(KitDuels.getTextManager().get("general.errors.couldNotGetSpawn"));
            Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".world");
            player.teleport(location);
        }
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(KitDuels.getTextManager().get("items.leaveGame"));
        item.setItemMeta(meta);
        player.getInventory().setItem(8, item);

        if (KitDuels.allMaps.get(map).equalsIgnoreCase("")) {
            item = new ItemStack(Material.CHEST);
            meta = item.getItemMeta();
            meta.setDisplayName(KitDuels.getTextManager().get("items.kitSelect"));
            item.setItemMeta(meta);
            player.getInventory().setItem(0, item);
        }

        String message = KitDuels.getTextManager().get("messages.playerJoined", player.getDisplayName(), playerNum, getMapMaxPlayers(map));
        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
            mapPlayer.sendMessage(message);
        }

        if (KitDuels.enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            KitDuels.inUseMaps.put(map, 1);
            new CountdownTimer(10, KitDuels.getInstance()) {
                @Override
                public void count(int current) {
                    if (current == 0) {
                        startGame(map);
                    } else {
                        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                            mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.gameStarting", current));
                            mapPlayer.sendTitle(ChatColor.RED.toString() + current, KitDuels.getTextManager().get("messages.gameStartingSoon"), 0, 30, 0);
                            mapPlayer.getWorld().playSound(mapPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                    }
                }
            }.start();
        }
    }

    public static boolean removePlayerFromMap(Player player) {
        if (KitDuels.playerMaps.containsKey(player)) {
            String map = KitDuels.playerMaps.get(player);

            KitDuels.enabledMaps.get(map).remove(player);
            KitDuels.playerMaps.remove(player);

            if (KitDuels.ongoingMaps.contains(map)) {
                KitDuels.mapAlivePlayers.get(map).remove(player);

                String gameOver = "";
                if (KitDuels.enabledMaps.get(map).size() < 2) {
                    gameOver = KitDuels.getTextManager().get("messages.gameOver");
                }

                for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                    mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.playerLeftOngoing") + gameOver);
                }

                if (player.isOnline()) {
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    GameManager.sendToSpawn(player);
                    player.sendMessage(KitDuels.getTextManager().get("messages.youLeft"));
                }

                gameEnded(map);
            } else {
                boolean sendMessage = false;
                if (!KitDuels.inUseMaps.containsKey(map)) {
                    sendMessage = true;
                }
                if (KitDuels.inUseMaps.containsKey(map)) {
                    if (KitDuels.inUseMaps.get(map) == 1) {
                        sendMessage = true;
                    }
                }

                if (sendMessage) {
                    String message = KitDuels.getTextManager().get("messages.playerLeft", player.getDisplayName(), KitDuels.enabledMaps.get(map).size(), getMapMaxPlayers(map));
                    for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                        mapPlayer.sendMessage(message);
                    }

                    if (player.isOnline()) {
                        player.sendMessage(KitDuels.getTextManager().get("messages.youLeft"));
                        GameManager.sendToSpawn(player);
                    }
                } else {
                    GameManager.sendToSpawn(player);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static void startGame(String map) {
        if (KitDuels.enabledMaps.get(map).size() != getMapMaxPlayers(map)) {
            for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.gameStartCancelled"));
            }
            KitDuels.inUseMaps.remove(map);
            return;
        }

        KitDuels.inUseMaps.replace(map, 0);
        KitDuels.ongoingMaps.add(map);
        KitDuels.mapAlivePlayers.put(map, new ArrayList<>());

        int playerNum = 1;
        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
            KitDuels.mapAlivePlayers.get(map).add(mapPlayer);
            mapPlayer.setHealth(20);
            mapPlayer.setFoodLevel(20);
            mapPlayer.getInventory().clear();
            mapPlayer.setFireTicks(0);

            mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.gameStarted"));
            mapPlayer.sendTitle(KitDuels.getTextManager().get("messages.gameStartedTitle"), " ", 0, 10, 10);
            mapPlayer.setGameMode(GameMode.SURVIVAL);

            if (KitDuels.allKits.size() > 0) {
                String kitName = "";

                if (!KitDuels.allMaps.get(map).equalsIgnoreCase("")) {
                    kitName = KitDuels.allMaps.get(map);
                } else if (KitDuels.playerKits.containsKey(mapPlayer)) {
                    kitName = KitDuels.playerKits.get(mapPlayer);
                } else {
                    int index = new Random().nextInt(KitDuels.allKits.size());
                    kitName = KitDuels.allKits.get(index);
                }
                if (!kitName.equalsIgnoreCase("")) {
                    GameManager.loadKitToPlayer(mapPlayer, kitName);
                }
                mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.gotKit", kitName));
            } else {
                if (mapPlayer.isOp()) {
                    mapPlayer.sendMessage(KitDuels.getTextManager().get("messages.considerAddingKits"));
                }
            }

            if (KitDuels.getMapsYML().getConfig().contains(map + ".spawn" + playerNum)) {
                Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".spawn" + playerNum);
                mapPlayer.teleport(location);
            } else {
                mapPlayer.sendMessage(KitDuels.getTextManager().get("general.errors.couldNotGetSpawn"));
                Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".world");
                mapPlayer.teleport(location);
            }
            playerNum++;
        }
    }

    public static void gameEnded(String map) {
        for (Player player : KitDuels.enabledMaps.get(map)) {
            if (!player.isOnline()) {
                continue;
            }

            player.setHealth(20);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            GameManager.sendToSpawn(player);
            KitDuels.playerMaps.remove(player);
        }

        KitDuels.mapAlivePlayers.remove(map);
        KitDuels.enabledMaps.get(map).clear();
        KitDuels.ongoingMaps.remove(map);

        Location pos1 = KitDuels.getMapsYML().getConfig().getLocation(map + ".pos1");
        Location pos2 = KitDuels.getMapsYML().getConfig().getLocation(map + ".pos2");
        Location[] region = WorldEditUtils.getCloneRegion(pos1, pos2);
        WorldEditUtils.removeRegion(pos1, pos2);
        WorldEditUtils.cloneRegion(region[0], region[1], pos1);
        EntityUtils.killEntities(pos1, pos2);

        KitDuels.inUseMaps.remove(map);
    }

    public static void setMapGameRules(World world) {
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
    }
}
