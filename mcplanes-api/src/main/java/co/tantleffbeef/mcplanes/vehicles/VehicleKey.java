package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.KeyManager;

public enum VehicleKey {
    P_51("display_vehicle_p_51")

    ;

    public final String keyName;

    VehicleKey(String keyName) {
        this.keyName = keyName;
    }

    public static void registerKeys(KeyManager<VehicleKey> manager) {
        for (final var k : VehicleKey.values()) {
            manager.registerKey(k.keyName, k);
        }
    }
}
