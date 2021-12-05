package com.github.imdabigboss.kitduels.nukkit.interfaces;

import cn.nukkit.Server;

public class Location implements com.github.imdabigboss.kitduels.common.interfaces.Location {
    private cn.nukkit.level.Location location;
    private Server server;

    public Location(cn.nukkit.level.Location location, Server server) {
        this.location = location;
        this.server = server;
    }

    public Location(com.github.imdabigboss.kitduels.common.interfaces.Location location, Server server) {
        this.location = new cn.nukkit.level.Location(location.getX(), location.getY(), location.getZ(), location.getYawFloat(), location.getPitchFloat());
        this.location.setLevel(server.getLevelByName(location.getWorld()));
        this.server = server;
    }

    @Override
    public void setWorld(String world) {
        this.location.setLevel(this.server.getLevelByName(world));
    }

    @Override
    public String getWorld() {
        return this.location.getLevel().getName();
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
        return (float) this.location.getYaw();
    }

    @Override
    public float getPitchFloat() {
        return (float) this.location.getPitch();
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
        this.location.yaw = yaw;
    }

    @Override
    public void setPitch(double pitch) {
        this.location.pitch = pitch;
    }

    @Override
    public boolean hasWorld() {
        return this.location.getLevel() != null;
    }

    public cn.nukkit.level.Location toNukkit() {
        return this.location;
    }
}
