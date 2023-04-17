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
    private final String name;

    public SimpleItemType(Plugin namespace, String id, boolean customModel, String name) {
        this.plugin = namespace;
        this.id = new NamespacedKey(namespace, id);
        this.customModel = customModel;
        this.name = name;
    }

    @Override
    public @NotNull Material baseMaterial() {
        return Material.STICK;
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
