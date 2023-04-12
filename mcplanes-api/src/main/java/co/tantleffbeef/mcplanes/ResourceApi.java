package co.tantleffbeef.mcplanes;

public interface ResourceApi {
    /**
     * Registers a listener to be called during McPlanes' onEnable function to register custom content
     * @param listener the callback
     */
    void registerInitialBuildListener(Runnable listener);
    BlockManager getBlockManager();
    KeyManager<CustomNbtKey> getNbtKeyManager();
    RecipeManager getRecipeManager();
    ResourceManager getResourceManager();
}
