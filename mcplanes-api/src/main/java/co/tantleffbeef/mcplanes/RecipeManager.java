package co.tantleffbeef.mcplanes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

public interface RecipeManager {
    /**
     * Registers the materials as unlocking the recipe
     * @param recipeId The id of the recipe to register for
     * @param materials The materials to unlock with
     */
    void registerUnlockableRecipe(@NotNull NamespacedKey recipeId, @NotNull Material... materials);

    /**
     * Registers the ingredients as unlocking the recipe
     * @param recipeId The id of the recipe to register for
     * @param choices The ingredients to unlock with
     */
    void registerUnlockableRecipe(@NotNull NamespacedKey recipeId, @NotNull RecipeChoice... choices);

    /**
     * Unlocks any custom recipes that have the ingredients in pickedUp
     * @param player the player who picked up the item
     * @param pickedUp the item they picked up
     */
    void unlockRecipes(@NotNull Player player, @NotNull ItemStack pickedUp);
}
