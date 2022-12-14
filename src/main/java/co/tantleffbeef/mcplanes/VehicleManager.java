package co.tantleffbeef.mcplanes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class VehicleManager implements Runnable {
    private final Plugin plugin;
    private final NamespacedKey isVehicle;
    private final NamespacedKey vehicleType;
    private final NamespacedKey vehicleModel;

    public VehicleManager(Plugin plugin) {
        this.plugin = plugin;
        isVehicle = new NamespacedKey(plugin, "isVehicle");
        vehicleType = new NamespacedKey(plugin, "vehicleType");
        vehicleModel = new NamespacedKey(plugin, "vehicleModel");

        plugin.getServer().getScheduler().runTaskTimer(plugin, this, 1, 1);
    }

    private final Map<UUID, JVehicle> activeVehicles = new HashMap<>();
    private final Map<UUID, JVehicleRider> riders = new HashMap<>();

    public boolean checkIfVehicle(Entity vehicle) {
        var data = vehicle.getPersistentDataContainer();
        //noinspection ConstantConditions
        return data.has(isVehicle, PersistentDataType.BYTE) &&
                data.get(isVehicle, PersistentDataType.BYTE) == 1;
    }

    public boolean checkIfActive(Entity vehicle) {
        return activeVehicles.containsKey(vehicle.getUniqueId());
    }

    public void activateVehicle(Entity vehicle) {
        // TODO
    }

    public void activateVehicle() {
        // TODO
    }

    public boolean checkIfRider(UUID player) {
        return riders.containsKey(player);
    }

    public JVehicleRider getAsRider(UUID player) {
        return riders.get(player);
    }

    public void riderInput(Player player, Input input) {
        var rider = riders.get(player.getUniqueId());
        //rider.update(input, player); TODO
    }

    // runs every tick
    public void run() {
        if (activeVehicles.isEmpty())
            return;

        final var server = plugin.getServer();

        activeVehicles.forEach((uuid, vehicle) -> {
            final Entity entity = server.getEntity(uuid);

            if (entity == null) {
                activeVehicles.remove(uuid);
                return;
            }

            vehicle.tick();
        });
    }
}
