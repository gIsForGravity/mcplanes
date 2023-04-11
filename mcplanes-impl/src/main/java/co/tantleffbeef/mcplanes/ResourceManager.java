package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.custom.item.CustomItemType;
import co.tantleffbeef.mcplanes.pojo.serialize.CustomItemNbt;
import com.google.gson.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static co.tantleffbeef.mcplanes.Tools.clearFolder;

public class ResourceManager implements Listener {
    private final McPlanes plugin;
    private final KeyManager<CustomNbtKey> nbtKeyManager;
    private final File tempFolder;
    private final File webserverFolder;
    private final Gson gson;
    private final File clientJar;
    private final Map<NamespacedKey, CustomItemType> customItems = new HashMap<>();
    private final Map<NamespacedKey, ItemStack> customItemStacks = new HashMap<>();
    private final Map<Material, List<NamespacedKey>> customModels = new HashMap<>();
    private byte[] resourcePackHash;

    public ResourceManager(McPlanes plugin, KeyManager<CustomNbtKey> nbtKeyManager, File webserverFolder, File clientJar) {
        this.plugin = plugin;
        this.nbtKeyManager = nbtKeyManager;
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

    public void registerItem(CustomItemType item) {
        ItemStack customItemStack = new ItemStack(item.baseMaterial());
        ItemMeta meta = customItemStack.getItemMeta();
        assert meta != null;

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
        var nbt = new CustomItemNbt(item.id(), false);
        item.addAdditionalNbtItemData(nbt);
        nbt.saveToPersistentDataContainer(data, nbtKeyManager);


        // put all changes back into custom item stack
        customItemStack.setItemMeta(meta);

        // finally add custom item stack into list of custom items
        customItemStacks.put(item.id(), customItemStack);
        customItems.put(item.id(), item);
    }

    private File saveFile(File folder, String path, JarFile jar, ZipEntry zipFile) {
        final File javaFile = new File(folder, path);

        // make directories if they don't exist
        javaFile.getParentFile().mkdirs();

        // Stream in from jar file and out to actual file
        try (var out = new BufferedOutputStream(new FileOutputStream(javaFile))) {
            try (var in = new BufferedInputStream(jar.getInputStream(zipFile))) {
                // Buffer used to hold data in memory as its transfered from jar to file
                byte[] buffer = new byte[Tools.FILE_BUFFER_SIZE];

                int nBytes; // how much (uncompressed) data is read from the jar
                while ((nBytes = in.read(buffer)) > 0) {
                    // Read FILE_BUFFER_SIZE bytes from jar and write them into the file
                    out.write(buffer, 0, nBytes);
                }

                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return javaFile;
    }

    public String getResourcePackFilename() {
        return "resources.zip";
    }

    // return list of custom item ids
    public Set<NamespacedKey> getItemIdList() {
        return customItemStacks.keySet();
    }

    public ItemStack getCustomItemStack(NamespacedKey key) {
        return new ItemStack(Objects.requireNonNull(customItemStacks.get(key)));
    }

    public CustomItemType getCustomItem(NamespacedKey key) {
        return Objects.requireNonNull(customItems.get(key));
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

            // create a json object which will store the model's current data
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

        // The file that the resource pack is to be saved to
        File resourcePackFile = new File(webserverFolder, "resources.zip");

        // save resource pack to a zip file so it can be sent to players
        saveResourcesToZip(tempFolder, resourcePackFile, true);

        //   hash resource pack so clients can know if the resource pack has
        // changed since the last time they joined the server
        hashResourcePack(resourcePackFile);
    }

    /**
     * Takes a resource pack folder (or really any folder) and saves it all to a zip file
     * @param inputFolder the folder to be compressed
     * @param outputFile the file to output said folder to
     * @param compressZip whether to compress the zip file or not
     */
    private void saveResourcesToZip(File inputFolder, File outputFile, boolean compressZip) {
        if (outputFile.exists())
            outputFile.delete();

        // Create a list to store the names of all the files that'll go in the zip
        final List<String> fileNames = new ArrayList<>();
        addFilesToList(fileNames, inputFolder, "");

        plugin.getLogger().info("Saving resource pack to zip file");
        // make zip file
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outputFile))) {
            if (compressZip)
                // Tell zip stream to deflate (compress) zip file
                zip.setMethod(ZipOutputStream.DEFLATED);
            else
                // Tell zip stream to just store files as is
                zip.setMethod(ZipOutputStream.STORED);

            // Buffer to hold data as it's transferred from file to zip file
            final byte[] buffer = new byte[Tools.FILE_BUFFER_SIZE];

            // loop through all the files in the folder
            for (String fileName : fileNames) {
                plugin.getLogger().info("Writing " + fileName);

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
        // if the directory doesn't exist or if the file isn't a directory then just leave
        // we should never get here but just ignore
        if (!directory.exists() || !directory.isDirectory())
            return;

        // create a prefix that goes on the beginning
        // if this is the very first directory in the list (for example "assets") then
        // we don't want it to have a / at the beginning (/assets/minecraft/models is
        // not right).
        // instead we want the very first directory to have nothing before it:
        // "assets/minecraft/models" instead of "/assets/minecraft/models"
        final String prefix;

        if (parent.equals(""))
            prefix = "";
        else
            prefix = parent + "/";

        for (File f : Objects.requireNonNull(directory.listFiles())) {
            // if it's a directory then run the same function in the subdirectory
            if (f.isDirectory()) {
                addFilesToList(files, f, prefix + f.getName());
                continue;
            }

            // otherwise just add the filename to the list
            files.add(prefix + f.getName());
        }
    }

    private void hashResourcePack(File resourcePackFile) {
        // sets local variable resourcePackHash to the hash of the resources.zip which
        // will then be passed to the player upon login
        resourcePackHash = Tools.createSha1(resourcePackFile).clone();
    }

    public byte[] getResourcePackHash() {
        return resourcePackHash;
    }
}
