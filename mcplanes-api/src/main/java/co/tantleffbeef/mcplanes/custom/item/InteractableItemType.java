package co.tantleffbeef.mcplanes.custom.item;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InteractableItemType extends CustomItemType {
    /**
     * Called when someone tries to right click with the item
     * @param player the player who interacted
     * @param item the itemstack that is of this item type
     * @param targetBlock the block they are looking at (not actually implemented)
     * @return Whether to cancel the interact event - true = cancel the event
     */
    boolean interact(@NotNull Player player, @NotNull ItemStack item, @Nullable Block targetBlock);
}
