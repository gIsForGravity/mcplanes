package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class EntityPickupItemListener extends AbstractListener {
    public EntityPickupItemListener(Plugin plugin) {
        super(plugin);
    }

    private final NamespacedKey customItemKey = new NamespacedKey(plugin, "customItem");

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        Bukkit.broadcastMessage("onInventoryPickupItemEvent()");
        if (!(event.getEntity() instanceof final Player player))
            return;

        player.sendMessage("onInventoryPickupItemEvent()");

        final var itemStack = event.getItem().getItemStack();
        final var itemData = Objects.requireNonNull(itemStack.getItemMeta())
                .getPersistentDataContainer();

        String itemId;

        if (itemData.has(customItemKey, PersistentDataType.STRING)) {
            itemId = itemData.get(customItemKey, PersistentDataType.STRING);
        } else {
            itemId = itemStack.getType().getKey().toString();
        }

        player.sendMessage("itemId: ", itemId);
        switch (Objects.requireNonNull(itemId)) {
            default:
                return;
            case "minecraft:copper_ingot":
            case "minecraft:gold_ingot":
            case "minecraft:redstone_block":
                player.discoverRecipe(new NamespacedKey(plugin, "battery"));
                break;
        }
    }
}
