package co.tantleffbeef.mcplanes.Custom.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

public class SimpleItem implements CustomItem {
    private final NamespacedKey id;
    private final Plugin plugin;

    public SimpleItem(Plugin namespace, String id) {
        this.plugin = namespace;
        this.id = new NamespacedKey(namespace, id);
    }

    @Override
    public Material baseMaterial() {
        return Material.STICK;
    }

    @Override
    public NamespacedKey id() {
        return id;
    }

    @Override
    public NamespacedKey model() {
        return new NamespacedKey(plugin, "item/" + id.getKey());
    }
}
