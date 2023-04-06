package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.*;
import co.tantleffbeef.mcplanes.Custom.item.PlaceableItem;
import co.tantleffbeef.mcplanes.struct.CustomItemNbt;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    /*@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            tryBreakBlock(event);
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            tryPlaceBlock(event);
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Bukkit.broadcastMessage("block place event");

        if (event.isCancelled())
            return;

        // Basically, we need to get the item that the player is holding
        // and find out if it's a custom block
        // if it is then we will place it
        final var item = event.getItemInHand();
        final var meta = item.getItemMeta();

        // check if item actually has meta
        if (meta == null)
            return;

        // now check if its a custom item
        final var data = meta.getPersistentDataContainer();
        if (CustomItemNbt.hasCustomItemNbt(data, keyManager))
            return;

        // grab the custom item data
        final var itemNbt = CustomItemNbt.fromPersistentDataContainer(data, keyManager);

        // figure out if its placeable
        if (!itemNbt.placeable())
            return;

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
