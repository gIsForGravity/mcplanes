package co.tantleffbeef.mcplanes.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    public final Vector3f position;
    public final Quaternionf rotation;

    public Transform(Vector3f position, Quaternionf rotation) {
        this.position = new Vector3f(position);
        this.rotation = new Quaternionf(rotation);
    }

    public Transform(Vector3f position) {
        this(position, new Quaternionf());
    }

    public Transform() {
        this(new Vector3f(), new Quaternionf());
    }

    public void set(Transform from) {
        position.set(from.position);
        rotation.set(from.rotation);
    }
}
