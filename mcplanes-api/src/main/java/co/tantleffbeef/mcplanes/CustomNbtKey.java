package co.tantleffbeef.mcplanes;

public enum CustomNbtKey {
    CUSTOM_BLOCK_DATA("custom_block_data"),
    CUSTOM_ITEM_DATA("custom_item_data"),
    BLOCKS("blocks"),
    LOCATION("location"),
    DATA("data"),
    PLACEABLE("Placeable"),
    ID("id"),

    ;

    public final String keyName;

    CustomNbtKey(String keyName) {
        this.keyName = keyName;
    }

    public static void registerKeys(KeyManager<CustomNbtKey> manager) {
        for (CustomNbtKey k : CustomNbtKey.values()) {
            manager.registerKey(k.keyName, k);
        }
    }
}
