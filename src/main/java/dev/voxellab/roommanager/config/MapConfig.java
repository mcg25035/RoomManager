package dev.voxellab.roommanager.config;

import dev.voxellab.roommanager.exceptions.*;
import dev.voxellab.utils.PointTools;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;

public class MapConfig {
    static Path mapConfigPath = Path.of("maps/");

    public String mapName;
    public String mapId;
    public String scripts;
    public String names;
    public String loots;
    public String embeds;
    public int mapX1;
    public int mapZ1;
    public int mapX2;
    public int mapZ2;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public int playerMaxCount;
    public int playerMinCount;
    public int maxTime;

    private MapConfig() {
    }



    public static MapConfig load(String filename) throws MapConfingNotFoundException {
        if (!mapConfigPath.resolve(filename).toFile().exists()) throw new MapConfingNotFoundException("");

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(mapConfigPath.resolve(filename).toFile());
        MapConfig roomSampleConfig = new MapConfig();
        roomSampleConfig.mapName = yaml.getString("mapName");
        roomSampleConfig.mapId = yaml.getString("mapId");
        roomSampleConfig.mapX1 = yaml.getInt("mapX1");
        roomSampleConfig.mapZ1 = yaml.getInt("mapZ1");
        roomSampleConfig.mapX2 = yaml.getInt("mapX2");
        roomSampleConfig.mapZ2 = yaml.getInt("mapZ2");

        int[][] points = PointTools.normalizeCuboidPoints(roomSampleConfig.mapX1, roomSampleConfig.mapZ1, roomSampleConfig.mapX2, roomSampleConfig.mapZ2);
        roomSampleConfig.mapX1 = points[0][0];
        roomSampleConfig.mapZ1 = points[0][1];
        roomSampleConfig.mapX2 = points[1][0];
        roomSampleConfig.mapZ2 = points[1][1];

        roomSampleConfig.spawnX = yaml.getInt("spawnX");
        roomSampleConfig.spawnY = yaml.getInt("spawnY");
        roomSampleConfig.spawnZ = yaml.getInt("spawnZ");

        roomSampleConfig.playerMaxCount = yaml.getInt("playerMaxCount");
        roomSampleConfig.playerMinCount = yaml.getInt("playerMinCount");
        roomSampleConfig.maxTime = yaml.getInt("maxTime");
        return roomSampleConfig;
    }

    public static void create() throws IOException {
        MapConfig roomSampleConfig = new MapConfig();
        roomSampleConfig.mapName = "";
        roomSampleConfig.mapId = "empty";
        roomSampleConfig.mapX1 = 0;
        roomSampleConfig.mapZ1 = 0;
        roomSampleConfig.mapX2 = 0;
        roomSampleConfig.mapZ2 = 0;
        roomSampleConfig.spawnX = 0;
        roomSampleConfig.spawnY = 0;
        roomSampleConfig.spawnZ = 0;
        roomSampleConfig.playerMaxCount = 0;
        roomSampleConfig.playerMinCount = 0;
        roomSampleConfig.maxTime = -1;
        roomSampleConfig.scripts = "";
        roomSampleConfig.names = "";
        roomSampleConfig.loots = "";
        roomSampleConfig.embeds = "";

        roomSampleConfig.save();
    }

    public MapConfig save() throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("mapName", mapName);
        yaml.set("mapId", mapId);
        yaml.set("mapX1", mapX1);
        yaml.set("mapZ1", mapZ1);
        yaml.set("mapX2", mapX2);
        yaml.set("mapZ2", mapZ2);
        yaml.set("spawnX", spawnX);
        yaml.set("spawnY", spawnY);
        yaml.set("spawnZ", spawnZ);
        yaml.set("playerMaxCount", playerMaxCount);
        yaml.set("playerMinCount", playerMinCount);
        yaml.set("maxTime", maxTime);
        yaml.set("scripts", scripts);
        yaml.set("names", names);
        yaml.set("loots", loots);
        yaml.set("embeds", embeds);
        yaml.save(mapConfigPath.resolve(mapId+".yml").toFile());
        return this;
    }

}
