package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.physics.Transform;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class P51Controller implements PhysicVehicleController {
    public final Rigidbody rb;

    /**
     * *Precondition: location has a world*
     * @return a brand new p51!!!!!
     */
    public static PhysicVehicle spawn(@NotNull Location location, ItemStack displayItem) {
        assert location.getWorld() != null;

        final var world = location.getWorld();
        final var displayModel = world.spawn(location, ItemDisplay.class, vehicle -> {
            vehicle.setGravity(false);
            vehicle.addScoreboardTag("mcplanes_plane");
            vehicle.addScoreboardTag("mcplanes_p51");

            final var transformation = vehicle.getTransformation();
            transformation.getScale().set(5f, 5f, 5f);
            vehicle.setTransformation(transformation);
            vehicle.setItemStack(displayItem);
        });

        // TODO: refactor this and make it not bad
        return new PhysicVehicle(new P51Controller(displayModel.getLocation()), displayModel);
    }

    public P51Controller(Location location) {
        final var xPos = (float) location.getX();
        final var yPos = (float) location.getY();
        final var zPos = (float) location.getZ();
        final var box = new BoundingBox(xPos, yPos, zPos, xPos, yPos, zPos);
        box.expand(2.0);
        this.rb = new Rigidbody(new Transform(new Vector3f(xPos, yPos, zPos)), new Collider(box, new Vector3f(xPos, yPos, zPos),
                location.getWorld()), 1.0f, 0.5f, 0.1f);
    }

    private int tick = 0;
    private static final float MAX_VELOCITY_SQUARED = 100;
    private static final float THRUST_FORCE = 1;
    private static final float AIR_DENSITY = 1.225f;
    private static final float WING_AREA = 3;
    private static final float CONTROL_SURFACE_AREA = 0.2f;
    private static final float STABILIZER_AREA = 0.8f;
    private static final float CONTROL_SURFACE_DEFLECT = (float)Math.PI/6;
    private float throttle = 0f; // normally start at 0 but 1 for testing

    private float timer = 0;

    @Override
    public boolean tick(float deltaTime, @Nullable Input input, @NotNull Entity vehicle) {
        // If the entity has been killed, then destroy all of the objects
        /*if (entity.isDead()) {
            entity.remove();
            model.remove();

            return false;
        }*/

        rb.pretick();

//        Bukkit.broadcastMessage("aero forces:");
//        Bukkit.broadcastMessage("force down" + getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime));
//        Bukkit.broadcastMessage("force down" + getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime));
//        Bukkit.broadcastMessage("force down" + getAeroForce(AeroSurfaceType.WING, deltaTime));
//        Bukkit.broadcastMessage("force down" + getAeroForce(AeroSurfaceType.VERTICAL_STABILIZER, deltaTime));
        getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime);

//        if (timer < 1) {
//            Bukkit.broadcastMessage("throttle: " + throttle + " position: " + rb.getLocation().toString() + " vel: " + rb.velocity().toString() + " dt: " + deltaTime);
//            Bukkit.broadcastMessage(ChatColor.GOLD + "forward: " + rb.forward().toString() + " right: " + rb.right().toString() + " up: " + rb.up().toString());
//            Bukkit.broadcastMessage(ChatColor.AQUA + "rotation: " + rb.currentRotation().toString());
//            // results in nans up the wazoo
//            timer += deltaTime;
//        }



//        Quaternionf rotation = rb.currentRotation();
//        Vector3f location = rb.getLocation();

//        Vector3f forward = rb.forward();
//        Vector3f up = rb.up();
//        Vector3f right = rb.right();

        // throttle
        if (rb.velocity.lengthSquared() < MAX_VELOCITY_SQUARED)
            rb.addForce(rb.forward().mul(THRUST_FORCE * throttle * deltaTime));

        // lift
        // this could be done per surface but rn im doing it for all of them
        rb.addForce(rb.up().mul(getAeroForce(AeroSurfaceType.WING, deltaTime)));
        // i could do lift forces on stabilizers other than vertical but im not going to
        rb.addForceAtPosition(rb.right().mul(getAeroForce(AeroSurfaceType.VERTICAL_STABILIZER, deltaTime)),
                              rb.transform.position.add(rb.forward().mul(-2)));

        // controls
        if (input != null) {

            // in the future these will apply a torque that is in some way proportional to airspeed

            if (input.forward() > 0.1f) // rotation.rotateAxis(-0.1f, right);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.forward().mul(-2))); // up force back

            else if (input.forward() < -0.1f) // rotation.rotateAxis(0.1f, right);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.forward().mul(-2))); // down force back

            if (input.right() > 0.1f) { // rotation.rotateAxis(0.1f, forward);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.right().mul(2))); // down force right
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.right().mul(-2))); // up force left

            } else if (input.right() < -0.1f) { // rotation.rotateAxis(-0.1f, forward);
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)).rotateX(CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.right().mul(-2))); // down force left
                rb.addForceAtPosition(rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)).rotateX(-CONTROL_SURFACE_DEFLECT),
                                      rb.transform.position.add(rb.right().mul(2))); // up force right
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

    @Override
    public void setRider(@NotNull Entity vehicle, @Nullable Entity rider) {
        // remove all current riders
        for (var passenger : vehicle.getPassengers()) {
            vehicle.removePassenger(passenger);
        }

        // if new rider, then set them as riding
        if (rider != null) {
            vehicle.addPassenger(rider);
        }
    }

    private enum AeroSurfaceType {
        CONTROL_SURFACE_UP,
        CONTROL_SURFACE_DOWN,
        WING,
        VERTICAL_STABILIZER
    }

    private float getAeroForce(AeroSurfaceType type, float deltaTime) {
        Bukkit.broadcastMessage("getAeroForce()");
        Bukkit.broadcastMessage("deltaTime: deltaTime");
        Bukkit.broadcastMessage("forward: " + rb.forward());
        Bukkit.broadcastMessage("right: " + rb.right());
        Bukkit.broadcastMessage("velocity: " + rb.velocity);
        float defaultAoA = rb.forward().angleSigned(rb.velocity, rb.right());
        Bukkit.broadcastMessage("defaultAoA: " + defaultAoA);
        float speedSquared = rb.velocity.lengthSquared();
        Bukkit.broadcastMessage("speedSquared: " + speedSquared);

        return deltaTime * AIR_DENSITY * speedSquared * (float)Math.PI * switch (type) {
            case WING -> WING_AREA * defaultAoA;

            case CONTROL_SURFACE_UP -> CONTROL_SURFACE_AREA * (defaultAoA + CONTROL_SURFACE_DEFLECT);

            case CONTROL_SURFACE_DOWN -> CONTROL_SURFACE_AREA * (defaultAoA - CONTROL_SURFACE_DEFLECT);

            // might have to do tangential velocity which i dont want to do
            case VERTICAL_STABILIZER -> STABILIZER_AREA * (rb.forward().angleSigned(rb.velocity, rb.up()));
        };
    }
}
