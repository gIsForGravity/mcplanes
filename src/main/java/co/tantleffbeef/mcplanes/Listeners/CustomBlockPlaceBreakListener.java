package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.BlockBreakProgress;
import co.tantleffbeef.mcplanes.KeyManager;
import co.tantleffbeef.mcplanes.PluginKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages placing and breaking custom blocks
 */
public class CustomBlockPlaceBreakListener implements Listener {
    private final KeyManager<PluginKey> keyManager;
    private final Map<UUID, BlockBreakProgress> playerBlockProgress;

    public CustomBlockPlaceBreakListener(KeyManager<PluginKey> keyManager) {
        this.keyManager = keyManager;
        this.playerBlockProgress = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            tryBreakBlock(event);
        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            tryPlaceBlock(event);
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
