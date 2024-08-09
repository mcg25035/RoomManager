package dev.voxellab.roommanager;

import dev.voxellab.roommanager.config.MapConfig;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class RoomsManager {
    public static HashSet<Room> rooms = new HashSet<>();
    static int MAX_TRY = 200;

    public static boolean isRoomPrepareAvaliable(RoomRectangle prepare) {
        for (Room room : rooms) {
            if (!room.isOverlap(prepare)) continue;
            return false;
        }
        return true;
    }

    public static CompletableFuture<Integer> createRoom(MapConfig config) {
        int x, z;
        int tries = 0;
        RoomRectangle prepare;
        do{
            if (tries++ > MAX_TRY) return CompletableFuture.completedFuture(-1);
            x = (int) (Math.random() * 100000);
            z = (int) (Math.random() * 100000);
            prepare = new RoomRectangle(x, z, config);
        } while (!isRoomPrepareAvaliable(prepare));

        Room room = new Room(prepare);
        rooms.add(room);
        return room.initRoom();
    }

    public static CompletableFuture<Void> deleteRoom(int id) {
        Room room = Room.getRoomById(id);
        if (room == null) return CompletableFuture.completedFuture(null);
        rooms.remove(room);
        room.gamePlugin.unload();
        return room.deleteRoom();
    }
}
