package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

public class SimpleItem implements CustomItem {
    private final NamespacedKey id;
    private final Plugin plugin;
    private final boolean customModel;
    private final String name;

    public SimpleItem(Plugin namespace, String id, boolean customModel, String name) {
        this.plugin = namespace;
        this.id = new NamespacedKey(namespace, id);
        this.customModel = customModel;
        this.name = name;
    }

    @Override
    public Material baseMaterial() {
        return Material.STICK;
    }

    @Override
    public NamespacedKey id() {
        return id;
    }

    @Override
    public NamespacedKey model() {
        return customModel ? new NamespacedKey(plugin, "item/" + id.getKey()) : null;
    }

    @Override
    public void setAdditionalItemData(KeyManager<CustomItemNbtKey> keyManager, PersistentDataContainer customitemContainer) {
        return;
    }

    @Override
    public String name() {
        return name;
    }
}
