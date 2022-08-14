package co.tantleffbeef.mcplanes.Listeners;

import co.tantleffbeef.mcplanes.Plugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
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

        if (Objects.requireNonNull(itemId).equals("minecraft:gold_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:copper_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:redstone_block") ||
                Objects.requireNonNull(itemId).equals("minecraft:iron_block")) {
            player.discoverRecipe(new NamespacedKey(plugin, "battery"));
        }

        if (Objects.requireNonNull(itemId).equals("minecraft:iron_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:fire_charge") ||
                Objects.requireNonNull(itemId).equals("minecraft:flint_and_steel"))
            player.discoverRecipe(new NamespacedKey(plugin, "blowtorch"));

        if (Objects.requireNonNull(itemId).equals("minecraft:coal") ||
                Objects.requireNonNull(itemId).equals("minecraft:charcoal"))
            player.discoverRecipe(new NamespacedKey(plugin, "crude_oil"));

        if (Objects.requireNonNull(itemId).equals("minecraft:gold_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:iron_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:redstone_block") ||
                Objects.requireNonNull(itemId).equals("minecraft:netherite_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:ancient_debris"))
            player.discoverRecipe(new NamespacedKey(plugin, "engine"));

        if (Objects.requireNonNull(itemId).equals("minecraft:phantom_membrane") ||
                Objects.requireNonNull(itemId).equals("minecraft:iron_ingot")) {
            player.discoverRecipe(new NamespacedKey(plugin, "tail"));
            player.discoverRecipe(new NamespacedKey(plugin, "wing"));
        }

        if (Objects.requireNonNull(itemId).equals("minecraft:iron_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:iron_block"))
            player.discoverRecipe(new NamespacedKey(plugin, "fuselage"));

        if (Objects.requireNonNull(itemId).equals("minecraft:iron_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:gold_ingot") ||
                Objects.requireNonNull(itemId).equals("minecraft:redstone") ||
                Objects.requireNonNull(itemId).equals("minecraft:diamond"))
            player.discoverRecipe(new NamespacedKey(plugin, "powertool"));

        if (Objects.requireNonNull(itemId).equals("minecraft:iron_ingot"))
            player.discoverRecipe(new NamespacedKey(plugin, "wrench"));
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
