package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface PhysicsVehicle {
    void tick(@Nullable Input input, float deltaTime);
    @Nullable Player driver();
}
