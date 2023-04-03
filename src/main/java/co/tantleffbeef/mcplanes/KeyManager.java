package co.tantleffbeef.mcplanes;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KeyManager<T> {
    private final Plugin namespacePlugin;
    private final Map<T, NamespacedKey> keyMap;

    public KeyManager(Plugin namespace) {
        namespacePlugin = namespace;
        keyMap = new HashMap<>();
    }

    /**
     * Gets the namespaced key associated with t
     * @param t the key for the key lol
     */
    public @NotNull NamespacedKey keyFor(T t) {
        final var key = keyMap.get(t);

        assert key != null;

        return key;
    }

    public void registerKey(String key, T For) {
        assert !keyMap.containsKey(For);

        final var newKey = new NamespacedKey(namespacePlugin, key);

        assert !keyMap.containsValue(newKey);

        keyMap.put(For, newKey);
    }
}

