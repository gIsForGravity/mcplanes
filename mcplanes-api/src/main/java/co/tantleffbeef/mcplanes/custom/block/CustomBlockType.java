package co.tantleffbeef.mcplanes.custom.block;

import co.tantleffbeef.mcplanes.custom.item.CustomItemType;
import co.tantleffbeef.mcplanes.pojo.serialize.CustomBlockNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public interface CustomBlockType {
    @NotNull Material blockMaterial();
    @NotNull CustomItemType displayItem();
    @NotNull NamespacedKey id();
    @NotNull ItemDisplay.ItemDisplayTransform displayTransform();
    default void addAdditionalNbtBlockData(@NotNull CustomBlockNbt nbt) {}
}
