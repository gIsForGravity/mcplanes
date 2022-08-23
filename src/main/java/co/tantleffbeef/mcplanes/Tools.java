package co.tantleffbeef.mcplanes;

import org.bukkit.Bukkit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public final class Tools {
    private Tools() {
        // STATIC ONLY!!!! NO INSTANCES!!!!
        throw new UnsupportedOperationException();
    }

    public static final int FILE_BUFFER_SIZE = 8192;

    public static void clearFolder(File folder) {
        if (folder.isFile())
            return;

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            // if f is a subdirectory we need to clear it first too
            if (f.isDirectory())
                clearFolder(f);
            f.delete();
        }
    }

    /**
     * Gets the plugin with said namespace
     * @param namespace namespace of plugin
     * @return Plugin if exists and loaded, otherwise null
     */
    public static org.bukkit.plugin.Plugin getNamespacePlugin(String namespace) {
        var plugins = Bukkit.getPluginManager().getPlugins();

        for (org.bukkit.plugin.Plugin plugin : plugins) {
            if (plugin.getName().toLowerCase().strip().equals(namespace))
                return plugin;
        }

        return null;
    }

    public static byte[] createSha1(File file) {
        try {
            // will create a sha-1 hash
            MessageDigest digest = MessageDigest.getInstance("SHA-1");

            // read file and send it into digest to be hashed
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) > 0) {
                    digest.update(buffer, 0, n);
                }
            }

            // finally hash now that whole file is loaded
            return digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
