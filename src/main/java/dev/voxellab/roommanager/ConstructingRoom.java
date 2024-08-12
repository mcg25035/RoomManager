package dev.voxellab.roommanager;

import dev.voxellab.roommanager.config.MapConfig;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class ConstructingRoom {
    public static int hash = 0;
    public int hashId = ConstructingRoom.hash++;
    public MapConfig config;
    public HashSet<Player> queuedPlayers = new HashSet<>();

    public boolean queuePlayer(Player player) {
        if (queuedPlayers.size() >= config.playerMaxCount) return false;
        queuedPlayers.add(player);
        return true;
    }

    public void unqueuePlayer(Player player) {
        queuedPlayers.remove(player);
    }

    public ConstructingRoom(MapConfig config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConstructingRoom) {
            return ((ConstructingRoom) obj).hashId == this.hashId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return hashId;
    }
}
