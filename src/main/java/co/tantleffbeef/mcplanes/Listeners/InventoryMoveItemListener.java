package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMoveItemListener implements Listener {
    private final RecipeManager manager;

    public InventoryMoveItemListener(RecipeManager manager) {
        this.manager = manager;
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
