package com.github.imdabigboss.kitduels.nukkit.interfaces;

import cn.nukkit.AdventureSettings;
import cn.nukkit.level.Sound;
import com.github.imdabigboss.kitduels.common.util.GameMode;

import cn.nukkit.Player;
import com.github.imdabigboss.kitduels.common.util.Sounds;

import java.util.UUID;

public class NukkitPlayer implements com.github.imdabigboss.kitduels.common.interfaces.CommonPlayer {
    private Player player;

    public NukkitPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
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
        return this.player.getLevel().getName();
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
        this.player.teleport(new Location(location, this.player.getServer()).toNukkit());
    }

    @Override
    public Location getLocation() {
        return new Location(this.player.getLocation(), this.player.getServer());
    }

    @Override
    public void setGameMode(GameMode gameMode) {
        if (gameMode == GameMode.CREATIVE) {
            this.player.setGamemode(1);
        } else if (gameMode == GameMode.SURVIVAL) {
            this.player.setGamemode(0);
        } else if (gameMode == GameMode.ADVENTURE) {
            this.player.setGamemode(2);
        } else if (gameMode == GameMode.SPECTATOR) {
            this.player.setGamemode(3);
        }
    }

    @Override
    public GameMode getGameMode() {
        int gameMode = this.player.getGamemode();

        if (gameMode == 1) {
            return GameMode.CREATIVE;
        } else if (gameMode == 0) {
            return GameMode.SURVIVAL;
        } else if (gameMode == 2) {
            return GameMode.ADVENTURE;
        } else if (gameMode == 3) {
            return GameMode.SPECTATOR;
        } else {
            return null;
        }
    }

    @Override
    public void clearInventory() {
        this.player.getInventory().clearAll();
    }

    @Override
    public void setHealth(double health) {
        this.player.setHealth((float) health);
    }

    @Override
    public double getHealth() {
        return this.player.getHealth();
    }

    @Override
    public void clearPotionEffects() {
        this.player.removeAllEffects();
    }

    @Override
    public void setFireTicks(int fireTicks) {
        int ticks = fireTicks == 0 ? 0 : fireTicks / 20;
        this.player.setOnFire(ticks);
    }

    @Override
    public int getFireTicks() {
        return this.player.fireTicks;
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void playSound(Sounds sound, float volume, float pitch) {
        if (sound == Sounds.NOTE_BLOCK) {
            this.player.getLevel().addSound(this.player.getLocation(), Sound.NOTE_PLING, volume, pitch);
        } else if (sound == Sounds.LIGHTNING) {
            this.player.getLevel().addSound(this.player.getLocation(), Sound.AMBIENT_WEATHER_LIGHTNING_IMPACT, volume, pitch);
        }
    }

    @Override
    public boolean isFlying() {
        return player.getAdventureSettings().get(AdventureSettings.Type.FLYING);
    }

    @Override
    public void setFlying(boolean flying) {
        if (player.getGamemode() == 1) {
            player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, true);
        } else {
            player.getAdventureSettings().set(AdventureSettings.Type.ALLOW_FLIGHT, flying);
        }
        player.getAdventureSettings().set(AdventureSettings.Type.FLYING, flying);
    }
}
