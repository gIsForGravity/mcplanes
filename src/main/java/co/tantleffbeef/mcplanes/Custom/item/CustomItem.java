package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;

public interface CustomItem {
    Material baseMaterial();
    NamespacedKey id();
    NamespacedKey model();
    String name();
    void setAdditionalItemData(KeyManager<CustomItemNbtKey> keyManager, PersistentDataContainer customitemContainer);
}
