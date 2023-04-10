package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.Custom.block.CustomBlockType;
import co.tantleffbeef.mcplanes.serialize.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SimplePlaceableItemType implements PlaceableItemType, CustomBlockType {
    private final NamespacedKey id;
    private final Plugin plugin;
    private final boolean customModel;
    private final String name;

    public SimplePlaceableItemType(Plugin namespace, String id, boolean customModel, String name) {
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
    public @NotNull NamespacedKey id() {
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
    public void addAdditionalNbtItemData(@NotNull CustomItemNbt nbtData) {
        nbtData.placeable = true;
    }

    @Override
    public @NotNull Material blockMaterial() {
        return baseMaterial();
    }

    @Override
    public @NotNull CustomItemType displayItem() {
        return this;
    }

    @Override
    public ItemDisplay.@NotNull ItemDisplayTransform displayTransform() {
        return ItemDisplay.ItemDisplayTransform.HEAD;
    }

    @Override
    public @NotNull CustomBlockType asBlock() {
        return this;
    }
}
