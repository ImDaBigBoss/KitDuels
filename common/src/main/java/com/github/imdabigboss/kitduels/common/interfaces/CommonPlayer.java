package com.github.imdabigboss.kitduels.common.interfaces;

import com.github.imdabigboss.kitduels.common.util.GameMode;
import com.github.imdabigboss.kitduels.common.util.Sounds;

import java.util.UUID;

public interface CommonPlayer {
    String getName();
    String getDisplayName();
    UUID getUUID();
    String getWorld();

    boolean isOnline();
    boolean isOp();

    default void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }
    void sendMessage(String message);

    void teleport(Location location);
    Location getLocation();

    void setGameMode(GameMode gameMode);
    GameMode getGameMode();

    void clearInventory();

    void setHealth(double health);
    double getHealth();

    void clearPotionEffects();

    void setFireTicks(int fireTicks);
    int getFireTicks();

    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    void playSound(Sounds sound, float volume, float pitch);

    boolean isFlying();
    void setFlying(boolean flying);
}
