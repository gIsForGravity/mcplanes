package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.custom.block.CustomBlockType;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

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
    void deleteCustomBlock(@NotNull Location blockLocation);
    @NotNull CustomBlockType getCustomBlockAtLocation(@NotNull Location location);

    void loadChunk(@NotNull Chunk chunk);

    void unloadChunk(@NotNull Chunk chunk);
}
