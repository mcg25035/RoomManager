package dev.voxellab.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.voxellab.roommanager.annotations.EngineeringMethod;
import dev.voxellab.roommanager.config.MapConfig;
import dev.voxellab.roommanager.Room;
import dev.voxellab.roommanager.RoomsManager;
import dev.voxellab.roommanager.exceptions.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class RoomManagerCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("room_manager")
                .withSubcommand(create())
                .withSubcommand(delete())
                .withSubcommand(join())
                .withSubcommand(quit())
                .withSubcommand(reloadPlugin())
                .withSubcommand(deleteAll());

    }

    public static CommandAPICommand join() {
        return new CommandAPICommand("join")
                .withArguments(new IntegerArgument("roomId"))
                .executes((sender, args) -> {
                    int roomId = (int) (args.get("roomId"));

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Joining room...</green>"
                    ));

                    try {
                        RoomsManager.joinRoom((Player) sender, roomId);
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Joined room</green>"
                        ));
                    }
                    catch (PlayerInAnotherRoomException ignored) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to join room : you are in another room.</red>"
                        ));
                    }
                    catch (RoomFullException ignored) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to join room : room is full.</red>"
                        ));
                    }
                    catch (RoomNotFoundException ignored) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to join room : specified room not found.</red>"
                        ));
                    }
                });
    }

    public static CommandAPICommand quit() {
        return new CommandAPICommand("quit")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    try{
                        RoomsManager.quitRoom(player);
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Room left.</green>"
                        ));
                    }
                    catch (PlayerNotInRoomException ignored) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to leave room : not in a room.</red>"
                        ));
                    }
                });
    }

    private static CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("map"))
                .executes((sender, args) -> {
                    String map = (String) (args.get("map"));
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Room Creating...</green>"
                    ));

                    MapConfig config;
                    try{
                        config = MapConfig.load(map + ".yml");
                    }
                    catch (MapConfingNotFoundException e) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to create room : map not found.</red>"
                        ));
                        return;
                    }

                    CompletableFuture<Integer> createPromise = RoomsManager.createRoom(config);
                    createPromise.exceptionally((e) -> {
                        String exception = "";
                        if (e instanceof RoomSpaceNotEnoughException) exception = "room space not enough";
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to create room : " + exception + "</red>"
                        ));
                        return null;
                    });
                    createPromise.thenAccept((roomId) -> {
                        if (createPromise.isCompletedExceptionally()) return;
                        Room room = Room.getRoomById(roomId);
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Room " + room + " created with id " + roomId + "</green>"
                        ));
                    });
                });
    }

    private static CommandAPICommand delete() {
        return new CommandAPICommand("delete")
                .withArguments(new IntegerArgument("roomId"))
                .executes((sender, args) -> {
                    int roomId = (int) (args.get("roomId"));

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Room Deleting...</green>"
                    ));

                    CompletableFuture<Void> deletePromise = RoomsManager.deleteRoom(roomId);
                    deletePromise.exceptionally((e) -> {
                        String exception = "";
                        if (e instanceof RoomNotFoundException) exception = "room not found";
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to delete room : " + exception + "</red>"
                        ));
                        return null;
                    });
                    deletePromise.thenAccept((success) -> {
                        if (deletePromise.isCompletedExceptionally()) return;
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Room deleted</green>"
                        ));
                    });
                });
    }

    @EngineeringMethod
    public static CommandAPICommand reloadPlugin() {
        return new CommandAPICommand("reloadPlugin")
                .withArguments(new IntegerArgument("roomId"))
                .withPermission(CommandPermission.OP)
                .executes((sender, args) -> {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Reloading plugin...</green>"
                    ));

                    Room room = Room.getRoomById((int) args.get("roomId"));
                    if (room == null) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Room not found</red>"
                        ));
                        return;
                    }

                    room.reloadPlugin();

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Plugin reloaded</green>"
                    ));
                });
    }

    @EngineeringMethod
    public static CommandAPICommand deleteAll() {
        return new CommandAPICommand("deleteAll")
                .withPermission(CommandPermission.OP)
                .withArguments(new IntegerArgument("roomIdStart"))
                .withArguments(new IntegerArgument("roomIdEnd"))
                .executes((sender, args) -> {
                    int roomIdStart = (int) (args.get("roomIdStart"));
                    int roomIdEnd = (int) (args.get("roomIdEnd"));

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Removing rooms...</green>"
                    ));


                    CompletableFuture<Void> deletePromise = RoomsManager.batchDeleteRooms(roomIdStart, roomIdEnd);
                    deletePromise.thenAccept((success) -> {
                        if (deletePromise.isCompletedExceptionally()) return;
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Rooms removed</green>"
                        ));
                    });
                });
    }


}
