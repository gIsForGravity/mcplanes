package co.tantleffbeef.mcplanes.pojo;

public record Input(float forward, float right, boolean jump, boolean crouch) {
    public static final Input empty = new Input(0f, 0f, false, false);
}
