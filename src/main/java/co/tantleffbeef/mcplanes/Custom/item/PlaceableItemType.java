package co.tantleffbeef.mcplanes.Custom.item;

import co.tantleffbeef.mcplanes.Custom.block.CustomBlockType;
import org.jetbrains.annotations.NotNull;

public interface PlaceableItemType extends CustomItemType {
    @NotNull CustomBlockType asBlock();
}
