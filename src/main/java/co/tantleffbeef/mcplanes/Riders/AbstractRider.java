package co.tantleffbeef.mcplanes.Riders;

import co.tantleffbeef.mcplanes.JVehicleRider;

public abstract class AbstractRider implements JVehicleRider {
    protected boolean isDriver = false;

    @Override
    public void setIsDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }
}
