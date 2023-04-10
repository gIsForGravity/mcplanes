package co.tantleffbeef.mcplanes.Custom.block;

import co.tantleffbeef.mcplanes.Custom.item.CustomItem;
import co.tantleffbeef.mcplanes.serialize.CustomBlockNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public interface CustomBlock {
    @NotNull Material blockMaterial();
    @NotNull CustomItem displayItem();
    @NotNull NamespacedKey id();
    @NotNull ItemDisplay.ItemDisplayTransform displayTransform();
    default void addAdditionalNbtBlockData(@NotNull CustomBlockNbt nbt) {}
}
