package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.McPlanes;
import co.tantleffbeef.mcplanes.VehicleManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.Plugin;

public class CustomVehicleEnterExitListener implements Listener {
    private final VehicleManager manager;
    public CustomVehicleEnterExitListener(VehicleManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        var vehicle = event.getVehicle();
        var data = vehicle.getPersistentDataContainer();

        // return function early if this isn't a plugin vehicle
        if (!manager.checkIfVehicle(vehicle))
            return;

        // TODO: make it so vehicles can be entered
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        var vehicle = event.getVehicle();

        if (!manager.checkIfVehicle(vehicle))
            return;

    }
}
