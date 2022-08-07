package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Listeners.VehicleEnterListener;
import co.tantleffbeef.mcplanes.Listeners.VehicleExitListener;
import co.tantleffbeef.mcplanes.Listeners.protocol.ServerboundPlayerInputListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

public class Plugin extends JavaPlugin {
    private ProtocolManager protocolManager;
    private VehicleManager vehicleManager;
    private ResourceManager resourceManager;
    private String mcVersion;

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
        mcVersion = getServer().getBukkitVersion().split("-", 2)[0];

        // // Listeners!!!
        // ProtocolLib listeners
        protocolManager.addPacketListener(new ServerboundPlayerInputListener(this));

        // Bukkit Listeners
        registerListener(new VehicleEnterListener(this));
        registerListener(new VehicleExitListener(this));

        // Check if there is a client jar with this version downloaded and if not download a new one
        final var versionsFolder = new File(getDataFolder(), "versions");
        final var clientJar = new File(versionsFolder, "client-" + mcVersion + ".jar");
        if (clientJar.exists() &&
                clientJar.isFile()) {
            getLogger().info("Jarfile with version " + mcVersion + "found.");
        } else {
            getLogger().info("No jarfile found with version " + mcVersion + ". Attempting to download.");
            try {
                // Attempt to download the client jar for this version
                downloadClientJar();
            } catch (IOException e) {
                e.printStackTrace();
                getLogger().severe(ChatColor.LIGHT_PURPLE + "There was an error downloading the jar. " +
                        "Please download it manually.");

                // shutdown the plugin since we need the jar for this
                getPluginLoader().disablePlugin(this);
            }
        }

        // Maybe setup resources would've been a better name, but maybe I'm lazy - gavint
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

    private void downloadClientJar() throws IOException {
        getLogger().info("Streaming from url...");

    }

    private static JsonObject downloadJsonFile(URL url) throws IOException {
        JsonObject json;

        try (InputStream is = new URL("https://piston-meta.mojang.com/v1/packages/92e6f1eba1748a43b8e215d0859a42bce4f999d2/1.19.json").openStream()) {
            json = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
        }

        return json;
    }
}
