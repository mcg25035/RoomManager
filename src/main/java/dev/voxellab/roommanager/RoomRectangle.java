package dev.voxellab.roommanager;

import org.bukkit.Location;

public class RoomRectangle {
    static int roomGap = 5;
    int x1;
    int z1;
    int x2;
    int z2;
    MapConfig config;

    public RoomRectangle(int x, int z, MapConfig config) {
        this.x1 = x;
        this.z1 = z;
        this.x2 = x + 2*roomGap + (config.mapX2 - config.mapX1);
        this.z2 = z + 2*roomGap + (config.mapZ2 - config.mapZ1);
        this.config = config;
    }

    public boolean isOverlap(RoomRectangle other) {
        return this.x2 > other.x1 && this.x1 < other.x2 && this.z2 > other.z1 && this.z1 < other.z2;
    }
}
