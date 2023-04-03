package co.tantleffbeef.mcplanes;

public enum PluginKey {
    CUSTOM_BLOCK_DATA("custom_block_data"),
    CUSTOM_ITEM_DATA("custom_item_data"),

    ;

    public final String keyName;

    PluginKey(String keyName) {
        this.keyName = keyName;
    }

    public static void registerKeys(KeyManager<PluginKey> manager) {
        for (PluginKey k : PluginKey.values()) {
            manager.registerKey(k.keyName, k);
        }
    }
}
