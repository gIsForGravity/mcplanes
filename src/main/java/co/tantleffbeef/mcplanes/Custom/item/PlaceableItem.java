package co.tantleffbeef.mcplanes.Custom.item;

import org.bukkit.entity.ItemDisplay;

public interface PlaceableItem extends CustomItem {
    ItemDisplay.ItemDisplayTransform displayPosition();
}
