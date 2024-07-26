package dev.voxellab.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.voxellab.roommanager.MapConfig;
import dev.voxellab.roommanager.Room;
import dev.voxellab.roommanager.RoomsManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class RoomManagerCommand {

    public static CommandAPICommand command() {
        return new CommandAPICommand("room_manager")
                .withSubcommand(create())
                .withSubcommand(delete())
                .withSubcommand(join())
                .withSubcommand(quit())
                .withSubcommand(reloadPlugin())
                .withSubcommand(removeAll());

    }

    public static CommandAPICommand join() {
        return new CommandAPICommand("join")
                .withArguments(new IntegerArgument("roomId"))
                .executes((sender, args) -> {
                    int roomId = (int) (args.get("roomId"));

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Joining room...</green>"
                    ));

                    if (Room.getRoomById(roomId).join((Player) sender)) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Joined room</green>"
                        ));
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to join room</red>"
                        ));
                    }
                });
    }

    public static CommandAPICommand quit() {
        return new CommandAPICommand("quit")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    Room room = Room.getRoomByPlayer(player);
                    if (room == null) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Not in a room</red>"
                        ));
                        return;
                    }
                    room.leave(player);
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Room left.</green>"
                    ));
                });
    }

    public static CommandAPICommand reloadPlugin() {
        return new CommandAPICommand("reloadPlugin")
                .withArguments(new IntegerArgument("roomId"))
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

    public static CommandAPICommand removeAll() {
        return new CommandAPICommand("removeAll")
                .withArguments(new IntegerArgument("roomIdStart"))
                .withArguments(new IntegerArgument("roomIdEnd"))
                .executes((sender, args) -> {
                    int roomIdStart = (int) (args.get("roomIdStart"));
                    int roomIdEnd = (int) (args.get("roomIdEnd"));

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Removing rooms...</green>"
                    ));

                    for (int i = roomIdStart; i <= roomIdEnd; i++) {
                        RoomsManager.deleteRoom(i);
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Room "+ i +" removed</green>"
                        ));
                    }
                });
    }

    private static CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("map"))
                .executes((sender, args) -> {
                    String map = (String) (args.get("map"));
                    MapConfig config = MapConfig.load(map + ".yml");

                    sender.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Room Creating...</green>"
                    ));

                    RoomsManager.createRoom(config).thenAccept((roomId) -> {
                        if (roomId == -1) {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<red>Failed to create room</red>"
                            ));
                            return;
                        }

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

                    RoomsManager.deleteRoom(roomId).thenAccept((success) -> {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<green>Room deleted</green>"
                        ));
                    });

                });

    }
}
