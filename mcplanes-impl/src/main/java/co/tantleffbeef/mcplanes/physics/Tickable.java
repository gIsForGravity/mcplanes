package co.tantleffbeef.mcplanes.physics;

public interface Tickable {
    default void pretick() {}
    default void tick(float deltaTime) {}
}
