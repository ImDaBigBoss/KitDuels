package com.github.imdabigboss.kitduels.spigot;

import com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer;
import com.github.imdabigboss.kitduels.common.util.GameMode;
import com.github.imdabigboss.kitduels.common.util.Sounds;
import com.github.imdabigboss.kitduels.spigot.interfaces.SpigotPlayer;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        SpigotPlayer player = new SpigotPlayer(event.getPlayer());
        plugin.getGameManager().sendToSpawn(player);
        plugin.getPlayerUtils().registerPlayer(player);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SpigotPlayer player = new SpigotPlayer(event.getPlayer());
        plugin.getGameManager().removePlayerFromMap(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        SpigotPlayer player = new SpigotPlayer((Player) event.getEntity());

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

                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                        public void run() {
                            player.setGameMode(GameMode.SPECTATOR);
                        }
                    }, 20L);

                    World world = plugin.getServer().getWorld(map);
                    if (world != null) {
                        world.strikeLightning(player.getLocation().toBukkit());
                    }

                    String deathMessage = plugin.getTextManager().get("messages.playerKilled", player.getDisplayName());
                    if (event instanceof EntityDamageByEntityEvent) {
                        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
                        if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                            SpigotPlayer damager = new SpigotPlayer((Player) entityDamageByEntityEvent.getDamager());
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
                            mapPlayer.setFoodLevel(20);
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

                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                            public void run() {
                                plugin.getGameManager().gameEnded(map);
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
    public void onHungerDeplete(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (plugin.getGameManager().playerMaps.containsKey(player.getName())) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        } else {
            if (KitDuels.disableDamageWhenNotInGame) {
                if (KitDuels.disableDamageInSelectWorlds) {
                    if (KitDuels.lobbyWorlds.contains(player.getWorld().getName())) {
                        event.setCancelled(true);
                        player.setFoodLevel(20);
                    }
                } else {
                    event.setCancelled(true);
                    player.setFoodLevel(20);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        SpigotPlayer player = new SpigotPlayer(event.getPlayer());
        if (!plugin.getGameManager().playerMaps.containsKey(player.getName())) {
            return;
        }
        String playerMap = plugin.getGameManager().playerMaps.get(player.getName());
        if (plugin.getGameManager().ongoingMaps.contains(playerMap)) {
            return;
        }

        ItemStack item = player.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
        if (item.getType() == Material.CHEST) {
            plugin.getGUIManager().showKitSelector(player);
        } else if (item.getType() == Material.RED_DYE) {
            plugin.getGameManager().removePlayerFromMap(player);
        }
    }
}
