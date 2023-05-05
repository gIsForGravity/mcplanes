package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface PhysicsVehicle {
    /**
     * runs a physics tick on the vehicle
     * @param input the player input of the driver
     * @param deltaTime the time since the last tick (should be 1/20th of a second always but whatever)
     * @return false if the vehicle no longer exists
     */
    boolean tick(@Nullable Input input, float deltaTime);
    @Nullable Player driver();
}
