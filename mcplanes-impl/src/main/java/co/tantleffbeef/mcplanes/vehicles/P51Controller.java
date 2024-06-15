package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.*;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
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
        final var displayModel = world.spawn(new Location(world, location.getX(), location.getY(), location.getZ()), ItemDisplay.class, vehicle -> {
            vehicle.setGravity(false);
            vehicle.addScoreboardTag("mcplanes_plane");
            vehicle.addScoreboardTag("mcplanes_p51");

            final var transformation = vehicle.getTransformation();
            transformation.getScale().set(5f, 5f, 5f);
            vehicle.setTransformation(transformation);
            vehicle.setItemStack(displayItem);
            vehicle.setInterpolationDuration(1);
            vehicle.setTeleportDuration(1);
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
        // TODO: add back box collider
        // this.rb = new Rigidbody(new Transform(new Vector3f(xPos, yPos, zPos)), new AABBCollider(box, new Vector3f(xPos, yPos, zPos),
        //        location.getWorld()), 1.0f, 0.5f, 0.1f, true);
        var transform = new Transform(new Vector3f(xPos, yPos, zPos));
        var collider = new SuperflatCollider(transform, -60, -3);

        final float mass = 2.0f;
        final float radius = 0.2f;
        final float length = 7f;

        final float yawMoment = ( mass * radius * radius / 4 ) + ( mass* length * length / 12 );
        final float pitchRollMoment = ( 3 * mass * radius * radius / 8 ) + ( mass * length * length / 24);

        final Matrix3f inertiaTensor = new Matrix3f().m00(pitchRollMoment).m11(yawMoment).m22(pitchRollMoment);

        this.rb = new Rigidbody(transform, collider, mass, 0.5f, 0.1f, true, inertiaTensor);
    }

    private int tick = 0;
    private static final float MAX_VELOCITY_SQUARED = 500;
    private static final float THRUST_FORCE = 30;
    private static final float AIR_DENSITY = 1.225f;
    private static final float WING_AREA = 4;
    private static final float CONTROL_SURFACE_AREA = 1.5f;
    private static final float STABILIZER_AREA = 1.8f;
    private static final float CONTROL_SURFACE_DEFLECT = (float)Math.PI/6;
    private static final float TAIL_OFFSET = -4;
    private static final float WINGTIP_OFFSET = 5;
    private float throttle = 1f; // normally start at 0 but 1 for testing

    private float timer = 0f;

    @Override
    public boolean tick(float deltaTime, @Nullable Input input, @NotNull Entity vehicle) {
        timer += deltaTime;

        rb.pretick();

        if (timer > 5f) {
            Bukkit.broadcastMessage("down getAeroForce: " + getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime));
            Bukkit.broadcastMessage("position: " + rb.transform.position);
            Bukkit.broadcastMessage("throttle: " + throttle);
            timer = 0;

            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "rotation: " + rb.transform.rotation);
            Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "fw: " + rb.forward() + " ri: " + rb.right() + " up: " + rb.up());

        }


        // throttle
        if (rb.velocity.lengthSquared() < MAX_VELOCITY_SQUARED)
            rb.addForce(rb.forward().mul(THRUST_FORCE * throttle * deltaTime));


        // lift
        rb.addForceAtRelativePosition(
                rb.up().mul(getAeroForce(AeroSurfaceType.WING, deltaTime)),
                rb.forward().mul(-0.05f) // wings slightly behind COM
        );


        // i could do lift forces on stabilizers other than vertical but im not going to
        rb.addForceAtRelativePosition(
                rb.right().mul(getAeroForce(AeroSurfaceType.VERTICAL_STABILIZER, deltaTime)),
                rb.forward().mul(TAIL_OFFSET)
        );

        // controls
        // doesnt ever seem to be null
        if (input != null) {
//            Bukkit.broadcastMessage("fw: " + input.forward() + " rt: " + input.right() + " jm: " + input.jump());

            if (input.forward() > 0.1f)
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)),
//                        rb.forward().mul(TAIL_OFFSET)); // up force back
                rb.addTorque(new Vector3f(10, 0, 0));

            else if (input.forward() < -0.1f)
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)),
//                        rb.forward().mul(TAIL_OFFSET)); // down force back
                rb.addTorque(new Vector3f(-10, 0, 0));

            if (input.right() > 0.1f) {
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)),
//                        rb.right().mul(WINGTIP_OFFSET)); // down force right
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)),
//                        rb.right().mul(-WINGTIP_OFFSET)); // up force left
                rb.addTorque(new Vector3f(0, 0, 10));

            } else if (input.right() < -0.1f) {
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_UP, deltaTime)),
//                        rb.right().mul(-WINGTIP_OFFSET)); // down force left
//                rb.addForceAtRelativePosition(
//                        rb.up().mul(getAeroForce(AeroSurfaceType.CONTROL_SURFACE_DOWN, deltaTime)),
//                        rb.right().mul(WINGTIP_OFFSET)); // up force right
                rb.addTorque(new Vector3f(0, 0, -10));
            }

            if (input.jump() && throttle < 1) {
                Bukkit.broadcastMessage("jump input is happening");
                throttle += 0.05f;
            }

