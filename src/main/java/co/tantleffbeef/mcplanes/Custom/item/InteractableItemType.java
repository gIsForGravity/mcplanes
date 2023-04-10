package co.tantleffbeef.mcplanes.Custom.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface InteractableItemType extends CustomItemType {
    void interact(Player player, ItemStack item);
}
