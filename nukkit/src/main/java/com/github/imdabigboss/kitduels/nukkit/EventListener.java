package com.github.imdabigboss.kitduels.nukkit;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.interfaces.Location;
import com.github.imdabigboss.kitduels.nukkit.interfaces.NukkitPlayer;

import com.github.imdabigboss.kitduels.common.util.GameMode;
import com.github.imdabigboss.kitduels.common.util.Sounds;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerFoodLevelChangeEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
    private KitDuels plugin;

    public EventListener(KitDuels plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        NukkitPlayer player = new NukkitPlayer(event.getPlayer());
        plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin, () -> {
            if (plugin.getConfigYML().contains("lobbySpawn")) {
                Location location = plugin.getConfigYML().getLocation("lobbySpawn");
                player.teleport(location);
            } else {
                Location location = plugin.getWorldUtils().getSpawnLocation();
                player.teleport(location);
            }
        }, 20);
        plugin.getPlayerUtils().registerPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLeave(PlayerQuitEvent event) {
        NukkitPlayer player = new NukkitPlayer(event.getPlayer());
        plugin.getGameManager().removePlayerFromMap(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        NukkitPlayer player = new NukkitPlayer((Player) event.getEntity());

        if (plugin.getGameManager().playerMaps.containsKey(player.getName())) {
            String map = plugin.getGameManager().playerMaps.get(player.getName());
            if (plugin.getGameManager().ongoingMaps.contains(map)) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
                    event.setCancelled(true);
                    return;
                }

                if (player.getHealth() - event.getFinalDamage() <= 0 && !plugin.getPlayerUtils().doesPlayerHaveTotem(player)) {
                    event.setCancelled(true);
                    player.setHealth(20);
                    plugin.removePlayerFromList(plugin.getGameManager().mapAlivePlayers.get(map), player.getName());


                    plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin, () -> {
                        player.setGameMode(GameMode.SPECTATOR);
                    }, 20);

                    Level world = plugin.getServer().getLevelByName(map);
                    if (world != null) {
                        Player nukkitPlayer = player.getPlayer();

                        AddEntityPacket light = new AddEntityPacket();
                        light.type = 93;
                        light.entityRuntimeId = Entity.entityCount++;
                        light.yaw = (float) nukkitPlayer.getYaw();
                        light.pitch = (float) nukkitPlayer.getPitch();
                        light.x = nukkitPlayer.getFloorX();
                        light.y = nukkitPlayer.getFloorY();
                        light.z = nukkitPlayer.getFloorZ();
                        plugin.getServer().broadcastPacket(nukkitPlayer.getLevel().getPlayers().values(), light);
                        nukkitPlayer.level.addSound(new Vector3(nukkitPlayer.getX(), nukkitPlayer.getY(), nukkitPlayer.getZ()), Sound.AMBIENT_WEATHER_THUNDER);
                    }

                    String deathMessage = plugin.getTextManager().get("messages.playerKilled", player.getDisplayName());
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                            NukkitPlayer damager = new NukkitPlayer((Player) entityDamageByEntityEvent.getDamager());
                            deathMessage = plugin.getTextManager().get("messages.playerKilledByPlayer", player.getDisplayName(), damager.getDisplayName());
                            plugin.getStatsManager().addPlayerKill(damager);
                        }
                    }
                    plugin.getStatsManager().addPlayerDeath(player);

                    for (CommonPlayer mapPlayer : plugin.getGameManager().enabledMaps.get(map)) {
                        mapPlayer.sendMessage(deathMessage);
                        mapPlayer.playSound(Sounds.LIGHTNING, 1, 1);
                    }

                    if (plugin.getGameManager().mapAlivePlayers.get(map).size() == 1) {
                        CommonPlayer winPlayer = null;
                        for (CommonPlayer alivePlayer : plugin.getGameManager().mapAlivePlayers.get(map)) {
                            winPlayer = alivePlayer;
                        }

                        plugin.getGameManager().ongoingMaps.remove(map);

                        for (CommonPlayer mapPlayer : plugin.getGameManager().enabledMaps.get(map)) {
                            mapPlayer.setHealth(20);
                            mapPlayer.sendMessage(plugin.getTextManager().get("messages.playerWon", winPlayer.getDisplayName()));
                            if (mapPlayer.getName().equals(winPlayer.getName())) {
                                mapPlayer.sendTitle(plugin.getTextManager().get("messages.gameOverTitles.win.title"), plugin.getTextManager().get("messages.gameOverTitles.win.subtitle"), 0, 60, 10);
                                mapPlayer.setGameMode(GameMode.ADVENTURE);
                                plugin.getStatsManager().addPlayerWin(mapPlayer);
                            } else {
                                mapPlayer.sendTitle(plugin.getTextManager().get("messages.gameOverTitles.lose.title"), plugin.getTextManager().get("messages.gameOverTitles.lose.subtitle", winPlayer.getDisplayName()), 0, 60, 10);
                                mapPlayer.setGameMode(GameMode.SPECTATOR);
                                plugin.getStatsManager().addPlayerLoss(player);
                            }
                        }

                        plugin.getServer().getScheduler().scheduleDelayedTask(this.plugin, () -> {
                            plugin.getGameManager().gameEnded(map);
                        }, 10 * 20);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            if (KitDuels.disableDamageWhenNotInGame) {
                if (KitDuels.disableDamageInSelectWorlds) {
                    if (KitDuels.lobbyWorlds.contains(player.getWorld())) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerDeplete(PlayerFoodLevelChangeEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGameManager().playerMaps.containsKey(player.getName())) {
            event.setFoodLevel(20);
        } else {
            if (KitDuels.disableDamageWhenNotInGame) {
                if (KitDuels.disableDamageInSelectWorlds) {
                    if (KitDuels.lobbyWorlds.contains(player.getLevel().getName())) {
                        event.setFoodLevel(20);
                    }
                } else {
                    event.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        NukkitPlayer player = new NukkitPlayer(event.getPlayer());
        if (!plugin.getGameManager().playerMaps.containsKey(player.getName())) {
            return;
        }
        String playerMap = plugin.getGameManager().playerMaps.get(player.getName());
        if (plugin.getGameManager().ongoingMaps.contains(playerMap)) {
            return;
        }

        Item item = player.getPlayer().getInventory().getItemInHand();
        if (item.getId() == Item.CHEST) {
            plugin.getGUIManager().showKitSelector(player);
        } else if (item.getId() == Item.DYE) {
            plugin.getGameManager().removePlayerFromMap(player);
        }
    }
}
