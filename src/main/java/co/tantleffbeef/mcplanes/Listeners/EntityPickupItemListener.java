package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class EntityPickupItemListener extends AbstractListener {
    private final RecipeManager manager;

    public EntityPickupItemListener(Plugin plugin, RecipeManager manager) {
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
}
