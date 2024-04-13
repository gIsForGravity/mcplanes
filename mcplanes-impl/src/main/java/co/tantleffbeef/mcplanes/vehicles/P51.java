package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.RigidDisplay;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.QuaternionfInterpolator;
import org.joml.Vector3f;

public class P51 implements PhysicsVehicle {
    public final Rigidbody rb;
    public final ArmorStand entity;
    private final Display model;

    /**
     * *Precondition: location has a world*
     * @return a brand new p51!!!!!
     */
    public static P51 spawn(@NotNull PluginManager pluginManager, @NotNull Location location, ItemStack displayItem) {
        assert location.getWorld() != null;

        final var world = location.getWorld();
        final var armorStand = world.spawn(location, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setGravity(false);
            stand.addScoreboardTag("mcplanes_plane");
            stand.addScoreboardTag("mcplanes_p51");

            world.spawn(location, ItemDisplay.class, display -> {
                display.setItemStack(displayItem);
                stand.addPassenger(display);
                display.addScoreboardTag("mcplanes_plane");
                display.addScoreboardTag("mcplanes_p51");
                final var transformation = display.getTransformation();
                transformation.getScale().set(5f, 5f, 5f);
                display.setTransformation(transformation);
            });
        });

        // TODO: refactor this and make it not bad
        return new P51(pluginManager, armorStand, (Display) armorStand.getPassengers().get(0));
    }

    public P51(PluginManager pluginManager, ArmorStand stand, Display model) {
        final var xPos = stand.getLocation().getX();
        final var yPos = stand.getLocation().getY();
        final var zPos = stand.getLocation().getZ();
        final var box = new BoundingBox(xPos, yPos, zPos, xPos, yPos, zPos);
        box.expand(2.0);
        this.rb = new Rigidbody(pluginManager, new RigidDisplay(stand, model), new Collider(box, new Vector3f((float) xPos, (float) yPos, (float) zPos), model.getWorld()), 1.0f);
        this.entity = stand;
        this.model = model;
    }

    private int tick = 0;
    private final float MAX_VELOCITY_SQUARED = 100;
    private final float THRUST_FORCE = 1;

    @Override
    public boolean tick(@Nullable Input input, float deltaTime) {
        // If the entity has been killed, then destroy all of the objects
        /*if (entity.isDead()) {
            entity.remove();
            model.remove();

            return false;
        }*/

        rb.pretick();

        Quaternionf rotation = rb.currentRotation();

        Vector3f forward = rb.forward();
        Vector3f up = rb.up();
        Vector3f right = rb.right();

        if (rb.velocity().lengthSquared() < MAX_VELOCITY_SQUARED)
             // im assuming this is normalized already
            rb.addForce(rb.forward().mul(THRUST_FORCE));


        if (input != null) {

            if (input.forward() > 0.1f)
                rotation.rotateAxis(-0.1f, right);

            else if (input.forward() < -0.1f)
                rotation.rotateAxis(0.1f, right);

            if (input.right() > 0.1f)
                rotation.rotateAxis(0.1f, forward);

            else if (input.right() < -0.1f)
                rotation.rotateAxis(-0.1f, forward);

        }
//        if (tick < 100) {
//            rb.addForce(new Vector3f(0, 0, 1));
//            tick++;
//        }


        rb.tick(deltaTime);

        return true;
    }

    @Override
    public @Nullable Player driver() {
        return null;
    }
}
