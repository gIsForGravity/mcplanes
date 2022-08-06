package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.event.Listener;

/**
 * Extend this when creating a listener
 */
public abstract class AbstractListener implements Listener {
    protected final Plugin plugin;

    public AbstractListener(Plugin plugin) {
        this.plugin = plugin;
    }
}
