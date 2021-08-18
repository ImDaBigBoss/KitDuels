package com.github.imdabigboss.kitduels.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;

import org.bukkit.Location;
import org.bukkit.Material;

public class WorldEditUtils {
    public static boolean cloneRegion(Location pos1, Location pos2, Location toPos) {
        Location min;
        Location max;
        if (pos1.getBlockY() < pos2.getBlockY()) {
            min = pos1;
            max = pos2;
        } else {
            min = pos2;
            max = pos1;
        }

        World world = BukkitAdapter.adapt(pos1.getWorld());
        CuboidRegion region = new CuboidRegion(world, locationToBlockVector3(min), locationToBlockVector3(max));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(true);
            Operations.complete(forwardExtentCopy);
            clipboard.setOrigin(locationToBlockVector3(pos1));
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(locationToBlockVector3(toPos))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static BlockVector3 locationToBlockVector3(Location pos) {
        return BlockVector3.at(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    public static Location[] getCloneRegion(Location pos1, Location pos2) {
        int diff;
        if (pos1.getBlockX() > pos2.getBlockX()) {
            diff = (pos1.getBlockX() - pos2.getBlockX()) + 1000;
        } else {
            diff = (pos2.getBlockX() - pos1.getBlockX()) + 1000;
        }

        return new Location[] { new Location(pos1.getWorld(), pos1.getBlockX() + diff, pos1.getBlockY(), pos1.getBlockZ()), new Location(pos2.getWorld(), pos2.getBlockX() + diff, pos2.getBlockY(), pos2.getBlockZ()) };
    }

    public static boolean removeRegion(Location pos1, Location pos2) {
        CuboidRegion region = new CuboidRegion(locationToBlockVector3(pos1), locationToBlockVector3(pos2));
        World world = BukkitAdapter.adapt(pos1.getWorld());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            BlockState air = BukkitAdapter.adapt(Material.AIR.createBlockData());
            editSession.setBlocks(region, air);
        } catch (WorldEditException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean removeRegion(Location[] pos) {
        return removeRegion(pos[0], pos[1]);
    }

    public static boolean removeMapClone(Location pos1, Location pos2) {
        Location[] region = getCloneRegion(pos1, pos2);

        return removeRegion(region[0], region[1]);
    }
}
