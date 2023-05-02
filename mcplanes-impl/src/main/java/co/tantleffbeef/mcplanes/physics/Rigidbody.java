package co.tantleffbeef.mcplanes.physics;

import co.tantleffbeef.mcplanes.physics.event.PhysicsObjectCollisionEvent;
import org.bukkit.plugin.PluginManager;
import org.joml.Vector3f;

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

    public Rigidbody(PluginManager pluginManager, RigidEntity entity, Collider collider, float mass) {
        this.pluginManager = pluginManager;
        this.entity = entity;
        this.collider = collider;
        this.acceleration = new Vector3f();
        setMass(mass);
    }

    /**
     * Call this before any physics thing
     */
    public void pretick() {
        System.out.println("rb pretick");
        // Sub-Preticks
        entity.pretick();

        // This object
        acceleration.zero();
    }

    /**
     * Called every physics tick (after all the setting of things)
     * @param deltaTime the time since the last tick
     */
    @Override
    public void tick(float deltaTime) {
        System.out.println("rb tick");
        // Tick this object first
        // Apply acceleration
        entity.velocity().add(acceleration);

        // Call subticks
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
        final var collisionDirection = collider.getDirection();
        if (collisionDirection == Collider.CollisionDirection.NONE)
            return;

        final Vector3f calculatedVelocity = new Vector3f().set(entity.velocity());

        switch (collisionDirection) {
            case UP, DOWN -> {
                float yVel = calculatedVelocity.y * 0.25f;

                if (yVel > 0.05f)
                    calculatedVelocity.y = yVel;
                else
                    calculatedVelocity.y = 0;
            }
            case NORTH, SOUTH -> {
                float zVel = calculatedVelocity.z * 0.25f;

                if (zVel > 0.05f)
                    calculatedVelocity.z = zVel;
                else
                    calculatedVelocity.z = 0;
            }
            case WEST, EAST -> {
                float xVel = calculatedVelocity.x * 0.25f;

                if (xVel > 0.05f)
                    calculatedVelocity.x = xVel;
                else
                    calculatedVelocity.x = 0;
            }
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
