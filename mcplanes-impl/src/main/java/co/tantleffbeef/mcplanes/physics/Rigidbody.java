package co.tantleffbeef.mcplanes.physics;

import org.joml.Vector3f;

public class Rigidbody implements Tickable {
    private final RigidEntity entity;
    private final Collider collider;
    private float mass;
    // We store the inverse mass so we can
    // multiply by that instead of dividing
    // by mass
    private float inverseMass;
    private final Vector3f acceleration;

    public Rigidbody(RigidEntity entity, Collider collider, float mass) {
        this.entity = entity;
        this.collider = collider;
        this.acceleration = new Vector3f();
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
    }

    /**
     * Called every physics tick (after all the setting of things)
     * @param deltaTime the time since the last tick
     */
    @Override
    public void tick(float deltaTime) {
        // Tick this object first
        // Apply acceleration
        entity.velocity().add(acceleration);

        // Call subticks
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

    public void addForce(Vector3f force) {
        force.mul(inverseMass);
    }
}
