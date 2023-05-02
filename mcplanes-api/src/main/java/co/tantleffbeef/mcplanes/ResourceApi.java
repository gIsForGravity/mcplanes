package co.tantleffbeef.mcplanes;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface ResourceApi {
    @NotNull BlockManager getBlockManager();
    @NotNull KeyManager<CustomNbtKey> getNbtKeyManager();
    @NotNull RecipeManager getRecipeManager();
    @NotNull ResourceManager getResourceManager();
}
