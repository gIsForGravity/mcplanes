package co.tantleffbeef.mcplanes.custom.item;

import co.tantleffbeef.mcplanes.custom.block.CustomBlockType;
import org.jetbrains.annotations.NotNull;

public interface PlaceableItemType extends CustomItemType {
    @NotNull CustomBlockType asBlock();
}
