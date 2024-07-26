package dev.voxellab.roommanager;

import com.onarandombox.MultiverseCore.MultiverseCore;
import dev.voxellab.commands.MapManagerCommand;
import dev.voxellab.commands.RoomManagerCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Main extends JavaPlugin {
    static Main instance;

    MultiverseCore mvCore = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");

    List<RoomsManager> roomsContainerList = new ArrayList<RoomsManager>();
    int roomsContainerIdCounter = 0;

    public static Main getInstance() {
        return instance;

    }


    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new Events(), this);

        MapManagerCommand.command().register();
        RoomManagerCommand.command().register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
