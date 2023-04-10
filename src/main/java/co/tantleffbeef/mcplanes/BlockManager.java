package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Custom.block.CustomBlockType;
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

public class BlockManager {
    private final KeyManager<CustomItemNbtKey> keyManager;
    private final Server server;
    private final ResourceManager resourceManager;
    private final Map<Location, UUID> displayEntities;
    private final Map<NamespacedKey, CustomBlockType> blockKeys;

    public BlockManager(KeyManager<CustomItemNbtKey> keyManager, Server server, ResourceManager resourceManager) {
        this.keyManager = keyManager;
        this.server = server;
        this.resourceManager = resourceManager;
        this.displayEntities = new HashMap<>();
        this.blockKeys = new HashMap<>();
    }

    public void registerBlock(@NotNull CustomBlockType block) {
        blockKeys.put(block.id(), block);
    }

    /* *
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
     * @param block the custom block to be placed
     * @param location where the block will be placed
     */
    public void placeBlock(@NotNull CustomBlockType block, @NotNull Location location) {
        final var mcBlock = location.getBlock();
        mcBlock.setType(block.blockMaterial());

        createDisplay(block, location);
        saveBlockToChunk(block, location);
    }

    private void saveBlockToChunk(@NotNull CustomBlockType block, @NotNull Location location) {
        final var blockNbt = new CustomBlockNbt(block.id());
        block.addAdditionalNbtBlockData(blockNbt);

        saveBlockDataToChunk(blockNbt, location);
    }

    /**
     * Saves a custom block to the data container of the chunk
     * The nbt should look something like this
     * <pre>
     *     {
     *         blocks:{
     *             location:[
     *                 {location:[0, 0, 0]},
     *                 {location:[5, 2, 3]}
     *             ],
     *             blocks:[
     *                 {custom_block_data:{
     *                     id:"mcplanes:aircrafter"
     *                 }},
     *                 {custom_block_data:{
     *                     id:"mcplanes:aircrafter"
     *                 }}
     *             ]
     *         }
     *     }
     * </pre>
     * @param blockData the data of the custom block to be placed here
     * @param location the location of the block
     */
    private void saveBlockDataToChunk(@NotNull CustomBlockNbt blockData, @NotNull Location location) {
        final var chunk = location.getChunk();
        final var chunkNbt = chunk.getPersistentDataContainer();

        // Get the blocks tag in chunk if it exists, otherwise create one
        final PersistentDataContainer chunkBlocksNbt;
        final boolean dataIsNew;
        if (chunkNbt.has(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER)) {
            chunkBlocksNbt = chunkNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER);
            dataIsNew = false;
        }
        else {
            chunkBlocksNbt = chunkNbt.getAdapterContext().newPersistentDataContainer();
            dataIsNew = true;
        }

        // there should be a blocks tag now
        assert chunkBlocksNbt != null;

