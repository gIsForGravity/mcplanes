package co.tantleffbeef.mcplanes;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class ResourceManager {
    private final Plugin plugin;
    private File tempFolder;
    private File webserverFolder;

    public ResourceManager(Plugin plugin) {
        this.plugin = plugin;
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

    private static void clearFolder(File folder) {
        if (folder.isFile())
            return;

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            // if f is a subdirectory we need to clear it first too
            if (f.isDirectory())
                clearFolder(f);
            f.delete();
        }
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
