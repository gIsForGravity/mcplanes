package co.tantleffbeef.mcplanes.physics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Rigidbody {
    public final Transform transform;
    private final Transform previousTransform;
    public final Vector3f velocity;
    public final Quaternionf angularVelocity;
    private final Collider collider;
    private float mass;
    // We store the inverse mass so we can
    // multiply by that instead of dividing by mass
    private float inverseMass;
    private final Matrix3f inertiaTensor;
    private final Matrix3f inertiaTensorInverse;
    public final Vector3f acceleration;
    private final float drag;
    private final float angularDrag;
    private final Vector3f tempVector;
    private final Quaternionf tempQuat;
    private final boolean hasGravity;

    public Rigidbody(Transform transform, Collider collider, float mass, float drag, float angularDrag, boolean hasGravity, Matrix3f inertiaTensor) {
        this.transform = transform;
        this.previousTransform = new Transform();
        this.collider = collider;
        this.acceleration = new Vector3f();
        this.tempVector = new Vector3f();
        this.tempQuat = new Quaternionf();
        this.velocity = new Vector3f();
        this.angularVelocity = new Quaternionf();
        this.drag = drag;
        this.angularDrag = angularDrag;
        this.hasGravity = hasGravity;
        this.inertiaTensor = inertiaTensor;
        this.inertiaTensorInverse = new Matrix3f(inertiaTensor).invert();
        setMass(mass);
    }

    public Rigidbody(Transform transform, Collider collider, float mass, float drag, float angularDrag, boolean hasGravity) {
        this(transform, collider, mass, drag, angularDrag, hasGravity, new Matrix3f());
    }

    public Rigidbody(Transform transform, Collider collider, float mass, float drag, float angularDrag) {
        this(transform, collider, mass, drag, angularDrag, true);
    }


    public Rigidbody(Transform transform, Collider collider, float mass) {
        this(transform, collider, mass, 0, 0);
    }

    /**
     * Call this before any physics thing
     */
    public void pretick() {
        // This object
        acceleration.zero();
        if (hasGravity)
            acceleration.y = -9.8f;

    }

    /**
     * Called every physics tick (after all the setting of things)
     * @param deltaTime the time since the last tick
     */
    public void tick(float deltaTime) {
        // Tick this object first

        // Apply drag
//        addForce(new Vector3f(velocity()).mul(-drag * deltaTime));
//        addTorque(new Quaternionf(angularVelocity).mul(-angularDrag * deltaTime));

        // Apply acceleration
        velocity.add(acceleration.mul(deltaTime, tempVector));

        // Store previous position
        previousTransform.set(transform);

        // Apply velocity
        transform.position.add(velocity.mul(deltaTime, tempVector));

        // Apply rotation velocity
        transform.rotation.add(angularVelocity.mul(deltaTime, tempQuat));

        // Send to collider
        collider.tick(deltaTime, previousTransform, this);
        // Call subticks
        // TODO fix this
//        collider.moveCenter(entity.location());
//        collider.tick(deltaTime);
////        resolveCollisions();
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
        this.inverseMass = 1f / mass;
    }

    // https://www.gamedev.net/forums/topic/56471-extracting-direction-vectors-from-quaternion/
    // probably should be calced avery tick and this should just return that vector
    // also maybe the rotation matrix thing is faster
    public Vector3f forward() {
        Quaternionf rot = transform.rotation;

        return new Vector3f(
                2 * (rot.x * rot.z + rot.w * rot.y),
                2 * (rot.y * rot.z - rot.w * rot.x),
                1 - 2 * (rot.x * rot.x + rot.y * rot.y)
        ).normalize();
    }
    public Vector3f up() {
        Quaternionf rot = transform.rotation;

        return new Vector3f(
                2 * (rot.x * rot.y - rot.w * rot.z),
                1 - 2 * (rot.x * rot.x + rot.z * rot.z),
                2 * (rot.y * rot.z + rot.w * rot.x)
        ).normalize();
    }
    public Vector3f right() {
        Quaternionf rot = transform.rotation;

        return new Vector3f(
                1 - 2 * (rot.y * rot.y + rot.z * rot.z),
                2 * (rot.x * rot.y + rot.w * rot.z),
                2 * (rot.x * rot.z - rot.w * rot.y)
        ).normalize();
    }

    /**
     * Applies a force to the object
     * @param force the force to apply **THIS VECTOR WILL BE MODIFIED**
     */
    public void addForce(Vector3f force) {
        force.mul(inverseMass);
        acceleration.add(force);
    }

//    public void addTorque(Vector3f torque) {
//
////        Bukkit.broadcastMessage(ChatColor.GOLD + "vector torque: " + torque);
//
//        addTorque(new Quaternionf().rotateXYZ(torque.x, torque.y, torque.z));
//    }

    public void addTorque(Vector3f torque) {
        Vector3f dw = new Vector3f();

        inertiaTensorInverse.transform(torque, dw);

        angularVelocity.add(new Quaternionf().rotateXYZ(dw.x, dw.y, dw.z));
    }

    public void addForceAtPosition(Vector3f force, Vector3f position) {
        addForce(force);
        Vector3f difference = new Vector3f(position);

//        Bukkit.broadcastMessage(ChatColor.RED + "force: " + force + " pos: " + position);

        difference.sub(transform.position); // why does java do this to me

//        Bukkit.broadcastMessage(ChatColor.RED + "diff: " + difference);

        addTorque(difference.cross(force));
    }

    public void addForceAtRelativePosition(Vector3f force, Vector3f position) {
        addForce(force);
//        Bukkit.broadcastMessage(ChatColor.AQUA + "diff: " + position.length());

        addTorque(position.cross(force));
    }


    // TODO: fix this
//    private void resolveCollisions() {
//        final var collisionDirection = collider.getDirections();
//        if (!collisionDirection.isColliding())
//            return;
//
//        final Vector3f calculatedVelocity = new Vector3f().set(entity.velocity());
//
//        if (collisionDirection.up || collisionDirection.down) {
//            float yVel = calculatedVelocity.y * 0.25f;
//
//            if (yVel > 0.05f)
//                calculatedVelocity.y = yVel;
//            else
//                calculatedVelocity.y = 0;
//        }
//
//        if (collisionDirection.north || collisionDirection.south) {
//            float zVel = calculatedVelocity.z * 0.25f;
//
//            if (zVel > 0.05f)
//                calculatedVelocity.z = zVel;
//            else
//                calculatedVelocity.z = 0;
//        }
//
//        if (collisionDirection.east || collisionDirection.west) {
//            float xVel = calculatedVelocity.x * 0.25f;
//
//            if (xVel > 0.05f)
//                calculatedVelocity.x = xVel;
//            else
//                calculatedVelocity.x = 0;
//        }
//
//        final var event = new PhysicsObjectCollisionEvent(this,
//                collisionDirection,
//                entity.velocity(),
//                calculatedVelocity,
//                entity.location(),
//                new Vector3f(entity.previousLocation()));
//
//        pluginManager.callEvent(event);
//        entity.velocity().set(event.newVelocity());
//        entity.location().set(event.resolvedPosition());
//    }
}
