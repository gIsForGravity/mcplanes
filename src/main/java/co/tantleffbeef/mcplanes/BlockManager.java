package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Custom.item.PlaceableItem;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockManager {
    private final KeyManager<CustomItemNbtKey> keyManager;
    private final ResourceManager resourceManager;
    private final Map<Location, UUID> displayEntities;

    public BlockManager(KeyManager<CustomItemNbtKey> keyManager, ResourceManager resourceManager) {
        this.keyManager = keyManager;
        this.resourceManager = resourceManager;
        this.displayEntities = new HashMap<>();
    }

    /**
     * Breaks custom block at blocklocation. Do not use this for a non-custom block
     * @param blockLocation
     * @param drop whether to drop an item
     */
    /*public void breakBlock(Location blockLocation, boolean drop) {
        final var block = blockLocation.getBlock();
        final var blockData = block.getBlockData();

        assert blockData.getMaterial() != Material.AIR && blockData.getMaterial() != Material.CAVE_AIR &&
                blockData.getMaterial() != Material.VOID_AIR;

        final var chunkStorage = blockLocation.getChunk().getPersistentDataContainer();

        assert chunkStorage.has(keyManager.keyFor(PluginKey.CUSTOM_BLOCK_DATA), PersistentDataType.TAG_CONTAINER);

        final var customBlockData = chunkStorage.get(
                keyManager.keyFor(PluginKey.CUSTOM_BLOCK_DATA),
                        PersistentDataType.TAG_CONTAINER);

        assert customBlockData != null;

        customBlockData
    }*/

    /**
     * Places a custom block at blockLocation
     * @param item the item that will become the block
     * @param location where the block will be placed
     */
    public void placeBlock(@NotNull PlaceableItem item, @NotNull Location location) {
        Bukkit.broadcastMessage("placing block");

        final var block = location.getBlock();
        final var world = location.getWorld();
        assert world != null;

        block.setType(item.baseMaterial());
        final var display = (ItemDisplay) world.spawnEntity(block.getLocation(), EntityType.ITEM_DISPLAY);

        final var displayItem = resourceManager.getCustomItemStack(item.id());

        display.setItemStack(displayItem);
        display.setItemDisplayTransform(item.displayType());

        displayEntities.put(block.getLocation(), display.getUniqueId());
    }

    /**
     * Sets the damage for the block at blockLocation
     * @param blockLocation the block in question
     * @param progress a scale from 0.0 --> 1.0 with 1.0 meaning completely broken
     */
    public void damageBlock(Location blockLocation, float progress) {

    }

    public void loadChunk(Chunk chunk) {

    }

    public void unloadChunk(Chunk chunk) {

    }
}