//            if (input.crouch() && throttle < 1) // probably have to cancel leave event but then how do you leave
//                throttle += 0.05f; // idk how you would throttle down (maybe something like this should be a hotbar thing)

        }


        rb.tick(deltaTime);

        // update entity position to match transform
        Transform transform = rb.transform;
        World world = vehicle.getWorld();
        ItemDisplay displayVehicle = (ItemDisplay) vehicle;
        Transformation displayTransform = displayVehicle.getTransformation();

        displayTransform.getLeftRotation().set(transform.rotation.normalize());
        displayTransform.getLeftRotation().rotateY((float) Math.PI);


        Vector3f position = transform.position;
        Location teleportPosition = new Location(world, position.x, position.y, position.z);

        teleportVehicle(vehicle, teleportPosition);

        displayVehicle.setTransformation(displayTransform);


        return true;
    }

    /**
     * Uses nms to teleport the vehicle to the new position while also moving rider
     * @param vehicle the entity to teleport
     * @param position where to teleport it to
     */
    private static void teleportVehicle(@NotNull Entity vehicle, @NotNull Location position) {
        var craftVehicle = (CraftEntity) vehicle;
        var nmsEntity = craftVehicle.getHandle();
        nmsEntity.moveTo(position.getX(), position.getY(), position.getZ(), position.getYaw(), position.getPitch());
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
//        Bukkit.broadcastMessage("getAeroForce()");
//        Bukkit.broadcastMessage("deltaTime: " + deltaTime);
        Bukkit.broadcastMessage("forward: " + rb.forward());
        Bukkit.broadcastMessage("right: " + rb.right());
        Bukkit.broadcastMessage("velocity: " + rb.velocity + " " + rb.velocity.length());
//        Bukkit.broadcastMessage("position: " + rb.transform.position);
//        Bukkit.broadcastMessage("rotation: " + rb.transform.rotation.getEulerAnglesXYZ(new Vector3f()));
        float defaultAoA = rb.forward().angleSigned(rb.velocity, rb.right());
        Bukkit.broadcastMessage("defaultAoA: " + defaultAoA);
        float speedSquared = rb.velocity.lengthSquared(); // would be best as the velocity of the surface but this works
//        Bukkit.broadcastMessage("speedSquared: " + speedSquared);

        if (type == AeroSurfaceType.CONTROL_SURFACE_DOWN)
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Down deflection of aoa " + (defaultAoA - CONTROL_SURFACE_DEFLECT) +
                    " with force of " + (deltaTime * AIR_DENSITY * speedSquared * (float)Math.PI * CONTROL_SURFACE_AREA * (defaultAoA - CONTROL_SURFACE_DEFLECT))
                    + " should be positive?");
        if (type == AeroSurfaceType.CONTROL_SURFACE_UP)
            Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "Up deflection of aoa " + (defaultAoA + CONTROL_SURFACE_DEFLECT) +
                    " with force of " + (deltaTime * AIR_DENSITY * speedSquared * (float)Math.PI * CONTROL_SURFACE_AREA * (defaultAoA + CONTROL_SURFACE_DEFLECT))
                    + " should be negative?");

        return deltaTime * AIR_DENSITY * speedSquared * (float)Math.PI * switch (type) {
            case WING -> WING_AREA * defaultAoA;

            case CONTROL_SURFACE_UP -> CONTROL_SURFACE_AREA * (defaultAoA + CONTROL_SURFACE_DEFLECT);

            case CONTROL_SURFACE_DOWN -> CONTROL_SURFACE_AREA * (defaultAoA - CONTROL_SURFACE_DEFLECT);

            // might have to do tangential velocity which i dont want to do
            case VERTICAL_STABILIZER -> STABILIZER_AREA * (rb.forward().angleSigned(rb.velocity, rb.up()));
        };
    }
}
