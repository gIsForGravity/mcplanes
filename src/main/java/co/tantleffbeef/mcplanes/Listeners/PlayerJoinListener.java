package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.ResourceManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinListener extends AbstractListener {
    private final String resourcePackUrl;
    private final ResourceManager resourceManager;

    public PlayerJoinListener(Plugin plugin, String resourcePackUrl, ResourceManager resourceManager) {
        super(plugin);

        this.resourcePackUrl = resourcePackUrl;
        this.resourceManager = resourceManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // TODO: add a feature to set an optional prompt message for the resource pack

        Objects.requireNonNull(event.getPlayer().getPlayer())
                .setResourcePack(
                        resourcePackUrl,
                        resourceManager.getResourcePackHash(),
                        true);
    }
}
