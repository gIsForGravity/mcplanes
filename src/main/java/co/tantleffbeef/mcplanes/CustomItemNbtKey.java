package co.tantleffbeef.mcplanes;

public enum CustomItemNbtKey {
    CUSTOM_BLOCK_DATA("custom_block_data"),
    CUSTOM_ITEM_DATA("custom_item_data"),
    PLACEABLE("Placeable"),
    ID("id"),

    ;

    public final String keyName;

    CustomItemNbtKey(String keyName) {
        this.keyName = keyName;
    }

    public static void registerKeys(KeyManager<CustomItemNbtKey> manager) {
        for (CustomItemNbtKey k : CustomItemNbtKey.values()) {
            manager.registerKey(k.keyName, k);
        }
    }
}
