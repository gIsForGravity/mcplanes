package co.tantleffbeef.mcplanes.Vehicles;

import co.tantleffbeef.mcplanes.JVehicle;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class AbstractVehicle implements JVehicle {
    protected Vector velocity = new Vector();

    @Override
    public void tick(Entity vehicle) {
        updatePosition(vehicle);
    }

    protected void updatePosition(Entity vehicle) {
        var loc = vehicle.getLocation();
        vehicle.teleport(loc.add(velocity));
    }
}
