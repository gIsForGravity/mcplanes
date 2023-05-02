package co.tantleffbeef.mcplanes.physics;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface RigidEntity extends Tickable {
    @NotNull Vector3f location();
    @NotNull Vector3fc previousLocation();
    @NotNull Quaternionf currentRotation();
    @NotNull Vector3f velocity();
}
