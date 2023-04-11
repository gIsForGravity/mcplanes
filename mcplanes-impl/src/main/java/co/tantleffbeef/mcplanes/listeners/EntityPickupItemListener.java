package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.RecipeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class EntityPickupItemListener implements Listener {
    private final RecipeManager manager;

    public EntityPickupItemListener(RecipeManager manager) {
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
