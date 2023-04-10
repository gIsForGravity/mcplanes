package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.Custom.block.CustomBlock;
import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public interface PlaceableItem extends CustomItem {
    @NotNull CustomBlock asBlock();
}
