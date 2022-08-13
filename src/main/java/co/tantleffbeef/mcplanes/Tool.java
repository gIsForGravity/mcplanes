package co.tantleffbeef.mcplanes;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.util.Objects;

public final class Tool {
    private Tool() {
        // STATIC ONLY!!!! NO INSTANCES!!!!
        throw new UnsupportedOperationException();
    }

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
}
