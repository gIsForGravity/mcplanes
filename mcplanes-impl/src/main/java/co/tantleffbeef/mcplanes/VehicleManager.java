package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.pojo.Input;
import co.tantleffbeef.mcplanes.vehicles.PhysicsVehicle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VehicleManager implements Runnable {
    public static final float FIXED_DELTA_TIME = 1.0f / 20.0f;

    private final List<PhysicsVehicle> vehicles = new ArrayList<>();
    private final Map<UUID, Input> inputs = new HashMap<>();

    public void registerVehicle(@NotNull PhysicsVehicle vehicle) {
        assert !vehicles.contains(vehicle);
        vehicles.add(vehicle);
    }

    public void unregisterVehicle(@NotNull PhysicsVehicle vehicle) {
        assert vehicles.contains(vehicle);
        vehicles.remove(vehicle);
    }

    @Override
    public void run() {
        System.out.println("vehiclemanager.run");
        for (int i = 0; i < vehicles.size(); i++) {
            final var vehicle = vehicles.get(i);
            final var driver = vehicle.driver();

            final Input input;
            if (driver != null && (input = inputs.get(driver.getUniqueId())) != null) {
                vehicle.tick(input, FIXED_DELTA_TIME);
            } else {
                System.out.println("driver null");
                vehicle.tick(null, FIXED_DELTA_TIME);
            }
        }
    }

    public void start(@NotNull Plugin plugin, @NotNull BukkitScheduler scheduler) {
        scheduler.runTaskTimer(plugin, this, 1, 1);
    }
}
