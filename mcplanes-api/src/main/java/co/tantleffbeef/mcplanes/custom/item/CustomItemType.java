package co.tantleffbeef.mcplanes.custom.item;

import co.tantleffbeef.mcplanes.pojo.serialize.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomItemType {
    @NotNull Material baseMaterial();
    @NotNull NamespacedKey id();
    @Nullable NamespacedKey model();
    @NotNull String name();

    /**
     * Called to add additional data to the nbt of an item, like whether it's placeable.
     * @param data the CustomItemNbt that this method should modify. this probably already
     *             contains id, so that doesn't need to be set
     */
    default void addAdditionalNbtItemData(@NotNull CustomItemNbt data) {}
}
