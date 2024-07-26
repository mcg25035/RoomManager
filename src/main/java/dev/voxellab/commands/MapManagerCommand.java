package dev.voxellab.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.voxellab.roommanager.MapConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.io.IOException;

public class MapManagerCommand {
    public static CommandAPICommand command() {
        return new CommandAPICommand("map_manager")
                .withSubcommand(create());
    }

    private static CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("mapName"))
                .executes((sender, args) -> {
                    String mapName = (String) (args.get("mapName"));
                    Player player = (Player) sender;
                    try {
                        MapConfig.create();
                    } catch (IOException e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to create map</red>"
                        ));
                        e.printStackTrace();
                    }
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>Map created</green>"
                    ));
                });
    }
}
