package com.github.imdabigboss.kitduels.common.managers;

import com.github.imdabigboss.kitduels.common.KitDuels;
import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.interfaces.Location;
import com.github.imdabigboss.kitduels.common.util.GameMode;

import java.util.*;

public class GameManager {
    private KitDuels plugin;

    public Map<String, String> editModePlayers = new HashMap<>();

    public Map<String, List<CommonPlayer>> enabledMaps = new HashMap<>();
    public Map<String, List<CommonPlayer>> mapAlivePlayers = new HashMap<>();
    public Map<String, String> allMaps = new HashMap<>();
    public Map<String, String> playerMaps = new HashMap<>();
    public List<String> ongoingMaps = new ArrayList<>();
    public Map<String, Integer> inUseMaps = new HashMap<>();

    public GameManager(KitDuels plugin) {
        this.plugin = plugin;
    }

    public void sendToSpawn(CommonPlayer player) {
        if (plugin.getConfigYML().contains("lobbySpawn")) {
            Location location = plugin.getConfigYML().getLocation("lobbySpawn");
            player.teleport(location);
        } else {
            Location location = plugin.getWorldUtils().getSpawnLocation();
            player.teleport(location);
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.clearInventory();
    }

    public void createMap(String name) {
        plugin.getWorldUtils().createWorld(name);
        allMaps.put(name, "");
        plugin.getConfigYML().set("allMaps", allMaps);
        plugin.getConfigYML().saveConfig();
    }

    public int getMapMaxPlayers(String map) {
        if (plugin.getMapsYML().contains((map + ".maxPlayers"))) {
            return plugin.getMapsYML().getInt(map + ".maxPlayers");
        } else {
            return 0;
        }
    }

    public String getNextMap() {
        int currentNumPlayers = -1;
        String currentMap = "";

        for (String map : enabledMaps.keySet()) {
            if (inUseMaps.containsKey(map)) {
                continue;
            }

            int num = enabledMaps.get(map).size();
            if (num > currentNumPlayers && num < getMapMaxPlayers(map)) {
                currentNumPlayers = num;
                currentMap = map;
            }
        }

        if (currentNumPlayers == 0) {
            int index = new Random().nextInt(enabledMaps.size());
            currentMap = new ArrayList<>(enabledMaps.keySet()).get(index);
        }

        return currentMap;
    }

    public String getNextMap(int players) {
        int currentNumPlayers = -1;
        String currentMap = "";
        List<String> maps = new ArrayList<>();

        for (String map : enabledMaps.keySet()) {
            if (getMapMaxPlayers(map) == players) {
                maps.add(map);

                int num = enabledMaps.get(map).size();
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

    public void queuePlayerToMap(CommonPlayer player, String map) {
        if (!enabledMaps.containsKey(map)) {
            player.sendMessage(plugin.getTextManager().get("general.errors.notAMap"));
            return;
        }
        if (enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            player.sendMessage(plugin.getTextManager().get("general.errors.noMapSlotsAvailable"));
            return;
        }
        if (inUseMaps.containsKey(map) || ongoingMaps.contains(map)) {
            player.sendMessage(plugin.getTextManager().get("general.errors.mapInUse"));
            return;
        }

        if (plugin.isPlayerInList(enabledMaps.get(map), player.getName())) {
            player.sendMessage(plugin.getTextManager().get("general.errors.alreadyInQueue"));
            return;
        }

        enabledMaps.get(map).add(player);
        int playerNum = enabledMaps.get(map).size();
        playerMaps.put(player.getName(), map);

        if (plugin.getMapsYML().contains(map + ".spawn" + playerNum)) {
            Location location = plugin.getMapsYML().getLocation(map + ".spawn" + playerNum);
            player.teleport(location);
        } else {
            player.sendMessage(plugin.getTextManager().get("general.errors.couldNotGetSpawn"));
            Location location = plugin.getMapsYML().getLocation(map + ".world");
            player.teleport(location);
        }
        player.clearInventory();
        player.setGameMode(GameMode.ADVENTURE);


        plugin.getPlayerUtils().givePlayerLeaveGameItem(player);
        if (allMaps.get(map).equalsIgnoreCase("")) {
            plugin.getPlayerUtils().givePlayerKitSelectItem(player);
        }

        String message = plugin.getTextManager().get("messages.playerJoined", player.getDisplayName(), playerNum, getMapMaxPlayers(map));
        for (CommonPlayer mapPlayer : enabledMaps.get(map)) {
            mapPlayer.sendMessage(message);
        }

        if (enabledMaps.get(map).size() == getMapMaxPlayers(map)) {
            inUseMaps.put(map, 1);
            plugin.startCountdownAndStartGame(map);
        }

        player.clearPotionEffects();
    }

    public boolean removePlayerFromMap(CommonPlayer player) {
        if (playerMaps.containsKey(player.getName())) {
            String map = playerMaps.get(player.getName());

            plugin.removePlayerFromList(enabledMaps.get(map), player.getName());
            playerMaps.remove(player.getName());

            if (ongoingMaps.contains(map)) {
                plugin.removePlayerFromList(mapAlivePlayers.get(map), player.getName());

                String gameOver = "";
                if (enabledMaps.get(map).size() < 2) {
                    gameOver = plugin.getTextManager().get("messages.gameOver");
                }

                for (CommonPlayer mapPlayer : enabledMaps.get(map)) {
                    mapPlayer.sendMessage(plugin.getTextManager().get("messages.playerLeftOngoing") + gameOver);
                }

                if (player.isOnline()) {
                    player.setHealth(20);
                    player.clearPotionEffects();
                    sendToSpawn(player);
                    player.sendMessage(plugin.getTextManager().get("messages.youLeft"));
                }

                gameEnded(map);
            } else {
                boolean sendMessage = !inUseMaps.containsKey(map);
                if (inUseMaps.containsKey(map)) {
                    if (inUseMaps.get(map) == 1) {
                        sendMessage = true;
                    }
                }

                if (sendMessage) {
                    String message = plugin.getTextManager().get("messages.playerLeft", player.getDisplayName(), enabledMaps.get(map).size(), getMapMaxPlayers(map));
                    for (CommonPlayer mapPlayer : enabledMaps.get(map)) {
                        mapPlayer.sendMessage(message);
                    }

                    if (player.isOnline()) {
                        player.sendMessage(plugin.getTextManager().get("messages.youLeft"));
                        sendToSpawn(player);
                    }
                } else {
                    sendToSpawn(player);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void startGame(String map) {
        if (enabledMaps.get(map).size() != getMapMaxPlayers(map)) {
            for (CommonPlayer mapPlayer : enabledMaps.get(map)) {
                mapPlayer.sendMessage(plugin.getTextManager().get("messages.gameStartCancelled"));
            }
            inUseMaps.remove(map);
            return;
        }

        inUseMaps.replace(map, 0);
        ongoingMaps.add(map);
        mapAlivePlayers.put(map, new ArrayList<>());

        int playerNum = 1;
        for (CommonPlayer mapPlayer : enabledMaps.get(map)) {
            mapAlivePlayers.get(map).add(mapPlayer);
            mapPlayer.setHealth(20);
            mapPlayer.clearInventory();
            mapPlayer.setFireTicks(0);
            mapPlayer.clearPotionEffects();

            mapPlayer.sendMessage(plugin.getTextManager().get("messages.gameStarted"));
            mapPlayer.sendTitle(plugin.getTextManager().get("messages.gameStartedTitle"), " ", 0, 10, 10);
            mapPlayer.setGameMode(GameMode.SURVIVAL);

            if (plugin.getKitManager().getKits().size() > 0) {
                String kitName;

                if (!allMaps.get(map).equalsIgnoreCase("")) {
                    kitName = allMaps.get(map);
                } else if (plugin.getKitManager().getPlayerKits().containsKey(mapPlayer.getName())) {
                    kitName = plugin.getKitManager().getPlayerKits().get(mapPlayer.getName());
                } else {
                    int index = new Random().nextInt(plugin.getKitManager().getKits().size());
                    kitName = plugin.getKitManager().getKits().get(index);
                }
                if (!kitName.equalsIgnoreCase("")) {
                    plugin.getKitManager().loadKitToPlayer(mapPlayer, kitName);
                }
                mapPlayer.sendMessage(plugin.getTextManager().get("messages.gotKit", kitName));
            } else {
                if (mapPlayer.isOp()) {
                    mapPlayer.sendMessage(plugin.getTextManager().get("messages.considerAddingKits"));
                }
            }

            if (plugin.getMapsYML().contains(map + ".spawn" + playerNum)) {
                Location location = plugin.getMapsYML().getLocation(map + ".spawn" + playerNum);
                mapPlayer.teleport(location);
            } else {
                mapPlayer.sendMessage(plugin.getTextManager().get("general.errors.couldNotGetSpawn"));
                Location location = plugin.getMapsYML().getLocation(map + ".world");
                mapPlayer.teleport(location);
            }
            playerNum++;
        }
    }

    public void gameEnded(String map) {
        for (CommonPlayer player : enabledMaps.get(map)) {
            if (!player.isOnline()) {
                continue;
            }

            player.setHealth(20);
            player.setFireTicks(0);
            player.clearPotionEffects();
            sendToSpawn(player);
            playerMaps.remove(player.getName());
        }

        mapAlivePlayers.remove(map);
        enabledMaps.get(map).clear();
        ongoingMaps.remove(map);

        Location pos1 = plugin.getMapsYML().getLocation(map + ".pos1");
        Location pos2 = plugin.getMapsYML().getLocation(map + ".pos2");
        plugin.getWorldUtils().resetMap(pos1, pos2);
        plugin.getEntityUtils().killEntities(pos1, pos2);

        inUseMaps.remove(map);
    }

    public void tryMap(String map, CommonPlayer player) {
        if (map.equals("")) {
            player.sendMessage(plugin.getTextManager().get("general.errors.noMapsAvailable"));
        }

        if (enabledMaps.containsKey(map)) {
            if (enabledMaps.get(map).size() >= getMapMaxPlayers(map)) {
                player.sendMessage(plugin.getTextManager().get("general.errors.noMapSlotsAvailable"));
                return;
            }
        } else {
            player.sendMessage(plugin.getTextManager().get("general.errors.notAMap"));
            return;
        }

        queuePlayerToMap(player, map);
    }
}
