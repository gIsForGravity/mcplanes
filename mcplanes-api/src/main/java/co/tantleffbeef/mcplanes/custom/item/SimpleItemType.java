package co.tantleffbeef.mcplanes.custom.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleItemType implements CustomItemType {
    private final NamespacedKey id;
    private final Plugin plugin;
    private final boolean customModel;
    private final Material baseItemMaterial;
    private final String name;

    public SimpleItemType(Plugin namespace, String id, boolean customModel, String name) {
        this(namespace, id, customModel, name, Material.STICK);
    }

    public SimpleItemType(Plugin namespace, String id, boolean customModel, String name, Material baseItemMaterial) {
        this.plugin = namespace;
        this.id = new NamespacedKey(namespace, id);
        this.customModel = customModel;
        this.name = name;
        this.baseItemMaterial = baseItemMaterial;
    }

    @Override
    public @NotNull Material baseMaterial() {
        return baseItemMaterial;
    }

    @Override
    public @NotNull NamespacedKey id() {
        return id;
    }

    @Override
    public @Nullable NamespacedKey model() {
        return customModel ? new NamespacedKey(plugin, "item/" + id.getKey()) : null;
    }

    @Override
    public void modifyItemMeta(@NotNull ItemMeta meta) {
        meta.setDisplayName(name);
    }
}
