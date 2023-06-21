package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.BlockManager;
import co.tantleffbeef.mcplanes.custom.block.InteractableBlockType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class InteractableBlockListener implements Listener {
    private final BlockManager manager;

    public InteractableBlockListener(@NotNull BlockManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (!event.hasBlock())
            return;

        final var block = event.getClickedBlock();
        assert block != null;

        // grab the block's location
        final var location = block.getLocation();

        // check if its a custom bock
        if (!manager.isCustomBlock(location))
            return;

        // grab the type
        final var blockType = manager.getCustomBlockAtLocation(location);

        // check if its interactable
        if (!(blockType instanceof InteractableBlockType interactable))
            return;

        // run the block type's interact event and return whether to cancel it
        event.setCancelled(
                interactable.interactBlock(event.getPlayer(), location, block, event.getAction())
        );
    }
}
