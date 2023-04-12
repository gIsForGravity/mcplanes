package co.tantleffbeef.mcplanes.custom.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface InteractableItemType extends CustomItemType {
    void interact(@NotNull Player player, @NotNull ItemStack item);
}
