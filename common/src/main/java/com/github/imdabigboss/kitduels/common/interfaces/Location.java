package com.github.imdabigboss.kitduels.common.interfaces;

public interface Location {
    String getWorld();
    double getX();
    double getY();
    double getZ();
    double getYaw();
    double getPitch();
    float getYawFloat();
    float getPitchFloat();

    void setWorld(String world);
    void setX(double x);
    void setY(double y);
    void setZ(double z);
    void setYaw(double yaw);
    void setPitch(double pitch);

    boolean hasWorld();
}
