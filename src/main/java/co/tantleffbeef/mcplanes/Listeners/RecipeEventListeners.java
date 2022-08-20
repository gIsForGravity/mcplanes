package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class RecipeEventListeners extends AbstractListener {
    private final RecipeManager manager;

    public RecipeEventListeners(Plugin plugin, RecipeManager manager) {
        super(plugin);

        this.manager = manager;
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        // return if this isn't a player picking up the item
        if (!(event.getEntity() instanceof final Player player))
            return;

        final var itemStack = event.getItem().getItemStack();
        // if the item unlocks any recipes than go do that
        manager.unlockRecipes(player, itemStack);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // return if this isn't entering a player's inventory
        if (!(event.getDestination().getHolder() instanceof final Player player))
            return;

        // if its moving from your inventory to your inventory then no need to check
        if (event.getDestination().equals(event.getSource()))
            return;

        final var itemStack = event.getItem();
        // if the item unlocks any recipes than go do that
        manager.unlockRecipes(player, itemStack);
    }
}
