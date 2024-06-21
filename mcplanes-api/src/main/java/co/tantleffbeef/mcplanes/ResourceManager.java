package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.custom.item.CustomItemType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.jar.JarFile;

public interface ResourceManager {
    /**
     * Loads assets from the resources of the jar to be added to the server pack
     * @param jar the jarfile of a plugin whose resources are to be added
     */
    void addAssetsFolder(@NotNull JarFile jar);

    /**
     * Registers an item, will create a custom model data variant for it which can be
     *  accessed with {@link ResourceManager#getCustomItemStack(NamespacedKey)}
     * @param itemType the item type to register
     */
    void registerItem(@NotNull CustomItemType itemType);

    // TODO: add javadoc
    void registerItemTextureAtlasDirectory(String dirName);

    /**
     * Returns the filename of the resource pack within the www folder
     * @return the built resource pack's filename
     */
    String getResourcePackFilename();

    /**
     * Returns all custom item ids, so they can be queried and indexed through
     * @return a set of all custom item ids
     */
    @NotNull Set<NamespacedKey> getItemIdList();

    /**
     * Returns the item stack for the custom item
     * @param key a namespacedkey representing the custom item
     * @return a clone of the item stack
     */
    @NotNull ItemStack getCustomItemStack(@NotNull NamespacedKey key);

    /**
     * Returns the custom item type for the custom item
     * @param key a namespaced key representing the custom item
     * @return the item type
     */
    @NotNull CustomItemType getCustomItemType(@NotNull NamespacedKey key);

    /**
     * Compiles all plugin resources. Call this function synchronously, and it will create an async task to run later
     */
    void compileResourcesAsync(@NotNull BukkitScheduler scheduler);

    /**
     * sends the compiled resource pack to said player. check if resources are compiling first please
     * @param player the player to send it to
     */
    void sendResourcesToPlayer(@NotNull Player player);

    boolean currentlyCompilingResources();

    /**
     * Returns a hash of the resource pack. Can be sent to the client to verify an updated version has been downloaded
     * @return a byte array representing the resource pack hash
     */
    byte[] getResourcePackHash();

    void addSpecificFileAtSpecificPlace(File input, String directoryInPackOrWhatever);
}
