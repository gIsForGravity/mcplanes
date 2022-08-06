package co.tantleffbeef.mcplanes.Vehicles;

import co.tantleffbeef.mcplanes.JVehicle;
import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public abstract class AbstractVehicle implements JVehicle {
    protected Vector velocity = new Vector();
    protected final UUID uuid;
    protected final Plugin plugin;

    public AbstractVehicle(UUID uuid, Plugin plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void tick() {
        var vehicle = Bukkit.getEntity(uuid);
        assert vehicle != null;
        updatePosition(vehicle);
    }

    protected void updatePosition(Entity vehicle) {
        var loc = vehicle.getLocation();
        vehicle.teleport(loc.add(velocity));
    }
}
