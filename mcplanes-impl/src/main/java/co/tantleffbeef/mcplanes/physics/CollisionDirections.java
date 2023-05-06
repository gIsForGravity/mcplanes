package co.tantleffbeef.mcplanes.physics;

public class CollisionDirections {
    public boolean up = false;
    public boolean down = false;
    public boolean north = false;
    public boolean south = false;
    public boolean east = false;
    public boolean west = false;

    public void clear() {
        up = false;
        down = false;
        north = false;
        south = false;
        east = false;
        west = false;
    }

    public boolean isColliding() {
        return !(up || down || north || south || east || west);
    }
}
