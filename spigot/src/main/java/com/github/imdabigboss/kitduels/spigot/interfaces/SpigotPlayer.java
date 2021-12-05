package com.github.imdabigboss.kitduels.spigot.interfaces;

import com.github.imdabigboss.kitduels.common.util.GameMode;

import com.github.imdabigboss.kitduels.common.util.Sounds;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

public class SpigotPlayer implements com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer {
    private Player player;

    public SpigotPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public String getDisplayName() {
        return this.player.getDisplayName();
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public String getWorld() {
        return this.player.getWorld().getName();
    }

    @Override
    public boolean isOnline() {
        return this.player.isOnline();
    }

    @Override
    public boolean isOp() {
        return this.player.isOp();
    }

    @Override
    public void sendMessage(String message) {
        this.player.sendMessage(message);
    }

    @Override
    public void teleport(com.github.imdabigboss.kitduels.common.interfaces.Location location) {
        player.teleport(new Location(location, player.getServer()).toBukkit());
    }

    @Override
    public Location getLocation() {
        return new Location(this.player.getLocation(), this.player.getServer());
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        if (gameMode == GameMode.CREATIVE) {
            this.player.setGameMode(org.bukkit.GameMode.CREATIVE);
        } else if (gameMode == GameMode.SURVIVAL) {
            this.player.setGameMode(org.bukkit.GameMode.SURVIVAL);
        } else if (gameMode == GameMode.ADVENTURE) {
            this.player.setGameMode(org.bukkit.GameMode.ADVENTURE);
        } else if (gameMode == GameMode.SPECTATOR) {
            this.player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        }
    }

    @Override
    public GameMode getGameMode() {
        org.bukkit.GameMode gameMode = this.player.getGameMode();
        if (gameMode == org.bukkit.GameMode.CREATIVE) {
            return GameMode.CREATIVE;
        } else if (gameMode == org.bukkit.GameMode.SURVIVAL) {
            return GameMode.SURVIVAL;
        } else if (gameMode == org.bukkit.GameMode.ADVENTURE) {
            return GameMode.ADVENTURE;
        } else if (gameMode == org.bukkit.GameMode.SPECTATOR) {
            return GameMode.SPECTATOR;
        } else {
            return null;
        }
    }

    @Override
    public void clearInventory() {
        this.player.getInventory().clear();
    }

    @Override
    public void setHealth(double health) {
        this.player.setHealth(health);
    }

    @Override
    public double getHealth() {
        return this.player.getHealth();
    }

    @Override
    public void setFoodLevel(int foodLevel) {
        this.player.setFoodLevel(foodLevel);
    }

    @Override
    public int getFoodLevel() {
        return this.player.getFoodLevel();
    }

    @Override
    public void clearPotionEffects() {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public void setFireTicks(int fireTicks) {
        this.player.setFireTicks(fireTicks);
    }

    @Override
    public int getFireTicks() {
        return this.player.getFireTicks();
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void playSound(Sounds sound, float volume, float pitch) {
        if (sound == Sounds.NOTE_BLOCK) {
            this.player.getWorld().playSound(this.player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, volume, pitch);
        } else if (sound == Sounds.LIGHTNING) {
            this.player.getWorld().playSound(this.player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, volume, pitch);
        }
    }

    @Override
    public boolean isFlying() {
        return this.player.isFlying();
    }

    @Override
    public void setFlying(boolean flying) {
        this.player.setFlying(flying);
    }
}
