package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.CustomNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import co.tantleffbeef.mcplanes.ResourceManager;
import co.tantleffbeef.mcplanes.custom.item.InteractableItemType;
import co.tantleffbeef.mcplanes.custom.item.PlaceableItemType;
import co.tantleffbeef.mcplanes.pojo.serialize.CustomItemNbt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractableItemListener implements Listener {
    private final KeyManager<CustomNbtKey> keyManager;
    private final ResourceManager resourceManager;

    public InteractableItemListener(KeyManager<CustomNbtKey> keyManager, ResourceManager resourceManager) {
        this.keyManager = keyManager;
        this.resourceManager = resourceManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Exit if wrong type of action
        final var action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
            return;

        // Exit if no item involved
        if (!event.hasItem())
            return;
        assert event.getItem() != null;

        // Check if interactable item involved
        final var item = getItemForInteractEvent(event);
        if (item == null)
            return;

        // Pass event to item
        item.interact(event.getPlayer(), event.getItem());
    }

    private InteractableItemType getItemForInteractEvent(PlayerInteractEvent event) {
        // Basically, we need to get the item that the player is holding
        // and find out if it's a custom block
        // if it is then we will place it
        final var item = event.getItem();
        assert item != null;

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

        // Grab the custom item type
        final var itemType = resourceManager.getCustomItemType(itemNbt.id);

        // figure out if its interactable
        if (!(itemType instanceof InteractableItemType))
            return null;

        // finally return custom item type
        return (InteractableItemType) itemType;
    }
}
