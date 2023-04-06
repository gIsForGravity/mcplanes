package co.tantleffbeef.mcplanes.struct;

import co.tantleffbeef.mcplanes.CustomItemNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public record CustomItemNbt(NamespacedKey id, boolean placeable) {
    public static boolean hasCustomItemNbt(PersistentDataContainer container, KeyManager<CustomItemNbtKey> keys) {
        return container.has(keys.keyFor(CustomItemNbtKey.CUSTOM_ITEM_DATA), PersistentDataType.TAG_CONTAINER);
    }
    public static @NotNull CustomItemNbt fromPersistentDataContainer(@NotNull PersistentDataContainer container,
                                                                     @NotNull KeyManager<CustomItemNbtKey> keys) {
        final var customItemData = container.get(keys.keyFor(CustomItemNbtKey.CUSTOM_ITEM_DATA),
                PersistentDataType.TAG_CONTAINER);
        assert customItemData != null;

        final var idString = container.get(keys.keyFor(CustomItemNbtKey.ID), PersistentDataType.STRING);
        assert idString != null;

        final var id = NamespacedKey.fromString(idString);

        final var placeableByte = container.get(keys.keyFor(CustomItemNbtKey.PLACEABLE), PersistentDataType.BYTE);
        assert placeableByte != null && (placeableByte.equals((byte) 1) || placeableByte.equals((byte) 0));

        final var placeable = placeableByte.equals((byte) 1);

        return new CustomItemNbt(id, placeable);
    }

    public void saveToPersistentDataContainer(@NotNull PersistentDataContainer rootContainer,
                                              @NotNull KeyManager<CustomItemNbtKey> keys) {
        // Create container to represent the custom_item_data
        final var customItemContainer = rootContainer.getAdapterContext().newPersistentDataContainer();
        // Serialize id to the container
        customItemContainer.set(keys.keyFor(CustomItemNbtKey.ID), PersistentDataType.STRING, id.toString());
        // Serialize placeable to the container
        if (placeable)
            customItemContainer.set(keys.keyFor(CustomItemNbtKey.PLACEABLE), PersistentDataType.BYTE, (byte) 1);
        else
            customItemContainer.set(keys.keyFor(CustomItemNbtKey.PLACEABLE), PersistentDataType.BYTE, (byte) 0);

        // Save the custom_item_data container to the root container
        rootContainer.set(keys.keyFor(CustomItemNbtKey.CUSTOM_ITEM_DATA), PersistentDataType.TAG_CONTAINER, customItemContainer);
    }

}
