package co.tantleffbeef.mcplanes;

import org.jetbrains.annotations.NotNull;

public interface ResourceApi {
    /**
     * Registers a listener to be called during McPlanes' onEnable function to register custom content
     * @param listener the callback
     */
    void registerInitialBuildListener(@NotNull Runnable listener);
    @NotNull BlockManager getBlockManager();
    @NotNull KeyManager<CustomNbtKey> getNbtKeyManager();
    @NotNull RecipeManager getRecipeManager();
    @NotNull ResourceManager getResourceManager();
}
