package co.tantleffbeef.mcplanes.Vehicles;

import co.tantleffbeef.mcplanes.McPlanes;

import java.util.UUID;

public abstract class AbstractPlane extends AbstractVehicle {
    public AbstractPlane(UUID uuid, McPlanes mcPlanes) {
        super(uuid, mcPlanes);
    }

    public abstract void pushRoll(float amount);
    public abstract void pushPitch(float amount);
    public abstract void pushYaw(float amount);
}
