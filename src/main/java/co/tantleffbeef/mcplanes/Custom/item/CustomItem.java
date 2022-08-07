package co.tantleffbeef.mcplanes.Custom.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public interface CustomItem {
    Material baseMaterial();
    NamespacedKey id();
    NamespacedKey model();
}
