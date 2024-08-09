package dev.voxellab.roommanager;

import dev.voxellab.roommanager.config.MapConfig;

public class RoomRectangle {
    public static int roomGap = 5;
    public int x1;
    public int z1;
    public int x2;
    public int z2;
    public MapConfig config;

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
