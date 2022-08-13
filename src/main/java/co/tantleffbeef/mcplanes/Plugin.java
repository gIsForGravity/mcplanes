package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Commands.ResourceGiveCommand;
import co.tantleffbeef.mcplanes.Custom.item.SimpleItem;
import co.tantleffbeef.mcplanes.Listeners.VehicleEnterListener;
import co.tantleffbeef.mcplanes.Listeners.VehicleExitListener;
import co.tantleffbeef.mcplanes.Listeners.protocol.ServerboundPlayerInputListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        saveDefaultConfig();

        // Location that webserver will host files at
        final File webserverFolder = new File(getDataFolder(), "www");

        protocolManager = ProtocolLibrary.getProtocolManager();
        vehicleManager = new VehicleManager(this);
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

        // initialize resource manager now that client jar has been downloaded
        resourceManager = new ResourceManager(this, webserverFolder, clientJar);

        // Commands!
        registerCommands();

        // Maybe setup resources would've been a better name, but maybe I'm lazy - gavint
        setupTextures();

        registerItems();
        registerRecipes();

        try {
            resourceManager.compileResources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Registers items with the resourceManager
     */
    private void registerItems() {
        resourceManager.registerItem(new SimpleItem(this, "battery", true, "Battery"));
        resourceManager.registerItem(new SimpleItem(this, "blowtorch", true, "Blowtorch"));
        resourceManager.registerItem(new SimpleItem(this, "crude_oil", true, "Crude Oil"));
        resourceManager.registerItem(new SimpleItem(this, "engine", true, "Engine"));
        resourceManager.registerItem(new SimpleItem(this, "fuel", true, "Fuel"));
        resourceManager.registerItem(new SimpleItem(this, "fuselage", true, "Fuselage"));
        resourceManager.registerItem(new SimpleItem(this, "glue", true, "Glue"));
        resourceManager.registerItem(new SimpleItem(this, "powertool", true, "Power Tool"));
        resourceManager.registerItem(new SimpleItem(this, "rudder", true, "Rudder"));
        resourceManager.registerItem(new SimpleItem(this, "wing", true, "Wing"));
        resourceManager.registerItem(new SimpleItem(this, "wrench", true, "Wrench"));
    }

    /**
     * Creates CommandExecutors and attaches them to PluginCommands
     */
    private void registerCommands() {
        new ResourceGiveCommand(getCommandRNN("resourcegive"), resourceManager);
    }

    private void registerRecipes() {
        
    }

    /**
     * surrounds getCommand() with Objects.requireNonNull
     * @param name the name of the command as defined in plugin.yml
     * @return the command
     */
    public PluginCommand getCommandRNN(String name) {
        return Objects.requireNonNull(getCommand(name));
    }

    private void downloadClientJar() throws IOException {
        String versionUrl = null;
        String jarUrl;

        getLogger().info("Locating jar with version \"" + mcVersion + "\".");

        final var versionList =
                downloadJsonFile(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"))
                        .getAsJsonArray("versions");

        // Loop through list of versions and try to find the one the server is running on
        for (var version : versionList) {

            if (!version.isJsonObject())
                continue;
            final var versionNumber = version.getAsJsonObject().get("id").getAsString();
            if (!versionNumber.equals(mcVersion))
                continue;

            versionUrl = version.getAsJsonObject().get("url").getAsString();
            break;
        }

        assert versionUrl != null;
        final var versionJson = downloadJsonFile(new URL(versionUrl));

        // get jar url
        jarUrl = versionJson.getAsJsonObject("downloads").getAsJsonObject("client").get("url").getAsString();

        // Download jar
        getLogger().info("Jar located. Downloading.");
        final URL jar = new URL(jarUrl);
        HttpURLConnection connection = (HttpURLConnection) jar.openConnection();

        // current size / total size = progress
        final double totalSize = connection.getContentLength();

        final File versionsFolder = new File(getDataFolder(), "versions");
        final File jarFileLocation = new File(versionsFolder, "client-" + mcVersion + ".jar");

        versionsFolder.mkdirs();

        final byte[] data = new byte[4096];
        double lastPercent = 0f;
        int currentSize = 0;

        try ( var in = new BufferedInputStream(connection.getInputStream());
            var fos = new FileOutputStream(jarFileLocation) ) {
            var out = new BufferedOutputStream(fos);

            int x = 0;
            while ((x = in.read(data)) >= 0) {
                currentSize += x;
                final double percentage = (float) currentSize / totalSize * 100;

                // Every time it goes up 10% tell the user
                if (lastPercent % 10 > percentage % 10)
                    getLogger().info("Downloading client-" + mcVersion + ".jar (" + String.valueOf(percentage).split("\\.")[0] + "%)");

                lastPercent = percentage;

                // don't forget to save downloaded data
                out.write(data, 0, x);
            }

            out.flush();
        }

        getLogger().info("Download complete!");
    }

    private static JsonObject downloadJsonFile(URL url) throws IOException {
        JsonObject json;

        try (InputStream is = url.openStream()) {
            json = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
        }

        return json;
    }
}
