package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.ResourceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class PlayerResourceListener implements Listener {
    private final Plugin plugin;
    private final String webserverUrl;
    private final ResourceManager resourceManager;

    public PlayerResourceListener(Plugin plugin, String webserverUrl, ResourceManager resourceManager) {
        this.plugin = plugin;
        this.webserverUrl = webserverUrl;
        this.resourceManager = resourceManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // this is actually the tick before the player joins
        // the server so we gotta wait one
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // TODO: add a feature to set an optional prompt message for the resource pack

            Objects.requireNonNull(event.getPlayer().getPlayer())
                    .setResourcePack(
                            webserverUrl + '/' + resourceManager.getResourcePackFilename(),
                            resourceManager.getResourcePackHash(),
                            true);
        }, 1);
    }
}
