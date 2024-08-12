package dev.voxellab.roommanager;

import dev.voxellab.roommanager.config.MapConfig;
import dev.voxellab.roommanager.exceptions.*;
import net.royawesome.jlibnoise.module.source.Const;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class RoomsManager {
    public static HashSet<Room> rooms = new HashSet<>();
    public static HashMap<String, HashSet<Room>> constructingRooms = new HashMap<>();
    static int MAX_TRY = 200;

    public static boolean isRoomPrepareAvailable(RoomRectangle prepare) {
        for (Room room : rooms) {
            if (!room.isOverlap(prepare)) continue;
            return false;
        }
        return true;
    }

    public static void joinRoom(Player player, int id)
            throws RoomFullException, PlayerInAnotherRoomException, RoomNotFoundException {
        Room room = Room.getRoomById(id);
        if (room == null) throw new RoomNotFoundException("");
        room.join(player);
    }

    public static void quitRoom(Player player) throws PlayerNotInRoomException {
        Room room = Room.getRoomByPlayer(player);
        if (room == null) throw new PlayerNotInRoomException("");
        room.leave(player);
    }

    public static void postPlayerLeave(Player player) {
        player.teleport(
                new Location(Bukkit.getWorld("rooms_empty_sample"), 8, -61, 8)
        );
    }

    public static CompletableFuture<Integer> createRoom(MapConfig config) {
        int x, z;
        int tries = 0;
        RoomRectangle prepare;
        do{
            if (tries++ > MAX_TRY) return CompletableFuture.failedFuture(new RoomSpaceNotEnoughException(""));
            x = (int) (Math.random() * 100000);
            z = (int) (Math.random() * 100000);
            prepare = new RoomRectangle(x, z, config);
        } while (!isRoomPrepareAvailable(prepare));

        Room room = new Room(prepare);
        rooms.add(room);
        return room.initRoom();
    }

    public static CompletableFuture<Void> batchDeleteRooms(int startId, int endId) {
        for (int i = startId; i <= endId; i++) {
            Room room = Room.getRoomById(i);
            if (room == null) continue;
            rooms.remove(room);
            room.deleteRoom();
        }
        return CompletableFuture.completedFuture(null);
    }

    public static CompletableFuture<Void> deleteRoom(int id) {
        Room room = Room.getRoomById(id);
        if (room == null) return CompletableFuture.failedFuture(new RoomNotFoundException(""));
        rooms.remove(room);
        return room.deleteRoom();
    }
}
