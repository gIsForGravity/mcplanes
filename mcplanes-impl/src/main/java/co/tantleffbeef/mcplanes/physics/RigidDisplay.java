package co.tantleffbeef.mcplanes.physics;

import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RigidDisplay implements RigidEntity {
    private final Display display;
    private final ArmorStand physics;
    private final Vector3f currentLocation;
    private final Vector3f previousLocation;
    private final Quaternionf currentRotation;
    private final Vector3f velocity;
    private final Vector3f tempVector;

    public RigidDisplay(@NotNull ArmorStand physicsEntity, @NotNull Display displayEntity) {
        this.display = displayEntity;
        this.physics = physicsEntity;
        this.currentLocation = new Vector3f();
        this.previousLocation = new Vector3f();
        this.currentRotation = new Quaternionf();
        this.velocity = new Vector3f();
        this.tempVector = new Vector3f();
    }

    @Override
    public @NotNull Vector3f location() {
        return currentLocation;
    }

    @Override
    public @NotNull Vector3fc previousLocation() {
        return previousLocation;
    }

    @Override
    public @NotNull Quaternionf currentRotation() {
        return currentRotation;
    }

    @Override
    public @NotNull Vector3f velocity() {
        return velocity;
    }

    @Override
    public void pretick() {
        // Copy the current location into the
        // previous location
        previousLocation.set(currentLocation);

        // Grab the entity's current location and
        // save that
        final var location = physics.getLocation();
        currentLocation.set(location.getX(), location.getY(), location.getZ());

        // Grab the rotation
        final var transform = display.getTransformation();
        currentRotation.set(transform.getRightRotation());
    }

    @Override
    public void tick(float deltaTime) {
        // Move based on velocity
        currentLocation.add(velocity.mul(deltaTime, tempVector));

        // Set the bukkitLocation
        teleport(physics, currentLocation);

        // Set the rotation
        final var bukkitTransform = display.getTransformation();
        bukkitTransform.getRightRotation().set(currentRotation);
        display.setTransformation(bukkitTransform);
    }

    private static void teleport(Entity entity, Vector3fc location) {
        assert entity instanceof CraftEntity;
        final var craftEntity = (CraftEntity) entity;
        final var serverEntity = craftEntity.getHandle();
        serverEntity.moveTo(location.x(), location.y(), location.z(), 0, 0);
    }
}
