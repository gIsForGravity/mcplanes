package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.VehicleManager;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhysicVehicle {
    /**
     * The controller of this physic vehicle. Controls its behavior
     */
    private final PhysicVehicleController controller;
    /**
     * The underlying entity used as the vehicle
     */
    private final Entity vehicle;
    /**
     * The current rider, or null for none
     */
    private Player rider;

    /**
     * Create a physic vehicle object
     * @param controller the controller of this vehicle. Controls its behavior
     * @param vehicle the underlying entity used as the vehicle, usually ridden by the player
     */
    public PhysicVehicle(@NotNull PhysicVehicleController controller, @NotNull Entity vehicle) {
        this.controller = controller;
        this.vehicle = vehicle;
        this.rider = null;
    }

    /**
     * Sets this vehicle's rider. Can be null for no rider
     * @param rider the new rider of the vehicle, or null for none
     */
    public void setRider(@Nullable Player rider, @NotNull VehicleManager manager) {
        if (rider == null) {
            if (this.rider != null) {
                manager.setRiderVehicle(this.rider, null);
            }
        } else {
            manager.setRiderVehicle(rider, this);
        }

        this.rider = rider;
        controller.setRider(vehicle, rider);
    }

    /**
     * Gives the vehicle's current rider, or null for none
     * @return the rider
     */
    public @Nullable Player getRider() {
        return rider;
    }

    public @NotNull PhysicVehicleController getController() {
        return controller;
    }

    /**
     * Applies physics to the vehicle over delta time
     * @param delta the amount of time
     * @param driverInput the input to use for controls
     * @return whether the vehicle is still alive
     */
    public boolean tickVehicle(float delta, Input driverInput) {
        return controller.tick(delta, driverInput, vehicle);
    }
}
