package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {
    protected final Plugin plugin;

    public AbstractListener(Plugin plugin) {
        this.plugin = plugin;
    }
}
