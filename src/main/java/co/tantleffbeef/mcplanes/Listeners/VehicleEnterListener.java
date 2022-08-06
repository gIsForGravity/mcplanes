package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class VehicleEnterListener extends AbstractListener {
    public VehicleEnterListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        var vehicle = event.getVehicle();
        var data = vehicle.getPersistentDataContainer();

        // return function early if this isn't a plugin vehicle
        if (!plugin.getVehicleManager().checkIfVehicle(vehicle))
            return;

        // TODO: make it so vehicles can be entered
    }
}
