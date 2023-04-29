package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.ResourceManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerResourceListener implements Listener {
    private final Plugin plugin;
    private final ResourceManager resourceManager;
    private final Map<UUID, Long> timer = new HashMap<>();

    public PlayerResourceListener(Plugin plugin, ResourceManager resourceManager) {
        this.plugin = plugin;
        this.resourceManager = resourceManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // this is actually the tick before the player joins
        // the server so we gotta wait one
        sendPack(event.getPlayer());
    }

    @EventHandler
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        final var player = event.getPlayer();

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.ACCEPTED) {
            // Start timer when player accepts resource pack

            timer.remove(player.getUniqueId());
            timer.put(player.getUniqueId(), new Date().getTime());
        } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            if (timer.get(player.getUniqueId()) == null) {
                sendPack(player);
                return;
            }

            // Stop timer after resource pack is accepted
            final long time = new Date().getTime() - timer.get(player.getUniqueId());

            // if time between response was less than 3 seconds then resend
            if (time < 1250)
                sendPack(player);

            // Remove old value that isn't needed anymore
            timer.remove(player.getUniqueId());
        }
    }

    private void sendPack(Player player) {
        if (resourceManager.currentlyCompilingResources())
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!resourceManager.currentlyCompilingResources()) {
                        // Once resources no longer compiling, send the pack
                        sendPackNow(player);
                        // Stop checking, we all good now
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 0);
        else
            sendPackNow(player);
    }

    private void sendPackNow(Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // TODO: add a feature to set an optional prompt message for the resource pack

            resourceManager.sendResourcesToPlayer(Objects.requireNonNull(player));
        }, 1);
    }
}
