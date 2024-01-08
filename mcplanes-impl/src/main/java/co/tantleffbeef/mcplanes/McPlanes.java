package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.commands.ResourceGiveCommand;
import co.tantleffbeef.mcplanes.custom.item.PlaceableItemType;
import co.tantleffbeef.mcplanes.custom.item.SimpleItemType;
import co.tantleffbeef.mcplanes.custom.item.SimplePlaceableItemType;
import co.tantleffbeef.mcplanes.custom.item.VehicleItemType;
import co.tantleffbeef.mcplanes.listeners.*;
import co.tantleffbeef.mcplanes.listeners.protocol.CustomBlockDigListener;
import co.tantleffbeef.mcplanes.listeners.protocol.ServerboundPlayerInputListener;
import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.vehicles.P51;
import co.tantleffbeef.mcplanes.vehicles.VehicleKey;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.jar.JarFile;

public class McPlanes extends JavaPlugin implements ResourceApi {
    @SuppressWarnings("FieldCanBeLocal")
    private ProtocolManager protocolManager;
    private VehicleManager vehicleManager;
    private ResourceManager resourceManager;
    private RecipeManager recipeManager;
    private InternalsTools internalsTools;
    private WebServer webServer;
    private BlockManager blockManager;
    private KeyManager<CustomNbtKey> persistentDataKeyManager;
    @SuppressWarnings("FieldCanBeLocal")
    private KeyManager<VehicleKey> vehicleKeyManager;
    private String mcVersion;

