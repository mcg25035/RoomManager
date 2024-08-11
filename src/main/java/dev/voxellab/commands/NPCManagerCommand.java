package dev.voxellab.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.voxellab.roommanager.config.MapConfig;
import dev.voxellab.roommanager.config.NPCConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.io.IOException;

public class NPCManagerCommand {
    public static CommandAPICommand command() {
        return new CommandAPICommand("npc_manager")
                .withSubcommand(create());
    }

    private static CommandAPICommand create() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("map_id"))
                .withArguments(new StringArgument("conversation_id"))
                .withArguments(new StringArgument("skin_name"))
                .withArguments(new GreedyStringArgument("npc_name"))
                .executes((sender, args) -> {
                    String mapId = (String) (args.get("map_id"));
                    String conversationId = (String) (args.get("conversation_id"));
                    String npcName = (String) (args.get("npc_name"));
                    String skinName = (String) (args.get("skin_name"));
                    Player player = (Player) sender;
                    try {
                        NPCConfig.create(mapId, ((Player) sender).getLocation(), conversationId, npcName, skinName);
                    } catch (IOException e) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(
                                "<red>Failed to create npc</red>"
                        ));
                        e.printStackTrace();
                    }
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<green>NPC created</green>"
                    ));
                });
    }


}
