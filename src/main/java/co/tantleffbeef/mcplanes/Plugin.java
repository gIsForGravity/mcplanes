package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Listeners.VehicleEnterListener;
import co.tantleffbeef.mcplanes.Listeners.VehicleExitListener;
import co.tantleffbeef.mcplanes.Listeners.protocol.ServerboundPlayerInputListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.jar.JarFile;

public class Plugin extends JavaPlugin {
    private ProtocolManager protocolManager;
    private VehicleManager vehicleManager;
    private ResourceManager resourceManager;

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        vehicleManager = new VehicleManager(this);
        resourceManager = new ResourceManager(this);

        // # Listener time!!!!

        // ProtocolLib listeners
        protocolManager.addPacketListener(new ServerboundPlayerInputListener(this));

        // Bukkit Listeners
        registerListener(new VehicleEnterListener(this));
        registerListener(new VehicleExitListener(this));

        // Maybe setup resources would've been a better name but maybe I'm lazy - gavint
        setupTextures();
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void setupTextures() {
        // Adds all the textures and models in the resources folder to the resource pack
        try (JarFile jar = new JarFile(getFile())) {
            resourceManager.addAssetsFolder(jar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
