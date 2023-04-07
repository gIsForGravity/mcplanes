package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.*;
import co.tantleffbeef.mcplanes.Custom.item.PlaceableItem;
import co.tantleffbeef.mcplanes.event.CustomBlockPlaceEvent;
import co.tantleffbeef.mcplanes.struct.CustomItemNbt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages placing and breaking custom blocks
 */
public class CustomBlockPlaceBreakListener implements Listener {
    private final BlockManager blockManager;
    private final ResourceManager resourceManager;
    private final PluginManager pluginManager;
    private final KeyManager<CustomItemNbtKey> keyManager;
    private final Map<UUID, BlockBreakProgress> playerBlockProgress; // TODO: block breaking progress

    public CustomBlockPlaceBreakListener(BlockManager blockManager, ResourceManager resourceManager, PluginManager pluginManager, KeyManager<CustomItemNbtKey> keyManager) {
        this.blockManager = blockManager;
        this.resourceManager = resourceManager;
        this.keyManager = keyManager;
        this.pluginManager = pluginManager;
        this.playerBlockProgress = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Bukkit.broadcastMessage(event.getAction().toString());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Bukkit.broadcastMessage("block break event");
        Bukkit.broadcastMessage("cancelled? " + event.isCancelled());
        Bukkit.broadcastMessage("");
    }

    private PlaceableItem getItemForPlaceEvent(BlockPlaceEvent event) {
        // Basically, we need to get the item that the player is holding
        // and find out if it's a custom block
        // if it is then we will place it
        final var item = event.getItemInHand();
        final var meta = item.getItemMeta();

        // check if item actually has meta
        if (meta == null)
            return null;

        // now check if its a custom item
        final var data = meta.getPersistentDataContainer();
        if (!CustomItemNbt.hasCustomItemNbt(data, keyManager))
            return null;

        // grab the custom item data
        final var itemNbt = CustomItemNbt.fromPersistentDataContainer(data, keyManager);

        // figure out if its placeable
        if (!itemNbt.placeable())
            return null;

        // finally get the custom item and place it
        return (PlaceableItem) resourceManager.getCustomItem(itemNbt.id());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent originalEvent) {
        // Check if there's a custom item and grab it
        final var cItem = getItemForPlaceEvent(originalEvent);
        if (cItem == null)
            return;

        // Dispatch an event
        final var event = new CustomBlockPlaceEvent(originalEvent, cItem);
        pluginManager.callEvent(event);
        originalEvent.setCancelled(event.isCancelled());
        originalEvent.setBuild(event.canBuild());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void afterBlockPlace(BlockPlaceEvent event) {
        // Check if the event was cancelled
        if (event.isCancelled())
            return;

        // Check if there's a custom item and grab it
        final var cItem = getItemForPlaceEvent(event);
        if (cItem == null)
            return;

        blockManager.placeBlock(cItem, event.getBlockPlaced().getLocation());
    }

    private void tryBreakBlock(PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;


    }

    private void tryPlaceBlock(PlayerInteractEvent event) {
        if (!event.hasItem())
            return;


    }
}
