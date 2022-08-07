package co.tantleffbeef.mcplanes.Vehicles;

import co.tantleffbeef.mcplanes.Plugin;

import java.util.UUID;

public abstract class AbstractPlane extends AbstractVehicle {
    public AbstractPlane(UUID uuid, Plugin plugin) {
        super(uuid, plugin);
    }

    public abstract void pushRoll(float amount);
    public abstract void pushPitch(float amount);
    public abstract void pushYaw(float amount);
}
