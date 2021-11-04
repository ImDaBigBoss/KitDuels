package com.github.imdabigboss.kitduels;

import com.github.imdabigboss.kitduels.managers.GameManager;
import com.github.imdabigboss.kitduels.managers.MapManager;
import com.github.imdabigboss.kitduels.managers.StatsManager;
import com.github.imdabigboss.kitduels.util.PlayerUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener {
    private KitDuels plugin;

    public EventListener(KitDuels plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameManager.sendToSpawn(player);
        PlayerUtils.registerPlayer(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MapManager.removePlayerFromMap(player);
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
                if (event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
                    event.setCancelled(true);
                    return;
                }

                if (player.getHealth() - event.getFinalDamage() <= 0) {
                    event.setCancelled(true);
                    player.setHealth(20);
                    KitDuels.mapAlivePlayers.get(map).remove(player);

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        public void run() {
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    }, 20L);

                    World world = plugin.getServer().getWorld(map);
                    if (world != null) {
                        world.strikeLightning(player.getLocation());
                    }

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
                                mapPlayer.setGameMode(GameMode.ADVENTURE);
                                StatsManager.addPlayerWin(mapPlayer);
                            } else {
                                mapPlayer.sendTitle(ChatColor.RED + "DEFEAT!", winPlayer.getDisplayName() + ChatColor.GREEN + " won the game", 0, 60, 10);
                                mapPlayer.setGameMode(GameMode.SPECTATOR);
                            }
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
            if (KitDuels.disableDamageWhenNotInGame) {
                if (KitDuels.disableDamageInSelectWorlds) {
                    if (KitDuels.lobbyWorlds.contains(player.getWorld().getName())) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerDeplete(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getEntity();
        player.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!KitDuels.playerMaps.containsKey(player)) {
            return;
        }
        String playerMap = KitDuels.playerMaps.get(player);
        if (KitDuels.ongoingMaps.contains(playerMap)) {
            return;
        }

        ItemStack item = player.getInventory().getItem(EquipmentSlot.HAND);
        if (item.getType() == Material.CHEST) {
            GameManager.openKitSelectGUIPlayer(player);
        } else if (item.getType() == Material.RED_DYE) {
            MapManager.removePlayerFromMap(player);
        }
    }
}
