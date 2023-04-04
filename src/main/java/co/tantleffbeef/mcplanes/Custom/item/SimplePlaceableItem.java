package co.tantleffbeef.mcplanes.Custom.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.plugin.Plugin;

public class SimplePlaceableItem implements PlaceableItem {
    private final NamespacedKey id;
    private final Plugin plugin;
    private final boolean customModel;
    private final String name;

    public SimplePlaceableItem(Plugin namespace, String id, boolean customModel, String name) {
        this.plugin = namespace;
        this.id = new NamespacedKey(namespace, id);
        this.customModel = customModel;
        this.name = name;
    }

    @Override
    public Material baseMaterial() {
        return Material.BARRIER;
    }

    @Override
    public NamespacedKey id() {
        return id;
    }

    @Override
    public NamespacedKey model() {
        return customModel ? new NamespacedKey(plugin, "block/" + id.getKey()) : null;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ItemDisplay.ItemDisplayTransform displayPosition() {
        return ItemDisplay.ItemDisplayTransform.HEAD;
    }
}
