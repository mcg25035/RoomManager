package dev.voxellab.roommanager.config;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class NPCConfig {
    public static Path npcConfigPath = Path.of("npcs/");

    public double npcX;
    public double npcY;
    public double npcZ;
    public double npcYaw;
    public double npcPitch;
    public String npcName;
    public String conversationId;

    private NPCConfig() {
    }

    public static void create(String mapId, Location location, String conversationId, String npcName) throws IOException {
        NPCConfig npcConfig = new NPCConfig();
        npcConfig.npcName = npcName;
        npcConfig.npcX = location.getX();
        npcConfig.npcY = location.getY();
        npcConfig.npcZ = location.getZ();
        npcConfig.npcYaw = location.getYaw();
        npcConfig.npcPitch = location.getPitch();
        npcConfig.conversationId = conversationId;
        npcConfig.save(mapId, npcName);
    }

    public static NPCConfig load(String mapId, String npcName) {
        Path mapNpcConfigPath = npcConfigPath.resolve(mapId);
        if (!mapNpcConfigPath.toFile().exists()) return null;

        String filename = npcName + ".yml";
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(mapNpcConfigPath.resolve(filename).toFile());
        NPCConfig npcConfig = new NPCConfig();
        npcConfig.npcName = yaml.getString("npcName");
        npcConfig.npcX = yaml.getDouble("npcX");
        npcConfig.npcY = yaml.getDouble("npcY");
        npcConfig.npcZ = yaml.getDouble("npcZ");
        npcConfig.npcYaw = yaml.getDouble("npcYaw");
        npcConfig.npcPitch = yaml.getDouble("npcPitch");
        npcConfig.conversationId = yaml.getString("conversationId");
        return npcConfig;
    }

    public static List<NPCConfig> loadAll(String mapId) {
        Path mapNpcConfigPath = npcConfigPath.resolve(mapId);
        if (!mapNpcConfigPath.toFile().exists()) return null;

        List<NPCConfig> npcConfigs = new ArrayList<>();
        for (File file : mapNpcConfigPath.toFile().listFiles()) {
            String filename = file.getName();
            if (!filename.endsWith(".yml")) continue;
            String npcName = filename.replaceAll(".yml", "");
            npcConfigs.add(load(mapId, npcName));
        }

        return npcConfigs;
    }

    public static List<String> listAll(String mapId) {
        Path mapNpcConfigPath = npcConfigPath.resolve(mapId);
        if (!mapNpcConfigPath.toFile().exists()) return null;

        List<String> npcNames = new ArrayList<>();
        for (File file : mapNpcConfigPath.toFile().listFiles()) {
            String filename = file.getName();
            if (!filename.endsWith(".yml")) continue;
            String npcName = filename.replaceAll(".yml", "");
            npcNames.add(npcName);
        }

        return npcNames;
    }

    public void save(String mapId, String npcName) throws IOException {
        Path mapNpcConfigPath = npcConfigPath.resolve(mapId);
        if (!mapNpcConfigPath.toFile().exists()) mapNpcConfigPath.toFile().mkdirs();

        String filename = npcName + ".yml";
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(mapNpcConfigPath.resolve(filename).toFile());
        yaml.set("npcName", npcName);
        yaml.set("npcX", npcX);
        yaml.set("npcY", npcY);
        yaml.set("npcZ", npcZ);
        yaml.set("npcYaw", npcYaw);
        yaml.set("npcPitch", npcPitch);
        yaml.set("conversationId", conversationId);
        yaml.save(mapNpcConfigPath.resolve(filename).toFile());
    }
}
