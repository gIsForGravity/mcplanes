package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Custom.item.CustomItem;
import com.google.gson.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static co.tantleffbeef.mcplanes.Tool.clearFolder;

public class ResourceManager implements Listener {
    private final Plugin plugin;
    private final File tempFolder;
    private final File webserverFolder;
    private final Gson gson;
    private final File clientJar;
    private final Map<NamespacedKey, ItemStack> customItems = new HashMap<>();
    private final Map<Material, List<NamespacedKey>> customModels = new HashMap<>();

    public ResourceManager(Plugin plugin, File webserverFolder, File clientJar) {
        this.plugin = plugin;
        this.webserverFolder = webserverFolder;
        this.clientJar = clientJar;

        this.gson = new GsonBuilder().setPrettyPrinting().create();

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

        meta.setDisplayName(ChatColor.RESET + item.name());

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
        data.set(new NamespacedKey(plugin, "customItem"), PersistentDataType.STRING, id.toString());

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

    // return list of custom item ids
    public Set<NamespacedKey> getItemIdList() {
        return customItems.keySet();
    }

    public ItemStack getCustomItem(NamespacedKey key) {
        return new ItemStack(Objects.requireNonNull(customItems.get(key)));
    }

    public void compileResources() throws IOException {
        plugin.getLogger().info("Building resources");

        // create pack.mcmeta file
        JsonObject mcMeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 9);
        pack.addProperty("description", "Autogenerated by MCPlanes");
        mcMeta.add("pack", pack);

        // save pack.mcmeta
        try (Writer writer = new PrintWriter(new FileOutputStream(new File(tempFolder, "pack.mcmeta")))) {
            writer.write(gson.toJson(mcMeta));
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
                writer.write(gson.toJson(modelJson));
            }
        }

        saveResourcesToZip(tempFolder, new File(webserverFolder, "resources.zip"));
    }

    private void saveResourcesToZip(File inputFolder, File outputFile) {
        if (outputFile.exists())
            outputFile.delete();

        // Create a list to store the names of all the files that'll go in the zip
        final List<String> fileNames = new ArrayList<>();
        addFilesToList(fileNames, inputFolder, "");

        plugin.getLogger().info("Saving resource pack to zip file");
        // make zip file
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outputFile))) {
            final byte[] buffer = new byte[4096];

            // loop through all the files in the folder
            for (String fileName : fileNames) {
                plugin.getLogger().info("writing " + fileName);

                // Add new zip entry to the stream
                zip.putNextEntry(new ZipEntry(fileName));

                // write file into zip file
                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(inputFolder,
                        fileName)))) {
                    // read bytes from file
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) > 0) {
                        // and copy bytes into zip
                        zip.write(buffer, 0, bytesRead);
                    }
                }

                // prepare to write the next zip entry
                zip.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addFilesToList(List<String> files, File directory, String parent) {
        if (!directory.exists() && !directory.isDirectory())
            return;

        final String prefix;

        if (parent.equals(""))
            prefix = "";
        else
            prefix = parent + "/";

        for (File f : Objects.requireNonNull(directory.listFiles())) {
            // if it's a directory then run this in the subdirectory
            if (f.isDirectory()) {
                addFilesToList(files, f, prefix + f.getName());
                continue;
            }

            // otherwise just add the filename to the list
            files.add(prefix + f.getName());
        }
    }
}
