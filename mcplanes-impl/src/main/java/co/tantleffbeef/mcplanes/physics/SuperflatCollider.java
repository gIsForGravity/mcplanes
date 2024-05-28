package co.tantleffbeef.mcplanes.physics;

import org.joml.Vector3f;

public class SuperflatCollider implements Collider {
    public final Transform transform;
    private final int floorPos;
    private final int yOffset;
    private final Vector3f tempVector;

    public SuperflatCollider(Transform transform, int floorPos, int yOffset) {
        this.transform = transform;
        this.floorPos = floorPos;
        this.yOffset = yOffset;
        this.tempVector = new Vector3f();
    }

    @Override
    public void tick(float deltaTime, Transform previousTransform, Rigidbody rb) {
        if (transform.position.y + yOffset >= floorPos)
            return;

        transform.position.y = floorPos - yOffset;
        transform.rotation.getEulerAnglesXYZ(tempVector);
        rb.velocity.y *= -0.5f;
        tempVector.x = 0;
        transform.rotation.identity().rotateXYZ(tempVector.x, tempVector.y, tempVector.z);
    }
}
