package co.tantleffbeef.mcplanes.listeners;

import co.tantleffbeef.mcplanes.physics.event.PhysicsObjectCollisionEvent;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class PhysicsObjectCollisionListener implements Listener {
    private final Server server;

    public PhysicsObjectCollisionListener(@NotNull Server server) {
        this.server = server;
    }

    @EventHandler
    public void onPhysicsObjectCollision(@NotNull PhysicsObjectCollisionEvent event) {
        server.broadcastMessage("physics object collision event:");
        server.broadcastMessage("\t" + event.getDirection());
    }
}
