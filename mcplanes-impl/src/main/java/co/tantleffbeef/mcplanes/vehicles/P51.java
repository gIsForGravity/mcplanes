package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.RigidDisplay;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
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
        this.rb = new Rigidbody(pluginManager, new RigidDisplay(stand, model), new Collider(box, new Vector3f((float) xPos, (float) yPos, (float) zPos),
                                model.getWorld()), 1.0f, 0.5f, 0.1f);
        this.entity = stand;
        this.model = model;
    }

    private int tick = 0;
    private final float MAX_VELOCITY_SQUARED = 100;
    private final float THRUST_FORCE = 1;
    private final float AIR_DENSITY = 1.225f;
    private final float WING_AREA = 3;
    private final float CONTROL_SURFACE_AREA = 0.2f;
    private final float STABILIZER_AREA = 0.8f;
    private final float CONTROL_SURFACE_DEFLECT = (float)Math.PI/6;
    private float throttle = 0f; // normally start at 0 but 1 for testing

    @Override
    public boolean tick(@Nullable Input input, float deltaTime) {
        // If the entity has been killed, then destroy all of the objects
        /*if (entity.isDead()) {
            entity.remove();
            model.remove();

            return false;
        }*/

        rb.pretick();

        Bukkit.broadcastMessage("throttle: " + throttle + " position: " + rb.getLocation().toString() + " vel: " + rb.velocity().toString());

//        Quaternionf rotation = rb.currentRotation();
//        Vector3f location = rb.getLocation();

//        Vector3f forward = rb.forward();
//        Vector3f up = rb.up();
//        Vector3f right = rb.right();

        // throttle
        if (rb.velocity().lengthSquared() < MAX_VELOCITY_SQUARED)
            rb.addForce(rb.forward().mul(THRUST_FORCE * throttle).mul(deltaTime));

        // lift
        // this could be done per surface but rn im doing it for all of them
        rb.addForce(rb.up().mul(getAeroForce(AeroSurfaceType.WING, deltaTime)));
        // i could do lift forces on stabilizers other than vertical but im not going to
        rb.addForceAtPosition(rb.right().mul(getAeroForce(AeroSurfaceType.VERTICAL_STABILIZER, deltaTime)),
                              rb.getLocation().add(rb.forward().mul(-2)));

        // controls
        if (input != null) {

            // in the future these will apply a torque that is in some way proportional to airspeed

            if (input.forward() > 0.1f) // rotation.rotateAxis(-0.1f, right);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.forward().mul(-2))); // up force back

            else if (input.forward() < -0.1f) // rotation.rotateAxis(0.1f, right);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.forward().mul(-2))); // down force back

            if (input.right() > 0.1f) { // rotation.rotateAxis(0.1f, forward);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.right().mul(2))); // down force right
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.right().mul(-2))); // up force left

            } else if (input.right() < -0.1f) { // rotation.rotateAxis(-0.1f, forward);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.right().mul(-2))); // down force left
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.getLocation().add(rb.right().mul(2))); // up force right
            }

            if (input.jump() && throttle < 1) // probably have to cancel leave event but then how do you leave
                throttle += 0.05f;

//            if (input.crouch() && throttle < 1) // probably have to cancel leave event but then how do you leave
//                throttle += 0.05f; // idk how you would throttle down (maybe something like this should be a hotbar thing)

        }
//        if (tick < 100) {
//            rb.addForce(new Vector3f(0, 0, 1));
//            tick++;
//        }

        rb.tick(deltaTime);

        return true;
    }

    private enum AeroSurfaceType {
        CONTROL_SURFACE_UP,
        CONTROL_SURFACE_DOWN,
        WING,
        VERTICAL_STABILIZER
    }

    private float getAeroForce(AeroSurfaceType type, float deltaTime) {

        float defaultAoA = rb.forward().angleSigned(rb.velocity(), rb.right());
        float speedSquared = rb.velocity().lengthSquared();

        return deltaTime * AIR_DENSITY * speedSquared * (float)Math.PI * switch (type) {
            case WING -> WING_AREA * defaultAoA;

            case CONTROL_SURFACE_UP -> CONTROL_SURFACE_AREA * (defaultAoA + CONTROL_SURFACE_DEFLECT);

            case CONTROL_SURFACE_DOWN -> CONTROL_SURFACE_AREA * (defaultAoA - CONTROL_SURFACE_DEFLECT);

            // might have to do tangential velocity which i dont want to do
            case VERTICAL_STABILIZER -> STABILIZER_AREA * (rb.forward().angleSigned(rb.velocity(), rb.up()));
        };
    }

    @Override
    public @Nullable Player driver() {
        return null;
    }
}
