package co.tantleffbeef.mcplanes.pojo.serialize;

import co.tantleffbeef.mcplanes.CustomNbtKey;
import co.tantleffbeef.mcplanes.KeyManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CustomBlockNbt {
    public NamespacedKey id;

    public CustomBlockNbt(@NotNull NamespacedKey id) {
        this.id = id;
    }

    public static boolean hasCustomBlockNbt(PersistentDataContainer container, KeyManager<CustomNbtKey> keys) {
        return container.has(keys.keyFor(CustomNbtKey.CUSTOM_BLOCK_DATA), PersistentDataType.TAG_CONTAINER);
    }

    public static @NotNull CustomBlockNbt fromPersistentDataContainer(@NotNull PersistentDataContainer container,
                                                                     @NotNull KeyManager<CustomNbtKey> keys) {
        final var customItemData = container.get(keys.keyFor(CustomNbtKey.CUSTOM_BLOCK_DATA),
                PersistentDataType.TAG_CONTAINER);
        assert customItemData != null;

        final var idString = customItemData.get(keys.keyFor(CustomNbtKey.ID), PersistentDataType.STRING);
        assert idString != null;

        final var id = NamespacedKey.fromString(idString);
        assert id != null;

        return new CustomBlockNbt(id);
    }

    public void saveToPersistentDataContainer(@NotNull PersistentDataContainer rootContainer,
                                              @NotNull KeyManager<CustomNbtKey> keys) {
        // Create container to represent the custom_block_data tag
        final var customItemContainer = rootContainer.getAdapterContext().newPersistentDataContainer();
        // Serialize id to the container
        customItemContainer.set(keys.keyFor(CustomNbtKey.ID), PersistentDataType.STRING, id.toString());

        // Save the custom_block_data container to the root container
        rootContainer.set(keys.keyFor(CustomNbtKey.CUSTOM_BLOCK_DATA), PersistentDataType.TAG_CONTAINER, customItemContainer);
    }
}
