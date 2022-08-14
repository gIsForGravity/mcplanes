package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import co.tantleffbeef.mcplanes.RecipeBookTools;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class RecipeEventListeners extends AbstractListener {
    public RecipeEventListeners(Plugin plugin) {
        super(plugin);
    }

    private final NamespacedKey customItemKey = new NamespacedKey(plugin, "customItem");

    private void unlockRecipes(Player player, ItemStack itemStack) {

        final var itemData = Objects.requireNonNull(itemStack.getItemMeta())
                .getPersistentDataContainer();

        String itemId;

        if (itemData.has(customItemKey, PersistentDataType.STRING)) {
            itemId = itemData.get(customItemKey, PersistentDataType.STRING);
        } else {
            itemId = itemStack.getType().getKey().toString();
        }

        player.sendMessage("itemId: ", itemId);

        if (Objects.requireNonNull(itemId).equals("minecraft:gold_ingot")) {
            player.discoverRecipe(new NamespacedKey(plugin, "battery"));
            player.discoverRecipe(new NamespacedKey(plugin, "engine"));
        }

        switch (Objects.requireNonNull(itemId)) {
            default:
                return;
            case "minecraft:gold_ingot":
                player.discoverRecipe(new NamespacedKey(plugin, "battery"));
                player.discoverRecipe(new NamespacedKey(plugin, "engine"));
                break;
            case "minecraft:iron_ingot":
                player.discoverRecipe(new NamespacedKey(plugin, "blowtorch"));

                break;
            case "minecraft:copper_ingot":
            case "minecraft:redstone_block":
            case "minecraft:iron_block":
                player.discoverRecipe(new NamespacedKey(plugin, "battery"));
                break;
            case "minecraft:fire_charge":
            case "minecraft:flint_and_steel":
                player.discoverRecipe(new NamespacedKey(plugin, "blowtorch"));
                break;
            case "minecraft:coal":
            case "minecraft:charcoal":
                player.discoverRecipe(new NamespacedKey(plugin, "crude_oil"));
                break;
        }
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        // return if this isn't a player picking up the item
        if (!(event.getEntity() instanceof final Player player))
            return;

        final var itemStack = event.getItem().getItemStack();
        // if the item unlocks any recipes than go do that
        unlockRecipes(player, itemStack);
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        // return if this isn't entering a player's inventory
        if (!(event.getDestination().getHolder() instanceof final Player player))
            return;

        // if its moving from your inventory to your inventory then no need to check
        if (event.getDestination().equals(event.getSource()))
            return;

        final var itemStack = event.getItem();
        // if the item unlocks any recipes than go do that
        unlockRecipes(player, itemStack);
    }
}
