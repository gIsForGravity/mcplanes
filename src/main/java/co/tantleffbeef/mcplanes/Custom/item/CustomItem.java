package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.struct.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface CustomItem {
    Material baseMaterial();
    NamespacedKey id();
    NamespacedKey model();
    String name();

    /**
     * Called to add additional data to the nbt of an item, like whether it's placeable.
     * @param data the CustomItemNbt that this method should modify. this probably already
     *             contains id, so that doesn't need to be set
     * @return the modified nbt data
     */
    default @NotNull CustomItemNbt addAdditionalNbtItemData(@NotNull CustomItemNbt data) {
        return data;
    }
}
