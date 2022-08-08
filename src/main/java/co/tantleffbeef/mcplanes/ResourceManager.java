package co.tantleffbeef.mcplanes;

import co.tantleffbeef.mcplanes.Custom.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import static co.tantleffbeef.mcplanes.Tool.clearFolder;

public class ResourceManager implements Listener {
    private final Plugin plugin;
    private File tempFolder;
    private File webserverFolder;
    private final Map<NamespacedKey, ItemStack> customItems = new HashMap<>();
    private final Map<Material, List<NamespacedKey>> customModels = new HashMap<>();

    public ResourceManager(Plugin plugin, File webserverFolder) {
        this.plugin = plugin;
        this.webserverFolder = webserverFolder;
        setupResources();
    }

    /**
     * Create a temp folder for hosting assets
     */
    private void setupResources() {
        File dataFolder = plugin.getDataFolder();
        tempFolder = new File(dataFolder, "temp");
        webserverFolder = new File(dataFolder, "www");

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

            saveFile(resourceLocation, jar, zipEntry);
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

    private void saveFile(String path, JarFile jar, ZipEntry zipFile) {
        final File javaFile = new File(tempFolder, path);

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
    }

    public void compileResources() {

    }
}
