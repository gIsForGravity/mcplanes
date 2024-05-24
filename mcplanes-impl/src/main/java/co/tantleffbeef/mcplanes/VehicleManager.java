package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.pojo.Input;
import co.tantleffbeef.mcplanes.vehicles.PhysicVehicle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VehicleManager implements Runnable {
    public static final float FIXED_DELTA_TIME = 1.0f / 20.0f;

    private final List<PhysicVehicle> vehicles = new ArrayList<>();
    private final Map<UUID, Input> inputs = new HashMap<>();

    public void registerVehicle(@NotNull PhysicVehicle vehicle) {
        assert !vehicles.contains(vehicle);
        vehicles.add(vehicle);
    }

    public void unregisterVehicle(@NotNull PhysicVehicle vehicle) {
        assert vehicles.contains(vehicle);
        vehicles.remove(vehicle);

        // Grab the driver and dismount them
        vehicle.setRider(null);
    }

    @Override
    public void run() {
        // Loop through all vehicles in the list
        // and run their tick function
        for (final PhysicVehicle vehicle : vehicles) {
            final var driver = vehicle.getRider();

            final Input input;
            final boolean result;
            // If the vehicle has a driver then pass it in
            if (driver != null && (input = inputs.get(driver.getUniqueId())) != null) {
                result = vehicle.tickVehicle(FIXED_DELTA_TIME, input);
            } else {
                // Otherwise just pass in null
                result = vehicle.tickVehicle(FIXED_DELTA_TIME, Input.empty);
            }

            // If the entity is dead then unregister the vehicle
            if (!result)
                unregisterVehicle(vehicle);
        }
    }

    public void start(@NotNull Plugin plugin, @NotNull BukkitScheduler scheduler) {
        scheduler.runTaskTimer(plugin, this, 1, 1);
    }
}
