package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.util.CountdownTimer;
import com.github.imdabigboss.kitduels.util.EntityUtils;
import com.github.imdabigboss.kitduels.util.WorldEditUtils;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    public static void createMap(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGenerator");
        wc.type(WorldType.NORMAL);
        World out = wc.createWorld();
        //TODO: Set game rules
        KitDuels.allMaps.add(name);
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
            if (KitDuels.inUseMaps.contains(map)) {
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

        for (String map : KitDuels.enabledMaps.keySet()) {
            if (KitDuels.inUseMaps.contains(map)) {
                continue;
            }

            int num = KitDuels.enabledMaps.get(map).size();
            if (num > currentNumPlayers && num < getMapMaxPlayers(map) && getMapMaxPlayers(map) == players) {
                currentNumPlayers = num;
                currentMap = map;
            }
        }

        if (currentNumPlayers == 0) {
            List<String> maps = new ArrayList<>();
            for (String map : KitDuels.enabledMaps.keySet()) {
                if (!KitDuels.inUseMaps.contains(map)) {
                    maps.add(map);
                }
            }

            if (maps.size() == 0) {
                return "";
            }

            int index = new Random().nextInt(maps.size());
            currentMap = maps.get(index);
        }

        return currentMap;
    }

    public static void queuePlayerToMap(Player player, String map) {
        if (!KitDuels.enabledMaps.containsKey(map)) {
            player.sendMessage(ChatColor.RED + "The map you were sent to does not exist!");
            return;
        }
        if (KitDuels.enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            player.sendMessage(ChatColor.RED + "The map you were sent to has no more slots available!");
            return;
        }
        if (KitDuels.inUseMaps.contains(map)) {
            player.sendMessage(ChatColor.RED + "The map you were sent to is in use!");
            return;
        }

        if (KitDuels.enabledMaps.get(map).contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in the queue for this game!");
            return;
        }

        KitDuels.enabledMaps.get(map).add(player);
        int playerNum = KitDuels.enabledMaps.get(map).size();
        KitDuels.playerMaps.put(player, map);

        if (KitDuels.getMapsYML().getConfig().contains(map + ".spawn" + playerNum)) {
            Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".spawn" + playerNum);
            player.teleport(location);
        } else {
            player.sendMessage(ChatColor.RED + "Could not get your spawn location, please contact a server owner!");
            Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".world");
            player.teleport(location);
        }
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);

        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
            mapPlayer.sendMessage(player.getDisplayName() + " has joined (" + ChatColor.GREEN + playerNum + "/" + getMapMaxPlayers(map) + ChatColor.RESET + ")");
        }

        if (KitDuels.enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            new CountdownTimer(10, KitDuels.getInstance()) {
                @Override
                public void count(int current) {
                    if (current == 0) {
                        startGame(map);
                    } else {
                        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                            mapPlayer.sendMessage("Game starting in " + ChatColor.GOLD + current);
                            mapPlayer.sendTitle(ChatColor.RED.toString() + current, ChatColor.GOLD + "Game starting soon!", 0, 30, 0);
                            mapPlayer.getWorld().playSound(mapPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                    }
                }
            }.start();
        }
    }

    public static boolean removePlayerFromMap(Player player, boolean exists) {
        if (KitDuels.playerMaps.containsKey(player)) {
            String map = KitDuels.playerMaps.get(player);

            KitDuels.enabledMaps.get(map).remove(player);
            KitDuels.playerMaps.remove(player);

            if (KitDuels.ongoingMaps.contains(map)) {
                KitDuels.mapAlivePlayers.get(map).remove(player);

                String gameOver = "";
                if (KitDuels.enabledMaps.get(map).size() < 2) {
                    gameOver = ", game over";
                }

                for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                    mapPlayer.sendMessage(player.getDisplayName() + " has left" + gameOver);
                }

                if (player.isOnline()) {
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    KitDuels.sendToSpawn(player);
                    player.sendMessage("You left the map");
                }

                gameEnded(map);
            } else {
                if (!KitDuels.mapAlivePlayers.containsKey(map)) {
                    for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                        mapPlayer.sendMessage(player.getDisplayName() + " has left (" + ChatColor.GREEN + KitDuels.enabledMaps.get(map).size() + "/" + getMapMaxPlayers(map) + ChatColor.RESET + ")");
                    }

                    if (player.isOnline()) {
                        player.sendMessage("You left the map");
                        KitDuels.sendToSpawn(player);
                    }
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
                mapPlayer.sendMessage("Game start canceled...");
            }
            return;
        }

        KitDuels.ongoingMaps.add(map);
        KitDuels.inUseMaps.add(map);
        KitDuels.mapAlivePlayers.put(map, new ArrayList<>());
        int playerNum = 1;
        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
            KitDuels.mapAlivePlayers.get(map).add(mapPlayer);
            mapPlayer.setHealth(20);
            mapPlayer.setFoodLevel(20);

            mapPlayer.sendMessage("Go, go, go!");
            mapPlayer.sendTitle(ChatColor.RED + "Fight!", " ", 0, 10, 10);
            mapPlayer.setGameMode(GameMode.SURVIVAL);

            if (KitDuels.allKits.size() > 0) {
                String kitName;
                if (KitDuels.playerKits.containsKey(mapPlayer)) {
                    kitName = KitDuels.playerKits.get(mapPlayer);
                } else {
                    int index = new Random().nextInt(KitDuels.allKits.size());
                    kitName = KitDuels.allKits.get(index);
                }
                KitDuels.loadKitToPlayer(mapPlayer, kitName);
                mapPlayer.sendMessage(ChatColor.GREEN + "You got the " + ChatColor.GOLD + kitName + ChatColor.GREEN + " kit!");
            } else {
                if (mapPlayer.isOp()) {
                    mapPlayer.sendMessage("Consider adding kits with /kdkits!");
                }
            }

            if (KitDuels.getMapsYML().getConfig().contains(map + ".spawn" + playerNum)) {
                Location location = KitDuels.getMapsYML().getConfig().getLocation(map + ".spawn" + playerNum);
                mapPlayer.teleport(location);
            } else {
                mapPlayer.sendMessage(ChatColor.RED + "Could not get your spawn location, please contact a server owner!");
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
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            KitDuels.sendToSpawn(player);
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
}
