package dev.voxellab.worldedit;

import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.task.progress.Progress;
import dev.voxellab.roommanager.Main;
import org.bukkit.Location;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Material;

public class WeAPI {

    public static Clipboard copyClipboard(Location min, Location max, boolean shouldCopyEntities) {
        BlockVector3 bot = BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ());
        BlockVector3 top = BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ());
        CuboidRegion region = new CuboidRegion(bot, top);
        Clipboard clipboard = new BlockArrayClipboard(region);
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(min.getWorld().getName()))) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(shouldCopyEntities);
            Operations.complete(forwardExtentCopy);
        } catch (Exception e) {
            Main.getInstance().getLogger().info("Couldn't copy clipboard from: '" + min + "' to: '" + max + "'.");
            e.printStackTrace();
        }
        return clipboard;
    }

    public static void placeClipboard(Clipboard clipboard, Location placeLocation, boolean ignoreAir) {
        if(clipboard == null) return;
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(placeLocation.getWorld().getName()))) {
            @SuppressWarnings("resource")
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(placeLocation.getX(), placeLocation.getY(), placeLocation.getZ()))
                    .ignoreAirBlocks(ignoreAir)
                    .build();
            Operations.complete(operation);
        } catch (Exception e) {
            Main.getInstance().getLogger().info("Couldn't place clipboard at: '" + placeLocation + "'.");
            e.printStackTrace();
        }
    }

    public static void clearArea(Location min, Location max) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(min.getWorld().getName()));) {
            Region region = new CuboidRegion(BlockVector3.at(min.getX(), min.getY(), min.getZ()), BlockVector3.at(max.getX(), max.getY(), max.getZ()));
            Pattern pattern = BukkitAdapter.adapt(Material.AIR.createBlockData());
            RegionFunction set = new BlockReplace(editSession, pattern);
            RegionVisitor visitor = new RegionVisitor(region, set);
            Operations.completeBlindly(visitor);
        } catch (Exception e) {
            Main.getInstance().getLogger().info("Couldn't clear area from: '" + min + "' to: '" + max + "'.");
            e.printStackTrace();
        }
    }

}
 