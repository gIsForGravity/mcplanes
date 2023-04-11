package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.JVehicle;
import co.tantleffbeef.mcplanes.McPlanes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.UUID;

public abstract class AbstractVehicle implements JVehicle {
    protected Vector velocity = new Vector();
    protected final UUID uuid;
    protected final McPlanes mcPlanes;

    public AbstractVehicle(UUID uuid, McPlanes mcPlanes) {
        this.uuid = uuid;
        this.mcPlanes = mcPlanes;
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
