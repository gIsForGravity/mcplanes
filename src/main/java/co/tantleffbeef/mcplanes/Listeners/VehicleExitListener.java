package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleExitListener extends AbstractListener {
    public VehicleExitListener(Plugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent event) {
        var vehicle = event.getVehicle();
        var data = vehicle.getPersistentDataContainer();

        if (!plugin.checkIfVehicle(data))
            return;


    }
}
