package co.tantleffbeef.mcplanes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Vehicle;

import java.util.*;

public class VehicleManager {
    private final Plugin plugin;
    private final NamespacedKey vehicleType;
    private final NamespacedKey vehicleModel;

    public VehicleManager(Plugin plugin) {
        this.plugin = plugin;
        vehicleType = new NamespacedKey(plugin, "vehicleType");
        vehicleModel = new NamespacedKey(plugin, "vehicleModel");
    }

    private Map<UUID, JVehicle> activeVehicles = new HashMap<>();

    public void activateVehicle(Vehicle entity, JVehicle JVehicle) {}

    public void activateVehicle(Vehicle entity) {}
}
