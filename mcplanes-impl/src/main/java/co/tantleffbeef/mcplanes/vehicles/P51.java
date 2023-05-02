package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.RigidDisplay;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class P51 implements PhysicsVehicle {
    private final Rigidbody rb;

    public P51(Display entity) {
        final var xPos = entity.getLocation().getX();
        final var yPos = entity.getLocation().getY();
        final var zPos = entity.getLocation().getZ();
        final var box = new BoundingBox(xPos, yPos, zPos, xPos, yPos, zPos);
        box.expand(5.0);
        this.rb = new Rigidbody(new RigidDisplay(entity), new Collider(box, new Vector3f((float) xPos, (float) yPos, (float) zPos), entity.getWorld()), 1.0f);
    }

    @Override
    public void tick(@Nullable Input input, float deltaTime) {
        rb.pretick();



        rb.tick(deltaTime);
    }

    @Override
    public @Nullable Player driver() {
        return null;
    }
}