    @Override
    public void onEnable() {
        final var servicesManager = getServer().getServicesManager();

        servicesManager.register(ResourceApi.class, this, this, ServicePriority.Normal);
        final var registration = servicesManager.getRegistration(ResourceApi.class);
        if (registration == null)
            getLogger().severe("despite me just registering it, it is not registered");
        else
            getLogger().info("it is actually registered you just got trolled");

        internalsTools = new MCPInternalsTools();
        recipeManager = new MCPRecipeManager(this);
        vehicleManager = new VehicleManager();
        vehicleManager.start(this, getServer().getScheduler());

        persistentDataKeyManager = new KeyManager<>(this);
        CustomNbtKey.registerKeys(persistentDataKeyManager);

        vehicleKeyManager = new KeyManager<>(this);
        VehicleKey.registerKeys(vehicleKeyManager);

        saveDefaultConfig();
        addDefaultsToConfig();

        // Location that webserver will host files at
        final File webserverFolder = new File(getDataFolder(), "www");
        webServer = new WebServer(webserverFolder, getConfig().getString("webserver-bind"),
                getConfig().getInt("webserver-port"));

        protocolManager = ProtocolLibrary.getProtocolManager();
        mcVersion = getServer().getBukkitVersion().split("-", 2)[0];

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
        resourceManager = new MCPResourceManager(this,
                persistentDataKeyManager,
                webserverFolder,
                clientJar,
                getConfig().getString("webserver-url"));
        blockManager = new MCPBlockManager(persistentDataKeyManager, getServer(), resourceManager);

        // // Listeners!!!

        // ProtocolLib listeners
        protocolManager.addPacketListener(new ServerboundPlayerInputListener(this, vehicleManager));
        protocolManager.addPacketListener(new CustomBlockDigListener(this, blockManager));

        // Bukkit Listeners
        registerListener(new CustomVehicleEnterExitListener(vehicleManager));
        registerListener(new PlayerResourceListener(this,
                resourceManager));
        registerListener(new CustomBlockPlaceBreakListener(blockManager, resourceManager, getServer().getPluginManager(), persistentDataKeyManager));
        registerListener(new InteractableItemListener(persistentDataKeyManager, resourceManager));
        registerListener(new PhysicsObjectCollisionListener(getServer()));
        registerListener(new InteractableBlockListener(blockManager));
        if (getConfig().getBoolean("crafting.unlock-recipes")) {
            registerListener(new EntityPickupItemListener(recipeManager));
            registerListener(new InventoryMoveItemListener(recipeManager));
        }

        // Commands!
        registerCommands();

        // Maybe setup resources would've been a better name, but maybe I'm lazy - gavint
        setupTextures();
        // Registers the directory "vehicles" in the assets/namespace/textures folder
        resourceManager.registerItemTextureAtlasDirectory("vehicles");

        registerItems();
        registerRecipes();

        // Allow everyone to register items and stuff in enable. On the first tick, resources will be compiled
        getServer().getScheduler().runTask(this, () -> resourceManager.compileResourcesAsync(getServer().getScheduler()));

        getLogger().info("Running garbage collector");
        System.gc();

        webServer.start();

        getCommandRNN("p51").setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof Player player))
                return false;

            final var p51 = P51.spawn(getServer().getPluginManager(), player.getLocation(),
                    resourceManager.getCustomItemStack(new NamespacedKey(this, "p_51")));
            vehicleManager.registerVehicle(p51);

            lastp51 = p51.entity;
            return true;
        });

        getCommandRNN("ride").setExecutor((sender, command, label, args) -> {
            if (lastp51 == null)
                return false;

            if (!(sender instanceof Player player))
                return false;

            sender.sendMessage("riding");
            lastp51.addPassenger(player);
            return true;
        });

        Collider.startTicking(this);
    }

    private Entity lastp51;

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
        // Items
        resourceManager.registerItem(new SimpleItemType(this, "battery", true, "Battery"));
        resourceManager.registerItem(new SimpleItemType(this, "blowtorch", true, "Blowtorch"));
        resourceManager.registerItem(new SimpleItemType(this, "crude_oil", true, "Crude Oil"));
        resourceManager.registerItem(new SimpleItemType(this, "engine", true, "Engine"));
        resourceManager.registerItem(new SimpleItemType(this, "fuel", true, "Fuel"));
        resourceManager.registerItem(new SimpleItemType(this, "fuselage", true, "Fuselage"));
        resourceManager.registerItem(new SimpleItemType(this, "glue", true, "Glue"));
        resourceManager.registerItem(new SimpleItemType(this, "powertool", true, "Power Tool"));
        resourceManager.registerItem(new SimpleItemType(this, "tail", true, "Tail"));
        resourceManager.registerItem(new SimpleItemType(this, "wing", true, "Wing"));
        resourceManager.registerItem(new SimpleItemType(this, "wrench", true, "Wrench"));

        resourceManager.registerItem(new VehicleItemType(this, "p_51", true, "P-51"));

        // Blocks
        registerItemAndBlock(new SimplePlaceableItemType(this, "aircrafter", true, "Aircrafter"));
    }

    private void registerItemAndBlock(PlaceableItemType item) {
        resourceManager.registerItem(item);
        blockManager.registerBlock(item.asBlock());
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
        final var battery = new ShapedRecipe(batteryKey, resourceManager.getCustomItemStack(batteryKey))
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
        final var blowtorch = new ShapedRecipe(blowtorchKey, resourceManager.getCustomItemStack(blowtorchKey))
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
                resourceManager.getCustomItemStack(crudeOilKey),
                new RecipeChoice.MaterialChoice(Material.COAL, Material.CHARCOAL),
                1f, 200);
        getServer().addRecipe(crudeOil);
        recipeManager.registerUnlockableRecipe(crudeOilKey,
                Material.COAL,
                Material.CHARCOAL);

        // register engine recipe
        final var engineKey = new NamespacedKey(this, "engine");
        final var engine = new ShapedRecipe(engineKey, resourceManager.getCustomItemStack(engineKey))
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
        final var tail = new ShapedRecipe(tailKey, resourceManager.getCustomItemStack(tailKey))
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
        final var wing = new ShapedRecipe(wingKey, resourceManager.getCustomItemStack(wingKey))
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
        final var fuselage = new ShapedRecipe(fuselageKey, resourceManager.getCustomItemStack(fuselageKey))
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
        final var powertool = new ShapedRecipe(powertoolKey, resourceManager.getCustomItemStack(powertoolKey))
                .shape(
                        "dgg",
                        " ir")
                .setIngredient('g', Material.GOLD_INGOT)
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('r', Material.REDSTONE)
                .setIngredient('d', Material.DIAMOND);
        getServer().addRecipe(powertool);
        recipeManager.registerUnlockableRecipe(powertoolKey,
                Material.GOLD_INGOT,
                Material.IRON_INGOT,
                Material.REDSTONE,
                Material.DIAMOND);

        // register wrench recipe
        final var wrenchKey = new NamespacedKey(this, "wrench");
        final var wrench = new ShapedRecipe(wrenchKey, resourceManager.getCustomItemStack(wrenchKey))
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
        final var glue = new ShapedRecipe(glueKey, resourceManager.getCustomItemStack(glueKey))
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
    public @NotNull PluginCommand getCommandRNN(String name) {
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

            int readBytes;
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

    @Override
    public @NotNull BlockManager getBlockManager() {
        return blockManager;
    }

    @Override
    public @NotNull KeyManager<CustomNbtKey> getNbtKeyManager() {
        return persistentDataKeyManager;
    }

    @Override
    public @NotNull RecipeManager getRecipeManager() {
        return recipeManager;
    }

    @Override
    public @NotNull ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    public @NotNull InternalsTools getInternalsTools() {
        return internalsTools;
    }
}
