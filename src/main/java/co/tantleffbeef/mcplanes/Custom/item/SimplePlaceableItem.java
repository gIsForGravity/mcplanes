package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import co.tantleffbeef.mcplanes.struct.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public CustomItemNbt addAdditionalNbtItemData(@NotNull CustomItemNbt nbtData) {
        return new CustomItemNbt(nbtData.id(), true);
    }

    @Override
    public ItemDisplay.ItemDisplayTransform displayType() {
        return ItemDisplay.ItemDisplayTransform.HEAD;
    }

    @Override
    public void setAdditionalBlockData(KeyManager<CustomItemNbtKey> keyManager, PersistentDataContainer customitemContainer) {

    }
}
