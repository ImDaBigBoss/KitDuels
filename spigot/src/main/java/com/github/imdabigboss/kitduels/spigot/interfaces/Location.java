package com.github.imdabigboss.kitduels.spigot.interfaces;

import org.bukkit.Server;
import org.bukkit.World;

public class Location implements com.github.imdabigboss.kitduels.common.interfaces.Location {
    private org.bukkit.Location location;
    private Server server;

    public Location(com.github.imdabigboss.kitduels.common.interfaces.Location location, Server server) {
        this.server = server;
        this.location = new org.bukkit.Location(server.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ(), location.getYawFloat(), location.getPitchFloat());
    }

    public Location(org.bukkit.Location location, Server server) {
        this.location = location;
        this.server = server;
    }

    @Override
    public void setWorld(String world) {
        this.location.setWorld(server.getWorld(world));
    }

    @Override
    public String getWorld() {
        return this.location.getWorld().getName();
    }

    @Override
    public double getX() {
        return this.location.getX();
    }

    @Override
    public double getY() {
        return this.location.getY();
    }

    @Override
    public double getZ() {
        return this.location.getZ();
    }

    @Override
    public double getYaw() {
        return this.location.getYaw();
    }

    @Override
    public double getPitch() {
        return this.location.getPitch();
    }

    @Override
    public float getYawFloat() {
        return this.location.getYaw();
    }

    @Override
    public float getPitchFloat() {
        return this.location.getPitch();
    }

    @Override
    public void setX(double x) {
        this.location.setX(x);
    }

    @Override
    public void setY(double y) {
        this.location.setY(y);
    }

    @Override
    public void setZ(double z) {
        this.location.setZ(z);
    }

    @Override
    public void setYaw(double yaw) {
        this.location.setYaw((float) yaw);
    }

    @Override
    public void setPitch(double pitch) {
        this.location.setPitch((float) pitch);
    }

    @Override
    public boolean hasWorld() {
        return this.location.getWorld() != null;
    }

    public void setYaw(float yaw) {
        this.location.setYaw(yaw);
    }

    public void setPitch(float pitch) {
        this.location.setPitch(pitch);
    }

    public org.bukkit.Location toBukkit() {
        return this.location;
    }

    public World getBukkitWorld() {
        return server.getWorld(this.getWorld());
    }
}
