package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.*;
import co.tantleffbeef.mcplanes.Custom.item.PlaceableItem;
import co.tantleffbeef.mcplanes.struct.CustomItemNbt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages placing and breaking custom blocks
 */
public class CustomBlockPlaceBreakListener implements Listener {
    private final BlockManager blockManager;
    private final ResourceManager resourceManager;
    private final KeyManager<CustomItemNbtKey> keyManager;
    private final Map<UUID, BlockBreakProgress> playerBlockProgress; // TODO: block breaking progress

    public CustomBlockPlaceBreakListener(BlockManager blockManager, ResourceManager resourceManager, KeyManager<CustomItemNbtKey> keyManager) {
        this.blockManager = blockManager;
        this.resourceManager = resourceManager;
        this.keyManager = keyManager;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Bukkit.broadcastMessage("block place event");

        if (event.isCancelled())
            return;

        Bukkit.broadcastMessage("event not cancelled");

        // Basically, we need to get the item that the player is holding
        // and find out if it's a custom block
        // if it is then we will place it
        final var item = event.getItemInHand();
        final var meta = item.getItemMeta();

        // check if item actually has meta
        if (meta == null)
            return;

        Bukkit.broadcastMessage("meta not null");

        // now check if its a custom item
        final var data = meta.getPersistentDataContainer();
        if (!CustomItemNbt.hasCustomItemNbt(data, keyManager))
            return;

        Bukkit.broadcastMessage("has custom item nbt");

        // grab the custom item data
        final var itemNbt = CustomItemNbt.fromPersistentDataContainer(data, keyManager);

        // figure out if its placeable
        if (!itemNbt.placeable())
            return;

        Bukkit.broadcastMessage("is placeable");

        // finally get the custom item and place it
        final var customItem = resourceManager.getCustomItem(itemNbt.id());
        blockManager.placeBlock((PlaceableItem) customItem, event.getBlockPlaced().getLocation());
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
