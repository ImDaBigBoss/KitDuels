package com.github.imdabigboss.kitduels.nukkit.util;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.NukkitRunnable;

public abstract class CountdownTimer {
    private int time;

    protected Runnable task;
    protected final Plugin plugin;

    public CountdownTimer(int time, Plugin plugin) {
        this.time = time;
        this.plugin = plugin;
    }

    public abstract void count(int current);

    public final void start() {
        task = new NukkitRunnable() {
            @Override
            public void run() {
                count(time);
                if (time-- <= 0) cancel();
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
