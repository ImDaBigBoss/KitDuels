package com.github.imdabigboss.kitduels;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    private KitDuels plugin;

    public EventListener(KitDuels plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        KitDuels.sendToSpawn(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MapManager.removePlayerFromMap(player, false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (KitDuels.playerMaps.containsKey(player)) {
            String map = KitDuels.playerMaps.get(player);
            if (KitDuels.ongoingMaps.contains(map)) {
                if (player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    player.setHealth(20);
                    KitDuels.mapAlivePlayers.get(map).remove(player);

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        public void run() {
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    }, 20L);

                    for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                        mapPlayer.sendMessage(player.getDisplayName() + ChatColor.GREEN + " was killed!");
                        mapPlayer.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                    }

                    if (KitDuels.mapAlivePlayers.get(map).size() == 1) {
                        Player winPlayer = null;
                        for (Player alivePlayer : KitDuels.mapAlivePlayers.get(map)) {
                            winPlayer = alivePlayer;
                        }

                        KitDuels.ongoingMaps.remove(map);

                        for (Player mapPlayer : KitDuels.enabledMaps.get(map)) {
                            mapPlayer.setHealth(20);
                            mapPlayer.setFoodLevel(20);
                            mapPlayer.sendMessage(winPlayer.getDisplayName() + " wins!");
                            if (mapPlayer == winPlayer) {
                                mapPlayer.sendTitle(ChatColor.GOLD + "VICTORY!", "You won the game", 0, 60, 10);
                            } else {
                                mapPlayer.sendTitle(ChatColor.RED + "DEFEAT!", winPlayer.getDisplayName() + ChatColor.GREEN + " won the game", 0, 60, 10);
                            }
                            mapPlayer.setGameMode(GameMode.ADVENTURE);
                        }

                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                            public void run() {
                                MapManager.gameEnded(map);
                            }
                        }, 10 * 20L);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerDeplete(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getEntity();
        player.setFoodLevel(20);
    }
}
