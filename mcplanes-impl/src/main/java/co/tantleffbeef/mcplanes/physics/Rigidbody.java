package co.tantleffbeef.mcplanes.physics;

import co.tantleffbeef.mcplanes.physics.event.PhysicsObjectCollisionEvent;
import org.bukkit.plugin.PluginManager;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Rigidbody implements Tickable {
    private final RigidEntity entity;
    private final Collider collider;
    private final PluginManager pluginManager;
    private float mass;
    // We store the inverse mass so we can
    // multiply by that instead of dividing
    // by mass
    private float inverseMass;
    private final Vector3f acceleration;
    private final Vector3f tempVector;
    private Matrix4f rotationMatrix = new Matrix4f();

    public Rigidbody(PluginManager pluginManager, RigidEntity entity, Collider collider, float mass) {
        this.pluginManager = pluginManager;
        this.entity = entity;
        this.collider = collider;
        this.acceleration = new Vector3f();
        this.tempVector = new Vector3f();
        setMass(mass);
    }

    /**
     * Call this before any physics thing
     */
    public void pretick() {
        // Sub-Preticks
        entity.pretick();

        // This object
        acceleration.zero();

        rotationMatrix = rotationMatrix.rotation(currentRotation());
    }

    /**
     * Called every physics tick (after all the setting of things)
     * @param deltaTime the time since the last tick
     */
    @Override
    public void tick(float deltaTime) {
        // Tick this object first
        // Apply acceleration
        entity.velocity().add(acceleration.mul(deltaTime, tempVector));

        // Call subticks
        collider.moveCenter(entity.location());
        collider.tick(deltaTime);
        resolveCollisions();
        entity.tick(deltaTime);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        this.inverseMass = 1 / mass;
    }

    public Vector3f velocity() {
        return entity.velocity();
    }
    public Quaternionf currentRotation() { return entity.currentRotation(); }

    public Vector3f forward() { return new Vector3f(rotationMatrix.m20(), rotationMatrix.m21(), rotationMatrix.m22()); }
    public Vector3f up() { return new Vector3f(rotationMatrix.m10(), rotationMatrix.m11(), rotationMatrix.m12()); }
    public Vector3f right() { return new Vector3f(rotationMatrix.m00(), rotationMatrix.m01(), rotationMatrix.m02()); }

    public Vector3f acceleration() {
        return acceleration;
    }

    /**
     * Applies a force to the object
     * @param force the force to apply **THIS VECTOR WILL BE MODIFIED**
     */
    public void addForce(Vector3f force) {
        force.mul(inverseMass);
        acceleration.add(force);
    }

    private void resolveCollisions() {
        final var collisionDirection = collider.getDirections();
        if (!collisionDirection.isColliding())
            return;

        final Vector3f calculatedVelocity = new Vector3f().set(entity.velocity());

        if (collisionDirection.up || collisionDirection.down) {
            float yVel = calculatedVelocity.y * 0.25f;

            if (yVel > 0.05f)
                calculatedVelocity.y = yVel;
            else
                calculatedVelocity.y = 0;
        }

        if (collisionDirection.north || collisionDirection.south) {
            float zVel = calculatedVelocity.z * 0.25f;

            if (zVel > 0.05f)
                calculatedVelocity.z = zVel;
            else
                calculatedVelocity.z = 0;
        }

        if (collisionDirection.east || collisionDirection.west) {
            float xVel = calculatedVelocity.x * 0.25f;

            if (xVel > 0.05f)
                calculatedVelocity.x = xVel;
            else
                calculatedVelocity.x = 0;
        }

        final var event = new PhysicsObjectCollisionEvent(this,
                collisionDirection,
                entity.velocity(),
                calculatedVelocity,
                entity.location(),
                new Vector3f(entity.previousLocation()));

        pluginManager.callEvent(event);
        entity.velocity().set(event.newVelocity());
        entity.location().set(event.resolvedPosition());
    }
}
