package dev.voxellab.utils;

public class PointTools {
    public static int[][] normalizeCuboidPoints(int x1, int z1, int x2, int z2) {
        int[][] points = new int[2][2];
        points[0][0] = Math.min(x1, x2);
        points[0][1] = Math.min(z1, z2);
        points[1][0] = Math.max(x1, x2);
        points[1][1] = Math.max(z1, z2);
        return points;
    }
}
