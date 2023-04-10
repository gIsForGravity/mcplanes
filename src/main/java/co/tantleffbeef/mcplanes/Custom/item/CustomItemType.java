package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.serialize.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public interface CustomItemType {
    Material baseMaterial();
    NamespacedKey id();
    NamespacedKey model();
    String name();

    /**
     * Called to add additional data to the nbt of an item, like whether it's placeable.
     * @param data the CustomItemNbt that this method should modify. this probably already
     *             contains id, so that doesn't need to be set
     */
    default void addAdditionalNbtItemData(@NotNull CustomItemNbt data) {}
}
