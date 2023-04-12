package co.tantleffbeef.mcplanes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MCPRecipeManager implements RecipeManager {
    // I guess we don't need to keep the plugin around???
    //private final Plugin plugin;
    // List of ingredients to be checked first before indexing the second list
    private final List<RecipeIngredient> ingredientList = new ArrayList<>();
    // List of recipes and their composite parts (the namespaced key is the recipe id)
    private final Map<NamespacedKey, List<RecipeIngredient>> recipes = new HashMap<>();
    private final NamespacedKey customItemKey;

    public MCPRecipeManager(Plugin plugin) {
        //this.plugin = plugin;

        customItemKey = new NamespacedKey(plugin, "customItem");
    }

    private static class RecipeIngredient {
        public RecipeIngredient(Material material) {
            ingredientType = IngredientType.MATERIAL;
            this.material = material;
            this.recipeChoice = null;
        }

        public RecipeIngredient(RecipeChoice recipeChoice) {
            ingredientType = IngredientType.RECIPE_CHOICE;
            this.material = null;
            this.recipeChoice = recipeChoice;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RecipeIngredient that = (RecipeIngredient) o;
            return ingredientType == that.ingredientType && material == that.material && Objects.equals(recipeChoice, that.recipeChoice);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ingredientType, material, recipeChoice);
        }

        public enum IngredientType {
            MATERIAL,
            RECIPE_CHOICE,
        }

        public final IngredientType ingredientType;
        public final Material material;
        public final RecipeChoice recipeChoice;
    }

    /**
     * Registers the materials as unlocking the recipe
     * @param recipeId The id of the recipe to register for
     * @param materials The materials to unlock with
     */
    public void registerUnlockableRecipe(@NotNull NamespacedKey recipeId, @NotNull Material @NotNull ... materials) {
        checkList(recipeId);

        List<RecipeIngredient> ingredients = recipes.get(recipeId);

        for (Material mat: materials) {
            // add each ingredient to the list of all ingredients and the list of ingredients for this recipe

            var ingredient = new RecipeIngredient(mat);
            if (!ingredients.contains(ingredient)) ingredients.add(ingredient);
            if (!ingredientList.contains(ingredient)) ingredientList.add(ingredient);
        }
    }

    /**
     * Registers the ingredients as unlocking the recipe
     * @param recipeId The id of the recipe to register for
     * @param choices The ingredients to unlock with
     */
    public void registerUnlockableRecipe(@NotNull NamespacedKey recipeId, RecipeChoice... choices) {
        checkList(recipeId);

        List<RecipeIngredient> ingredients = recipes.get(recipeId);

        for (RecipeChoice choice: choices) {
            // add each ingredient to the list of all ingredients and the list of ingredients for recipe

            var ingredient = new RecipeIngredient(choice);
            if (!ingredients.contains(ingredient)) ingredients.add(ingredient);
            if (!ingredientList.contains(ingredient)) ingredientList.add(ingredient);
        }
    }

    /**
     * Unlocks any custom recipes that have the ingredients in pickedUp
     * @param player the player who picked up the item
     * @param pickedUp the item they picked up
     */
    public void unlockRecipes(@NotNull Player player, @NotNull ItemStack pickedUp) {
        // get persistent data of item to check if its a custom item
        var itemData = Objects.requireNonNull(pickedUp.getItemMeta()).getPersistentDataContainer();

        RecipeIngredient ingredientFound;

        // set ingredient to either a custom item or just a spigot material
        if (itemData.has(customItemKey, PersistentDataType.STRING)) {
            var choice = new RecipeChoice.ExactChoice(pickedUp);
            ingredientFound = new RecipeIngredient(choice);
            //itemId = itemData.get(customItemKey, PersistentDataType.STRING);
        } else {
            ingredientFound = new RecipeIngredient(pickedUp.getType());
            //itemId = itemStack.getType().getKey().toString();
        }

        // whether we found the item in our list of custom recipes
        boolean found = false;

        // iterate through list of recipe items and if we don't find picked up item then stop
        for (var ingredient : ingredientList) {
            if (ingredientFound.equals(ingredient)) {
                found = true;
                break;
            }
        }

        // if item not found then just leave
        if (!found)
            return;

        // Loop through every recipe and every item in every recipe and unlock applicable ones
        var recipesSet = recipes.keySet();
        for (NamespacedKey recipeId : recipesSet) {
            // loop through all ingredients
            for (RecipeIngredient i : recipes.get(recipeId)) {
                if (ingredientFound.equals(i)) {
                    // if we find the ingredient in this recipe then give recipe to player and move
                    // on to next one
                    player.discoverRecipe(recipeId);
                    break;
                }
            }
        }

        // all done!
    }

    /**
     * Check if a list has been made for said recipe and if not then make one
     * @param recipeId id of said recipe
     */
    private void checkList(NamespacedKey recipeId) {
        if (recipes.containsKey(recipeId))
            return;

        var list = recipes.get(recipeId);
        if (list != null)
            return;

        recipes.put(recipeId, new ArrayList<>());
    }
}
