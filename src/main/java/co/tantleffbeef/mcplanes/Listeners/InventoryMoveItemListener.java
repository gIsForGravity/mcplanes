package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class InventoryMoveItemListener extends AbstractListener {
    private final RecipeManager manager;

    public InventoryMoveItemListener(Plugin plugin, RecipeManager manager) {
        super(plugin);

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
