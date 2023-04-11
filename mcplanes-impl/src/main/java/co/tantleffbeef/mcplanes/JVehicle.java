package co.tantleffbeef.mcplanes;

import org.bukkit.entity.Entity;

import java.util.UUID;

public interface JVehicle {
    void tick();
    UUID getUUID();
}
