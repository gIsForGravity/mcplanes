package co.tantleffbeef.mcplanes.physics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class Collider implements Tickable {
    public enum CollisionDirection {
        NONE,
        UP,
        DOWN,
        NORTH,
        WEST,
        SOUTH,
        EAST
    }

    private final BoundingBox box;
    private final World world;
    private final Vector3f location;
    private CollisionDirection direction;

    public Collider(BoundingBox fromBox, Vector3f location, World world) {
        this.box = fromBox.clone();
        this.world = world;
        this.location = location;
        this.direction = CollisionDirection.NONE;
    }

    public void moveCenter(Vector3f newLocation) {
        newLocation.sub(location, location);
        box.shift(location.x, location.y, location.z);
        location.set(newLocation);
    }

    public @NotNull CollisionDirection getDirection() {
        return direction;
    }

    @Override
    public void tick(float deltaTime) {
        if (checkUp())
            return;
        if (checkDown())
            return;
        if (checkNorth())
            return;
        if (checkSouth())
            return;
        if (checkWest())
            return;
        checkEast();
    }

    private boolean checkBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                                CollisionDirection direction) {
        for (double ix = minX; ix <= maxX; ix++) {
            for (double iy = minY; iy <= maxY; iy++) {
                for (double iz = minZ; iz <= maxZ; iz++) {
                    final var blockLoc = new Location(world, ix, iy, iz);
                    final var block = blockLoc.getBlock();
                    final var type = block.getType();

                    if (type == Material.AIR
                            || type == Material.CAVE_AIR
                            || type == Material.VOID_AIR
                            || type == Material.STRUCTURE_VOID)
                        continue;

                    if (!box.contains(BoundingBox.of(block)))
                        continue;

                    this.direction = direction;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkUp() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMaxY(), box.getMaxY() + 1,
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.UP);
    }

    private boolean checkDown() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMinY() - 1,
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.DOWN);
    }

    private boolean checkNorth() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMinZ() - 1,
                CollisionDirection.SOUTH);
    }

    private boolean checkSouth() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMaxY(),
                box.getMaxZ(), box.getMaxZ() + 1,
                CollisionDirection.NORTH);
    }

    private boolean checkEast() {
        return checkBounds(box.getMaxX(), box.getMaxX() + 1,
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.EAST);
    }

    private boolean checkWest() {
        return checkBounds(box.getMinX(), box.getMinX() - 1,
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.WEST);
    }
}
