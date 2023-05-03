package co.tantleffbeef.mcplanes.physics;

import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RigidDisplay implements RigidEntity {
    private final Display entity;
    private final Vector3f currentLocation;
    private final Vector3f previousLocation;
    private final Quaternionf currentRotation;
    private final Vector3f velocity;
    private final Vector3f tempVector;

    public RigidDisplay(Display displayEntity) {
        this.entity = displayEntity;
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
        final var location = entity.getLocation();
        currentLocation.set(location.getX(), location.getY(), location.getZ());

        // Grab the rotation
        final var transform = entity.getTransformation();
        currentRotation.set(transform.getRightRotation());
    }

    @Override
    public void tick(float deltaTime) {
        // Move based on velocity
        currentLocation.add(velocity.mul(deltaTime, tempVector));

        // Set the bukkitLocation
        final var bukkitLocation = entity.getLocation();
        bukkitLocation.setX(currentLocation.x);
        bukkitLocation.setY(currentLocation.y);
        bukkitLocation.setZ(currentLocation.z);
        entity.teleport(bukkitLocation);

        // Set the rotation
        final var bukkitTransform = entity.getTransformation();
        bukkitTransform.getRightRotation().set(currentRotation);
        entity.setTransformation(bukkitTransform);
    }
}
