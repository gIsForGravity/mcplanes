package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.custom.block.CustomBlockType;
import co.tantleffbeef.mcplanes.serialize.CustomBlockNbt;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public interface BlockManager {
    /**
     * Registers a custom block type
     * @param block the custom block type to register
     */
    void registerBlock(@NotNull CustomBlockType block);

    /**
     * Places a custom block at blockLocation
     * @param block the custom block to be placed
     * @param location where the block will be placed
     */
    void placeBlock(@NotNull CustomBlockType block, @NotNull Location location);

    boolean isCustomBlock(@NotNull Location location);

    /**
     * Replaces a custom block with air
     * @param blockLocation the block to replace with air
     */
    void deleteCustomBlock(Location blockLocation);

    void loadChunk(Chunk chunk);

    void unloadChunk(Chunk chunk);
}
