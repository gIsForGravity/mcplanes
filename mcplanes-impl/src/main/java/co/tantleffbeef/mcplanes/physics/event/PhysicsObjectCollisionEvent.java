package co.tantleffbeef.mcplanes.physics.event;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PhysicsObjectCollisionEvent extends Event {
    private final static HandlerList HANDLER_LIST = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    private final Rigidbody collidingBody;
    private final Collider.CollisionDirection direction;
    private final Vector3fc oldVelocity;
    private final Vector3f newVelocity;
    private final Vector3fc collidingPosition;
    private final Vector3f resolvedPosition;

    public PhysicsObjectCollisionEvent(@NotNull Rigidbody collidingBody,
                                       @NotNull Collider.CollisionDirection direction,
                                       @NotNull Vector3fc oldVelocity,
                                       @NotNull Vector3f newVelocity,
                                       @NotNull Vector3fc collidingPosition,
                                       @NotNull Vector3f resolvedPosition) {
        super(false);
        this.collidingBody = collidingBody;
        this.direction = direction;
        this.oldVelocity = oldVelocity;
        this.newVelocity = newVelocity;
        this.collidingPosition = collidingPosition;
        this.resolvedPosition = resolvedPosition;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gives the rigidbody that is colliding with blocks
     * @return the rigidbody
     */
    public @NotNull Rigidbody getCollidingBody() {
        return collidingBody;
    }

    /**
     * Gives the cardinal direction the collision happened in
     * @return the cardinal direction
     */
    public @NotNull Collider.CollisionDirection getDirection() {
        return direction;
    }

    /**
     * Provides **immutable** access to the unadjusted
     * velocity of the rigidbody
     * @return the old velocity
     */
    public @NotNull Vector3fc getOldVelocity() {
        return oldVelocity;
    }

    /**
     * Provides **mutable** access to the adjusted
     * velocity of the rigidbody
     * @return the new velocity
     */
    public @NotNull Vector3f newVelocity() {
        return newVelocity;
    }

    /**
     * Provides **immutable** access to the unresolved
     * location of the collision
     * @return the position of the collision
     */
    public @NotNull Vector3fc getCollidingPosition() {
        return collidingPosition;
    }

    /**
     * Provides **mutable** access to the resolved
     * position of the rigidbody
     * @return the resolved position
     */
    public @NotNull Vector3f resolvedPosition() {
        return resolvedPosition;
    }
}
