package co.tantleffbeef.mcplanes.custom.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface InteractableItemType extends CustomItemType {
    void interact(Player player, ItemStack item);
}
