package co.tantleffbeef.mcplanes;

import org.bukkit.entity.Player;

public interface JVehicleRider {
    void update(Input input);
    void kick();
    void setIsDriver(boolean isDriver);
}
