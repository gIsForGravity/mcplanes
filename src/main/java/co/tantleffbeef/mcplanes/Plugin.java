package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Commands.ResourceGiveCommand;
import co.tantleffbeef.mcplanes.Custom.item.SimpleItem;
import co.tantleffbeef.mcplanes.Listeners.*;
import co.tantleffbeef.mcplanes.Listeners.protocol.ServerboundPlayerInputListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.RecipeChoice;
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
    private RecipeManager recipeManager;
    private WebServer webServer;
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
        recipeManager = new RecipeManager(this);
        vehicleManager = new VehicleManager(this);

        saveDefaultConfig();
        addDefaultsToConfig();

        // Location that webserver will host files at
        final File webserverFolder = new File(getDataFolder(), "www");
        webServer = new WebServer(webserverFolder, getConfig().getString("webserver-bind"),
                getConfig().getInt("webserver-port"));

        protocolManager = ProtocolLibrary.getProtocolManager();
        mcVersion = getServer().getBukkitVersion().split("-", 2)[0];

        // // Listeners!!!
        // ProtocolLib listeners
        protocolManager.addPacketListener(new ServerboundPlayerInputListener(this));

        // Check if there is a client jar with this version downloaded and if not download a new one
        final var versionsFolder = new File(getDataFolder(), "versions");
        final var clientJar = new File(versionsFolder, "client-" + mcVersion + ".jar");
        if (clientJar.exists() &&
                clientJar.isFile()) {
            getLogger().info("Jarfile with version " + mcVersion + " found.");
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

        // Bukkit Listeners
        registerListener(new VehicleEnterListener(this));
        registerListener(new VehicleExitListener(this));
        registerListener(new PlayerJoinListener(this,
                getConfig().getString("webserver-url"), resourceManager));
        if (getConfig().getBoolean("crafting.unlock-recipes")) {
            registerListener(new EntityPickupItemListener(this, recipeManager));
            registerListener(new InventoryMoveItemListener(this, recipeManager));
        }

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

        getLogger().info("Running garbage collector");
        System.gc();

        webServer.start();
    }

    @Override
    public void onDisable() {
        webServer.stop();
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
        resourceManager.registerItem(new SimpleItem(this, "tail", true, "Tail"));
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
        // register battery recipe
        final var batteryKey = new NamespacedKey(this, "battery");
        final var battery = new ShapedRecipe(batteryKey, resourceManager.getCustomItem(batteryKey))
                .shape(
                        "cic",
                        "rgr",
                        "cgc")
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('g', Material.GOLD_INGOT)
                .setIngredient('r', Material.REDSTONE_BLOCK)
                .setIngredient('i', Material.IRON_BLOCK);
        getServer().addRecipe(battery);
        recipeManager.registerUnlockableRecipe(batteryKey,
                Material.COPPER_INGOT,
                Material.GOLD_INGOT,
                Material.REDSTONE_BLOCK,
                Material.IRON_BLOCK);

        // register blowtorch recipe
        final var blowtorchKey = new NamespacedKey(this, "blowtorch");
        final var blowtorch = new ShapedRecipe(blowtorchKey, resourceManager.getCustomItem(blowtorchKey))
                .shape(
                        " f ",
                        "imi",
                        "iii")
                .setIngredient('f', Material.FLINT_AND_STEEL)
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('m', Material.FIRE_CHARGE);
        getServer().addRecipe(blowtorch);
        recipeManager.registerUnlockableRecipe(blowtorchKey,
                Material.FLINT_AND_STEEL,
                Material.IRON_INGOT,
                Material.FIRE_CHARGE);

        // register crude oil recipe
        final var crudeOilKey = new NamespacedKey(this, "crude_oil");
        var crudeOil = new BlastingRecipe(crudeOilKey,
                resourceManager.getCustomItem(crudeOilKey),
                new RecipeChoice.MaterialChoice(Material.COAL, Material.CHARCOAL),
                1f, 200);
        getServer().addRecipe(crudeOil);
        recipeManager.registerUnlockableRecipe(crudeOilKey,
                Material.COAL,
                Material.CHARCOAL);

        // register engine recipe
        final var engineKey = new NamespacedKey(this, "engine");
        final var engine = new ShapedRecipe(engineKey, resourceManager.getCustomItem(engineKey))
                .shape(
                        "ggg",
                        "ini",
                        "rrr")
                .setIngredient('g', Material.GOLD_INGOT)
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('r', Material.REDSTONE_BLOCK)
                .setIngredient('n', Material.NETHERITE_INGOT);
        getServer().addRecipe(engine);
        recipeManager.registerUnlockableRecipe(engineKey,
                Material.GOLD_INGOT,
                Material.IRON_INGOT,
                Material.REDSTONE_BLOCK,
                Material.NETHERITE_INGOT);

        // register tail recipe
        final var tailKey = new NamespacedKey(this, "tail");
        final var tail = new ShapedRecipe(tailKey, resourceManager.getCustomItem(tailKey))
                .shape(
                        " i",
                        "pi")
                .setIngredient('p', Material.PHANTOM_MEMBRANE)
                .setIngredient('i', Material.IRON_INGOT);
        getServer().addRecipe(tail);
        recipeManager.registerUnlockableRecipe(tailKey,
                Material.PHANTOM_MEMBRANE,
                Material.IRON_INGOT);

        // register wing recipe
        final var wingKey = new NamespacedKey(this, "wing");
        final var wing = new ShapedRecipe(wingKey, resourceManager.getCustomItem(wingKey))
                .shape(
                        "  i",
                        " pp",
                        "iii")
                .setIngredient('p', Material.PHANTOM_MEMBRANE)
                .setIngredient('i', Material.IRON_INGOT);
        getServer().addRecipe(wing);
        recipeManager.registerUnlockableRecipe(wingKey,
                Material.PHANTOM_MEMBRANE,
                Material.IRON_INGOT);

        // register fuselage recipe
        final var fuselageKey = new NamespacedKey(this, "fuselage");
        final var fuselage = new ShapedRecipe(fuselageKey, resourceManager.getCustomItem(fuselageKey))
                .shape(
                        "iii",
                        "III",
                        "iii")
                .setIngredient('I', Material.IRON_BLOCK)
                .setIngredient('i', Material.IRON_INGOT);
        getServer().addRecipe(fuselage);
        recipeManager.registerUnlockableRecipe(fuselageKey,
                Material.IRON_BLOCK,
                Material.IRON_INGOT);

        // register powertool recipe
        final var powertoolKey = new NamespacedKey(this, "powertool");
        final var powertool = new ShapedRecipe(powertoolKey, resourceManager.getCustomItem(powertoolKey))
                .shape(
                        "dgg",
                        " ir")
                .setIngredient('g', Material.GOLD_INGOT)
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('r', Material.REDSTONE)
                .setIngredient('d', Material.DIAMOND);;
        getServer().addRecipe(powertool);
        recipeManager.registerUnlockableRecipe(powertoolKey,
                Material.GOLD_INGOT,
                Material.IRON_INGOT,
                Material.REDSTONE,
                Material.DIAMOND);

        // register wrench recipe
        final var wrenchKey = new NamespacedKey(this, "wrench");
        final var wrench = new ShapedRecipe(wrenchKey, resourceManager.getCustomItem(wrenchKey))
                .shape(
                        " i ",
                        "ii ",
                        "  i")
                .setIngredient('i', Material.IRON_INGOT);
        getServer().addRecipe(wrench);
        recipeManager.registerUnlockableRecipe(wrenchKey,
                Material.IRON_INGOT);

        // register glue recipe
        final var glueKey = new NamespacedKey(this, "glue");
        final var glue = new ShapedRecipe(glueKey, resourceManager.getCustomItem(glueKey))
                .shape(
                        "lsl",
                        "lsl",
                        " l ")
                .setIngredient('l', Material.LEATHER)
                .setIngredient('s', Material.SLIME_BALL);
        getServer().addRecipe(glue);
        recipeManager.registerUnlockableRecipe(glueKey,
                Material.LEATHER,
                Material.SLIME_BALL);

        /*final var aircrafterKey = new NamespacedKey(this, "aircrafter");
        final var aircrafter = new ShapedRecipe(aircrafterKey, resourceManager.getCustomItem(aircrafterKey))
                .shape(
                        "iii",
                        "III",
                        "iii")
                .setIngredient('w', Material.IRON_BLOCK)
                .setIngredient('g', Material.DIRT)
                .setIngredient('b', Material.BEEF)
                .setIngredient('p', Material.DIRT)
                .setIngredient('t', Material.IRON_INGOT)
                .setIngredient('c', Material.CRAFTING_TABLE)
                .setIngredient('d', Material.DIAMOND)
                .setIngredient('i', Material.IRON_BLOCK);
        getServer().addRecipe(aircrafter);*/
    }

    /**
     * surrounds getCommand() with Objects.requireNonNull
     * @param name the name of the command as defined in plugin.yml
     * @return the command
     */
    public PluginCommand getCommandRNN(String name) {
        return Objects.requireNonNull(getCommand(name));
    }

    /**
     * Adds default values to config in case they're missing for some reason
     */
    private void addDefaultsToConfig() {
        getConfig().addDefault("webserver-bind", "0.0.0.0");
        getConfig().addDefault("webserver-url", "127.0.0.1");
        getConfig().addDefault("webserver-port", 8467);

        getConfig().addDefault("crafting.allow-crafting", true);
        getConfig().addDefault("crafting.unlock-recipes", true);

        getConfig().options().copyDefaults(true);

        saveConfig();
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

        final byte[] memoryBuffer = new byte[Tools.FILE_BUFFER_SIZE];
        double lastPercent = 0f;
        int currentSize = 0;

        // buffered input from the web, buffered output to hard drive
        try (var in = new BufferedInputStream(connection.getInputStream());
            var out = new BufferedOutputStream(new FileOutputStream(jarFileLocation)) ) {

            int readBytes = 0;
            while ((readBytes = in.read(memoryBuffer)) > 0) {
                currentSize += readBytes;
                final double percentage = (float) currentSize / totalSize * 100;

                // Every time it goes up 10% tell the user
                if (lastPercent % 10 > percentage % 10)
                    getLogger().info("Downloading client-" + mcVersion + ".jar (" + String.valueOf(percentage).split("\\.")[0] + "%)");

                lastPercent = percentage;

                // don't forget to save downloaded data
                out.write(memoryBuffer, 0, readBytes);
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
