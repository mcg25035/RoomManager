package dev.voxellab.roommanager;

import io.papermc.paper.event.entity.EntityMoveEvent;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Events implements Listener {
    @EventHandler
    void PlayerMoveEvent(PlayerMoveEvent event) {
        Room roomPlayerIn = Room.playerRoom.get(event.getPlayer());
        if (roomPlayerIn == null) return;
        if (roomPlayerIn.isPlayerInRoomLocation(event.getPlayer())) return;
        roomPlayerIn.playerLocationCorrection(event.getPlayer());
    }

    @EventHandler
    void ProjectileHitEvent(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        EnderPearl enderPearl = (EnderPearl) event.getEntity();
        Player player = (Player) enderPearl.getShooter();
        Room roomPlayerIn = Room.playerRoom.get(player);
        if (roomPlayerIn == null) return;
        if (roomPlayerIn.isEntityInRoom(enderPearl)) return;
        enderPearl.remove();
        event.setCancelled(true);
    }

    @EventHandler
    void VehicleMoveEvent(VehicleMoveEvent event) {
        List<Entity> passengers = event.getVehicle().getPassengers();
        for (Entity i : passengers) {
            if (!(i instanceof Player player)) continue;
            Room roomPlayerIn = Room.playerRoom.get(player);
            if (roomPlayerIn == null) continue;
            if (roomPlayerIn.isEntityInRoom(event.getVehicle())) continue;
            event.getVehicle().remove();
        }
    }

    @EventHandler
    void NPCRightClickEvent(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        ConversationID conversationId = Room.conversation.get(npc);
        if (conversationId == null) return;
        BetonQuestLogger logger = BetonQuest.getInstance().getLoggerFactory().create(Main.getInstance());
        new Conversation(
                logger,
                PlayerConverter.getID(event.getClicker()),
                conversationId,
                npc.getEntity().getLocation()
        );
    }

    @EventHandler
    void EntityMoveEvent(EntityMoveEvent event) {
        List<Entity> passengers = event.getEntity().getPassengers();
        for (Entity i : passengers) {
            if (!(i instanceof Player player)) continue;
            Room roomPlayerIn = Room.playerRoom.get(player);
            if (roomPlayerIn == null) continue;
            if (roomPlayerIn.isEntityInRoom(event.getEntity())) continue;
            event.getEntity().remove();
        }
    }


//    HashMap<Player, Integer> playerBreaksInTime = new HashMap<>();
//    HashMap<Player, Instant> playerLastAlert = new HashMap<>();
//    BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
//        playerBreaksInTime.clear();
//    }, 0, 20 * 5);
//
//    @EventHandler
//    void PlayerInteractEvent(PlayerInteractEvent event) {
//        if (!event.getPlayer().getScoreboardTags().contains("building-mode")) return;
//        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
//        if (event.getClickedBlock().getLocation().getBlockY() == -64) return;
//        int breaks = playerBreaksInTime.getOrDefault(event.getPlayer(), 0);
//        Instant lastAlert = playerLastAlert.getOrDefault(event.getPlayer(), Instant.MIN);
//        if (breaks >= 20 && lastAlert.isBefore(Instant.now().minusSeconds(1))) {
//            playerLastAlert.put(event.getPlayer(), Instant.now());
//            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize(
//                    "<gold>[<red>快速挖掘</red>]</gold> " +
//                            "<gradient:gold:red>已達到數量上限，冷卻中...</gradient>"
//            ));
//        }
//        if (breaks >= 20) {
//            return;
//        }
//        playerBreaksInTime.put(event.getPlayer(), breaks + 1);
//        event.getPlayer().breakBlock(event.getClickedBlock());
//    }

//    HashSet<int[]> positions = new HashSet<>();
//    @EventHandler
//    void BlockBreakEvent(BlockBreakEvent event) {
//        if (event.getBlock().getType() != Material.GOLD_BLOCK) return;
//        positions.add(new int[]{
//                event.getBlock().getLocation().getBlockX(),
//                event.getBlock().getLocation().getBlockY(),
//                event.getBlock().getLocation().getBlockZ()
//        });
//        StringBuilder sb = new StringBuilder();
//        sb.append("{");
//        for (int[] i : positions) {
//            sb.append("{").append(i[0]).append(",").append(i[1]).append(",").append(i[2]).append("}");
//            sb.append(",");
//        }
//        sb.append("}");
//        System.out.println(sb);
//    }


//    @EventHandler
//    void BlockBreakEvent(BlockBreakEvent event) {
//        if (event.getBlock().getLocation().getBlockY() != -64) return;
//        event.setCancelled(true);
//    }

    @EventHandler
    void PlayerJoinEvent(PlayerJoinEvent event) {

    }

    @EventHandler
    void PlayerQuitEvent(PlayerQuitEvent event) {

    }

    @EventHandler
    void EntityDeathEvent(EntityDeathEvent event) {
        Room.onEntityDeath(event.getEntity());
    }

}
