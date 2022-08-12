package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Custom.item.CustomItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static co.tantleffbeef.mcplanes.Tool.clearFolder;

public class ResourceManager implements Listener {
    private final Plugin plugin;
    private final File tempFolder;
    private final File webserverFolder;
    private File clientJar;
    private final Map<NamespacedKey, ItemStack> customItems = new HashMap<>();
    private final Map<Material, List<NamespacedKey>> customModels = new HashMap<>();

    public ResourceManager(Plugin plugin, File webserverFolder, File clientJar) {
        this.plugin = plugin;
        this.webserverFolder = webserverFolder;
        this.clientJar = clientJar;

        final File dataFolder = plugin.getDataFolder();
        tempFolder = new File(dataFolder, "temp");

        setupResources();
    }

    /**
     * Create a temp folder for hosting assets
     */
    private void setupResources() {
        // Reset the temp folder
        if (tempFolder.exists()) {
            clearFolder(tempFolder);
            tempFolder.delete();
        }

        // Reset the webserver folder
        if (webserverFolder.exists()) {
            clearFolder(webserverFolder);
            webserverFolder.delete();
        }

        // Recreate folder after clearing it
        tempFolder.mkdirs();
        webserverFolder.mkdirs();
    }

    public void addAssetsFolder(JarFile jar) {
        // only get assets inside of assets folder
        final var directory = "assets";
        final Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            final var entry = entries.nextElement();
            final var name = entry.getName();

            // check if resource is inside of assets folder
            if (!name.startsWith(directory + "/") || entry.isDirectory()) {
                continue;
            }

            // turn jar entry into zip entry so it can be extracted
            final var resourceLocation = entry.getName();
            final var zipEntry = jar.getEntry(resourceLocation);

            saveFile(tempFolder, resourceLocation, jar, zipEntry);
        }
    }

    public void registerItem(CustomItem item) {
        ItemStack customItemStack = new ItemStack(item.baseMaterial());
        ItemMeta meta = customItemStack.getItemMeta();

        meta.setDisplayName(item.name());

        if (item.model() != null) {
            // If there isn't already an entry in the custom model list for this material, make one
            if (!customModels.containsKey(item.baseMaterial()))
                customModels.put(item.baseMaterial(), new ArrayList<>());

            // Add custom model data to item and add model to array
            List<NamespacedKey> modelList = customModels.get(item.baseMaterial());
            modelList.add(item.model());
            meta.setCustomModelData(modelList.size());
        }

        // add custom item to persistent data
        final var data = meta.getPersistentDataContainer();
        final var id = item.id();
        data.set(new NamespacedKey(plugin, "customItem"), PersistentDataType.STRING, id.getNamespace() + ':' +
                id.getKey());

        // put all changes back into custom item stack
        customItemStack.setItemMeta(meta);

        // finally add custom item stack into list of custom items
        customItems.put(id, customItemStack);
    }

    private File saveFile(File folder, String path, JarFile jar, ZipEntry zipFile) {
        final File javaFile = new File(folder, path);

        // make directories if they don't exist
        javaFile.getParentFile().mkdirs();

        // Stream in from jar file and out to actual file
        try (var out = new BufferedOutputStream(new FileOutputStream(javaFile))) {
            try (var in = new BufferedInputStream(jar.getInputStream(zipFile))) {
                byte[] buffer = new byte[4096];
                int nBytes;
                while ((nBytes = in.read(buffer)) > 0) {
                    out.write(buffer, 0, nBytes);
                }

                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return javaFile;
    }

    public void compileResources() throws IOException {
        // create pack.mcmeta file
        JsonObject mcMeta = new JsonObject();
        mcMeta.addProperty("pack_format", 9);
        mcMeta.addProperty("description", "Autogenerated by MCPlanes");

        // save pack.mcmeta
        try (Writer writer = new PrintWriter(new FileOutputStream(new File(tempFolder, "pack.mcmeta")))) {
            writer.write(new Gson().toJson(mcMeta));
        }

        // make model files
        for (Map.Entry<Material, List<NamespacedKey>> entry : customModels.entrySet()) {
            if (entry.getValue() == null)
                continue;

            // path to model file
            final NamespacedKey blockId = entry.getKey().getKey();
            final String path = "assets/" + blockId.getNamespace() + "/models/item/" + blockId.getKey() + ".json";

            final File modelFile;

            // save model's file to resource pack
            try (var jar = new JarFile(clientJar)) {
                var jarEntry = jar.getEntry(path);

                plugin.getLogger().info("Saving file " + path);
                modelFile = saveFile(tempFolder, path, jar, jarEntry);
            }

            // create a json object which will store the model's current data TODO: fix
            JsonObject modelJson;

            // load model's current data into json object
            try (var in = new FileInputStream(modelFile)) {
                modelJson = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            }

            // load all the custom model data into the json
            final var customModelList = entry.getValue();
            final JsonArray overrides = new JsonArray();
            for (int i = 0; i < customModelList.size(); i++) {
                JsonObject override = new JsonObject();

                // Set custom model value
                JsonObject predicate = new JsonObject();
                predicate.addProperty("custom_model_data", i + 1);
                override.add("predicate", predicate);

                // Set model to use
                final var modelKey = customModelList.get(i);
                override.addProperty("model", modelKey.getNamespace() + ":" + modelKey.getKey());

                // add this model to the list of all the models
                overrides.add(override);
            }

            // add all the overrides to the model file
            modelJson.add("overrides", overrides);

            // finally save the model file
            modelFile.delete(); // clear the file first
            try (Writer writer = new PrintWriter(new FileOutputStream(modelFile))) {
                writer.write(new Gson().toJson(modelJson));
            }
        }
    }
}
