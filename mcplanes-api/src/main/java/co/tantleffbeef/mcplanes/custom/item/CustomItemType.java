package co.tantleffbeef.mcplanes.custom.item;

import co.tantleffbeef.mcplanes.CustomNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import co.tantleffbeef.mcplanes.ResourceManager;
import co.tantleffbeef.mcplanes.pojo.serialize.CustomItemNbt;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomItemType {
    @NotNull Material baseMaterial();
    @NotNull NamespacedKey id();
    @Nullable NamespacedKey model();
    void modifyItemMeta(@NotNull ItemMeta meta);

    /**
     * Called to add additional data to the nbt of an item, like whether it's placeable.
     * @param data the CustomItemNbt that this method should modify. this probably already
     *             contains id, so that doesn't need to be set
     */
    default void addAdditionalNbtItemData(@NotNull CustomItemNbt data) {}

    static <T extends CustomItemType> @Nullable T asInstanceOf(Class<T> type, ItemStack itemStack,
                                                                      KeyManager<CustomNbtKey> keyManager, ResourceManager resourceManager) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return null;

        final var data = meta.getPersistentDataContainer();

        if (!CustomItemNbt.hasCustomItemNbt(data, keyManager))
            return null;

        final var itemNbt = CustomItemNbt.fromPersistentDataContainer(data, keyManager);
        final var itemType = resourceManager.getCustomItemType(itemNbt.id);


        if (!(type.isInstance(itemType)))
            return null;

        return (T) itemType;

    }
}
