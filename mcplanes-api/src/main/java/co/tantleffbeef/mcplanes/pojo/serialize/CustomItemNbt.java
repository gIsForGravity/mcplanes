package co.tantleffbeef.mcplanes.pojo.serialize;

import co.tantleffbeef.mcplanes.CustomNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CustomItemNbt {
    public NamespacedKey id;
    public boolean placeable;
    /*public CustomItemNbt(NamespacedKey id) {
        this(id, false);
    }*/
    public CustomItemNbt(NamespacedKey id, boolean placeable) {
        this.id = id;
        this.placeable = placeable;
    }

    public static boolean hasCustomItemNbt(PersistentDataContainer container, KeyManager<CustomNbtKey> keys) {
        return container.has(keys.keyFor(CustomNbtKey.CUSTOM_ITEM_DATA), PersistentDataType.TAG_CONTAINER);
    }
    public static @NotNull CustomItemNbt fromPersistentDataContainer(@NotNull PersistentDataContainer container,
                                                                     @NotNull KeyManager<CustomNbtKey> keys) {
        final var customItemData = container.get(keys.keyFor(CustomNbtKey.CUSTOM_ITEM_DATA),
                PersistentDataType.TAG_CONTAINER);
        assert customItemData != null;

        final var idString = customItemData.get(keys.keyFor(CustomNbtKey.ID), PersistentDataType.STRING);
        assert idString != null;

        final var id = NamespacedKey.fromString(idString);

        final var placeableByte = customItemData.get(keys.keyFor(CustomNbtKey.PLACEABLE), PersistentDataType.BYTE);
        assert placeableByte != null && (placeableByte.equals((byte) 1) || placeableByte.equals((byte) 0));

        final var placeable = placeableByte.equals((byte) 1);

        return new CustomItemNbt(id, placeable);
    }

    public void saveToPersistentDataContainer(@NotNull PersistentDataContainer rootContainer,
                                              @NotNull KeyManager<CustomNbtKey> keys) {
        // Create container to represent the custom_item_data
        final var customItemContainer = rootContainer.getAdapterContext().newPersistentDataContainer();
        // Serialize id to the container
        customItemContainer.set(keys.keyFor(CustomNbtKey.ID), PersistentDataType.STRING, id.toString());
        // Serialize placeable to the container
        if (placeable)
            customItemContainer.set(keys.keyFor(CustomNbtKey.PLACEABLE), PersistentDataType.BYTE, (byte) 1);
        else
            customItemContainer.set(keys.keyFor(CustomNbtKey.PLACEABLE), PersistentDataType.BYTE, (byte) 0);

        // Save the custom_item_data container to the root container
        rootContainer.set(keys.keyFor(CustomNbtKey.CUSTOM_ITEM_DATA), PersistentDataType.TAG_CONTAINER, customItemContainer);
    }

}
