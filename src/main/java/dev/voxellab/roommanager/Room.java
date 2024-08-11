package dev.voxellab.roommanager;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.voxellab.gameframework.GamePlugin;
import dev.voxellab.roommanager.config.NPCConfig;
import dev.voxellab.worldedit.WeAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class Room extends RoomRectangle {
    static int roomId = 0;

    // https://chatgpt.com/c/3e905186-21a5-425d-b813-ed679f2fa8b8
    public static HashMap<Integer, Room> rooms = new HashMap<>();
    public static HashMap<Player, Room> playerRoom = new HashMap<>();
    public static HashMap<Player, Location> playerLastAvailableLocation = new HashMap<>();
    public static HashMap<Entity, Integer> entityRoom = new HashMap<>();
    public static HashMap<NPC, Room> npcRoom = new HashMap<>();
    public static HashMap<NPC, ConversationID> conversation = new HashMap<>();
    GamePlugin gamePlugin;

    public HashSet<Player> joinedPlayers = new HashSet<>();
    public HashSet<NPC> npcs = new HashSet<>();

    int id;

    int innerX1;
    int innerZ1;

    public Location spawnLocation;

    int playerMax;

    public static Room getRoomById(int id) {
        return rooms.get(id);
    }

    public static Room getRoomByPlayer(Player player) {
        return playerRoom.get(player);
    }

    public static void onEntityDeath(Entity entity) {
        entityRoom.remove(entity);
    }

    public Room (RoomRectangle prepare) {
        super(prepare.x1, prepare.z1, prepare.config);

        this.innerX1 = x1 + roomGap;
        this.innerZ1 = z1 + roomGap;
        id = Room.roomId++;

        int[] spawnPos = new int[]{
                config.spawnX - config.mapX1 + innerX1,
                config.spawnY,
                config.spawnZ - config.mapZ1 + innerZ1
        };

        spawnLocation = new Location(
                Bukkit.getServer().getWorld("rooms_empty_sample"),
                spawnPos[0],
                spawnPos[1],
                spawnPos[2]
        );

        gamePlugin = GamePlugin.getMapPlugin(config.mapId, this);

        rooms.put(id, this);

        NPCConfig.loadAll(config.mapId).forEach(npcConfig -> {
            try {
                loadNPC(npcConfig);
            } catch (ObjectNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadNPC(NPCConfig npcConfig) throws ObjectNotFoundException {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcConfig.npcName);
        npc.spawn(fromSample(npcConfig.npcX, npcConfig.npcY, npcConfig.npcZ));
        SkinTrait skinTrait = npc.getTrait(SkinTrait.class);
        skinTrait.setSkinName(npcConfig.skinName);
        npcs.add(npc);
        npcRoom.put(npc, this);
        String packageIdStr = npcConfig.conversationId.split("\\.")[0];
        String conversationIdStr = npcConfig.conversationId.split("\\.")[1];
        ConversationID conversationId = new ConversationID(
                Config.getPackages().get(packageIdStr),
                conversationIdStr
        );

        conversation.put(npc, conversationId);
    }

    public void spawn(Entity entity) {
        entityRoom.put(entity, id);
    }

    public Location fromSample(double x, double y, double z) {
        return new Location(
                Bukkit.getWorld("rooms_empty_sample"),
                x - config.mapX1 + innerX1, y, z - config.mapZ1 + innerZ1);
    }

    public boolean join(Player player) {
        if (joinedPlayers.size() >= playerMax) return false;
        if (playerRoom.containsKey(player)) return false;
        joinedPlayers.add(player);
        playerRoom.put(player, this);
        player.teleport(spawnLocation);
        return true;
    }

    public void leave(Player player) {
        if (!joinedPlayers.contains(player)) return;
        joinedPlayers.remove(player);
        playerRoom.remove(player);
    }

    public void playerLocationCorrection(Player player) {
        player.teleport(playerLastAvailableLocation.get(player));
    }
    
    public boolean isPlayerInRoom(Player player) {
        return joinedPlayers.contains(player);
    }

    protected boolean isPlayerInRoomLocation(Player player) {
         if (player.getLocation().getBlockX() >= innerX1 &&
                player.getLocation().getBlockX() <= x2 - roomGap &&
                player.getLocation().getBlockZ() >= innerZ1 &&
                player.getLocation().getBlockZ() <= z2 - roomGap) {
             playerLastAvailableLocation.put(player, player.getLocation());
             return true;
         }
         return false;
    }

    public boolean isEntityInRoom(Entity entity) {
        return entity.getLocation().getBlockX() >= innerX1 &&
                entity.getLocation().getBlockX() <= x2 - roomGap &&
                entity.getLocation().getBlockZ() >= innerZ1 &&
                entity.getLocation().getBlockZ() <= z2 - roomGap;
    }

    public CompletableFuture<Integer> initRoom() {
        playerMax = config.playerMaxCount;
        CompletableFuture<Integer> faweTaskPromise = new CompletableFuture<>();
        FaweAPI.getTaskManager().async(()->{
            Location loc1 = new Location(Bukkit.getServer().getWorld("sample1"), config.mapX1, -64, config.mapZ1);
            Location loc2 = new Location(Bukkit.getServer().getWorld("sample1"), config.mapX2, 320, config.mapZ2);
            Clipboard clipboard = WeAPI.copyClipboard(loc1, loc2, true);
            WeAPI.placeClipboard(
                    clipboard,
                    new Location(Bukkit.getServer().getWorld("rooms_empty_sample"), innerX1, -64, innerZ1),
                    false,
                    true
            );
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), ()->{
                faweTaskPromise.complete(id);
            });
        });
        return faweTaskPromise;
    }

    public void reloadPlugin() {
        gamePlugin.unload();
        gamePlugin = GamePlugin.getMapPlugin(config.mapId, this);
    }

    public void unloadNPCs() {
        npcs.forEach(NPC::destroy);
        npcs.forEach(npc -> npcRoom.remove(npc));
        npcs.clear();
    }

    public CompletableFuture<Void> deleteRoom() {
        CompletableFuture<Void> faweTaskPromise = new CompletableFuture<>();
        gamePlugin.unload();
        unloadNPCs();

        FaweAPI.getTaskManager().async(()->{
            WeAPI.clearArea(
                    new Location(Bukkit.getServer().getWorld("rooms_empty_sample"),
                            innerX1,
                            -64,
                            innerZ1
                    ),
                    new Location(Bukkit.getServer().getWorld("rooms_empty_sample"),
                            (x2 - roomGap),
                            320,
                            (z2 - roomGap)
                    )
            );

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), ()->{
                faweTaskPromise.complete(null);
            });

            rooms.remove(id);
        });
        return faweTaskPromise;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Room) {
            return ((Room) obj).id == id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Room { at (" + innerX1 + ", " + innerZ1 + ") }";
    }

}