        // If the data is new get the array that already exists, otherwise make a new one (although we will expand it
        //  so this is a little dumb but whatever)
        PersistentDataContainer[] chunkBlockLocationsArray;
        if (dataIsNew)
            chunkBlockLocationsArray = new PersistentDataContainer[0];
        else
            chunkBlockLocationsArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY);

        // There should be a locations tag now
        assert chunkBlockLocationsArray != null;

        // do the same thing as above but for the blocks array
        PersistentDataContainer[] chunkBlocksArray;
        if (dataIsNew)
            chunkBlocksArray = new PersistentDataContainer[0];
        else
            chunkBlocksArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY);

        // There should be a blocks array tag now
        assert chunkBlocksArray != null;

        assert chunkBlocksArray.length == chunkBlockLocationsArray.length;

        int locationIndex = -1;
        for (int i = 0; i < chunkBlockLocationsArray.length; i++) {
            final var locationContainer = chunkBlockLocationsArray[i];
            final var nbtLocation = locationContainer.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY);
            assert nbtLocation != null;
            assert nbtLocation.length == 3;

            final var bukkitLocation = new Location(chunk.getWorld(), nbtLocation[0], nbtLocation[1], nbtLocation[2]);
            if (bukkitLocation.equals(location)) {
                locationIndex = i;
            }
        }

        // If we couldn't find the block already in there then make a new spot for it
        if (locationIndex == -1) {
            chunkBlocksArray = Arrays.copyOf(chunkBlocksArray, chunkBlocksArray.length + 1);
            chunkBlockLocationsArray = Arrays.copyOf(chunkBlockLocationsArray, chunkBlockLocationsArray.length + 1);
            locationIndex = chunkBlockLocationsArray.length - 1;
        }

        // Set the location inside the location array
        var thisLocationNbtContainer = chunkBlockLocationsArray[locationIndex];
        final int[] thisLocationArray;
        if (thisLocationNbtContainer == null) {
            thisLocationNbtContainer = chunkBlocksNbt.getAdapterContext().newPersistentDataContainer();
            thisLocationArray = new int[3];
        } else
            thisLocationArray = thisLocationNbtContainer.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY);

        assert thisLocationArray != null;
        assert thisLocationArray.length == 3;

        thisLocationArray[0] = location.getBlockX();
        thisLocationArray[1] = location.getBlockY();
        thisLocationArray[2] = location.getBlockZ();

        // Set the data at the new location
        var thisBlockNbtContainer = chunkBlocksArray[locationIndex];
        if (thisBlockNbtContainer == null)
            thisBlockNbtContainer = chunkBlocksNbt.getAdapterContext().newPersistentDataContainer();

        blockData.saveToPersistentDataContainer(thisBlockNbtContainer, keyManager);

        // Save everything back
        // Save this location's array to this location tag
        thisLocationNbtContainer.set(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY, thisLocationArray);

        // Save location tag and block tag to location and block arrays
        chunkBlockLocationsArray[locationIndex] = thisLocationNbtContainer;
        chunkBlocksArray[locationIndex] = thisBlockNbtContainer;

        // Save arrays back to blocks tag
        chunkBlocksNbt.set(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY, chunkBlockLocationsArray);
        chunkBlocksNbt.set(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY, chunkBlocksArray);

        // Save blocks tag back to chunk
        chunkNbt.set(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER, chunkBlocksNbt);
    }

    private void deleteBlockDataFromChunk(Location blockLocation) {
        assert isCustomBlock(blockLocation);

        final var chunk = blockLocation.getChunk();
        final var chunkNbt = chunk.getPersistentDataContainer();

        final var chunkBlocksNbt = chunkNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER);
        assert chunkBlocksNbt != null;

        final var chunkBlockLocationsArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlockLocationsArray != null;

        final var chunkBlocksArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlocksArray != null;

        assert chunkBlockLocationsArray.length == chunkBlocksArray.length;

        int locationIndex = -1;
        for (int i = 0; i < chunkBlockLocationsArray.length; i++) {
            final var locationContainer = chunkBlockLocationsArray[i];

            final var nbtLocation = locationContainer.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY);
            assert nbtLocation != null;
            assert nbtLocation.length == 3;

            final var bukkitLocation = new Location(chunk.getWorld(), nbtLocation[0], nbtLocation[1], nbtLocation[2]);
            if (bukkitLocation.equals(blockLocation)) {
                locationIndex = i;
                break;
            }
        }

        assert locationIndex != -1;
        assert chunkBlockLocationsArray.length > 0;
        assert chunkBlocksArray.length > 0;

        final var newChunkBlockLocationsArray = new PersistentDataContainer[chunkBlockLocationsArray.length - 1];
        final var newChunkBlocksArray = new PersistentDataContainer[chunkBlocksArray.length - 1];

        System.arraycopy(chunkBlockLocationsArray, 0, newChunkBlockLocationsArray, 0, locationIndex);
        System.arraycopy(chunkBlockLocationsArray, locationIndex + 1, newChunkBlockLocationsArray, locationIndex, chunkBlockLocationsArray.length - locationIndex - 1);

        System.arraycopy(chunkBlocksArray, 0, newChunkBlocksArray, 0, locationIndex);
        System.arraycopy(chunkBlocksArray, locationIndex + 1, newChunkBlocksArray, locationIndex, chunkBlocksArray.length - locationIndex - 1);

        // Save arrays back to blocks tag
        chunkBlocksNbt.set(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY, newChunkBlockLocationsArray);
        chunkBlocksNbt.set(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY, newChunkBlocksArray);

        // Save blocks tag back to chunk
        chunkNbt.set(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER, chunkBlocksNbt);
    }

    public boolean isCustomBlock(@NotNull Location location) {
        final var chunk = location.getChunk();
        final var chunkNbt = chunk.getPersistentDataContainer();
        if (!chunkNbt.has(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER))
            return false;

        final var chunkBlocksNbt = chunkNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER);
        assert chunkBlocksNbt != null;

        final var chunkBlockLocationsArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlockLocationsArray != null;

        final var chunkBlocksArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlocksArray != null;

        assert chunkBlockLocationsArray.length == chunkBlocksArray.length;

        for (int i = 0; i < chunkBlockLocationsArray.length; i++) {
            final var locationContainer = chunkBlockLocationsArray[i];

            final var nbtLocation = locationContainer.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY);
            assert nbtLocation != null;
            assert nbtLocation.length == 3;

            final var bukkitLocation = new Location(chunk.getWorld(), nbtLocation[0], nbtLocation[1], nbtLocation[2]);
            if (bukkitLocation.equals(location)) {
                return CustomBlockNbt.hasCustomBlockNbt(chunkBlocksArray[i], keyManager);
            }
        }

        return false;
    }

    /**
     * Tries to get the custom block at Location, might throw an exception if there isn't one there, so check first
     * @param location where to get the custom block at
     * @return a custom block which is the type found at that location
     * @see BlockManager#isCustomBlock(Location)
     */
    private @NotNull CustomBlockType getCustomBlockAtLocation(@NotNull Location location) {
        assert isCustomBlock(location);

        final var chunk = location.getChunk();
        final var chunkNbt = chunk.getPersistentDataContainer();
        assert chunkNbt.has(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER);

        final var chunkBlocksNbt = chunkNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER);
        assert chunkBlocksNbt != null;

        final var chunkBlockLocationsArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlockLocationsArray != null;

        final var chunkBlocksArray = chunkBlocksNbt.get(keyManager.keyFor(CustomItemNbtKey.BLOCKS), PersistentDataType.TAG_CONTAINER_ARRAY);
        assert chunkBlocksArray != null;

        assert chunkBlockLocationsArray.length == chunkBlocksArray.length;

        for (int i = 0; i < chunkBlockLocationsArray.length; i++) {
            final var locationContainer = chunkBlockLocationsArray[i];
            final var nbtLocation = locationContainer.get(keyManager.keyFor(CustomItemNbtKey.LOCATION), PersistentDataType.INTEGER_ARRAY);
            assert nbtLocation != null;
            assert nbtLocation.length == 3;

            final var bukkitLocation = new Location(chunk.getWorld(), nbtLocation[0], nbtLocation[1], nbtLocation[2]);
            if (bukkitLocation.equals(location)) {
                final var blockNbt = CustomBlockNbt.fromPersistentDataContainer(chunkBlocksArray[i], keyManager);
                return blockKeys.get(blockNbt.id);
            }
        }

        throw new AssertionError("not a custom block");
    }

    private void createDisplay(@NotNull CustomBlockType block, @NotNull Location location) {
        final var world = location.getWorld();
        assert world != null;

        final var displayLocation = location.getBlock().getLocation().add(new Vector(0.5, 0.5, 0.5));
        final var display = (ItemDisplay) world.spawnEntity(displayLocation, EntityType.ITEM_DISPLAY);

        final var displayItem = block.displayItem();
        final var displayItemStack = resourceManager.getCustomItemStack(displayItem.id());

        display.setItemStack(displayItemStack);
        display.setItemDisplayTransform(block.displayTransform());

        displayEntities.put(location.getBlock().getLocation(), display.getUniqueId());
    }

    /**
     * Replaces a custom block with air
     * @param blockLocation the block to replace with air
     */
    public void deleteCustomBlock(Location blockLocation) {
        deleteBlockDataFromChunk(blockLocation);
        blockLocation.getBlock().setType(Material.AIR);

        final var entityId = displayEntities.remove(blockLocation);
        final var entity = Bukkit.getEntity(entityId);
        if (entity == null) {
            return;
        }

        entity.remove();
    }

    public void loadChunk(Chunk chunk) {

    }

    public void unloadChunk(Chunk chunk) {

    }
}
