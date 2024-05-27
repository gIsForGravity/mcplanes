package co.tantleffbeef.mcplanes.physics;

public interface Collider {
    void tick(float deltaTime, Transform previousTransform, Rigidbody rb);
}
