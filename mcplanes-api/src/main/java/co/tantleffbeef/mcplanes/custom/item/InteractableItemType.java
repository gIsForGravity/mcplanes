package co.tantleffbeef.mcplanes.custom.item;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InteractableItemType extends CustomItemType {
    void interact(@NotNull Player player, @NotNull ItemStack item, @Nullable Block targetBlock);
}
