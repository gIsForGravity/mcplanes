package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;

public interface PlaceableItem extends CustomItem {
    ItemDisplay.ItemDisplayTransform displayType();
    void setAdditionalBlockData(KeyManager<CustomItemNbtKey> keyManager, PersistentDataContainer customitemContainer);
}
