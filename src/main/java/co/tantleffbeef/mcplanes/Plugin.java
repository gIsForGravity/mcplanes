package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Listeners.VehicleEnterListener;
import co.tantleffbeef.mcplanes.Listeners.VehicleExitListener;
import co.tantleffbeef.mcplanes.Listeners.protocol.ServerboundPlayerInputListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    private ProtocolManager manager;
    private VehicleManager vehicleManager;

    public ProtocolManager getManager() {
        return manager;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    @Override
    public void onEnable() {
        manager = ProtocolLibrary.getProtocolManager();


        // ProtocolLib listeners
        manager.addPacketListener(new ServerboundPlayerInputListener(this, ListenerPriority.MONITOR));

        // Bukkit Listeners
        registerListener(new VehicleEnterListener(this));
        registerListener(new VehicleExitListener(this));
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }
}
