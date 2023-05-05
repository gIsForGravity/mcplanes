package co.tantleffbeef.mcplanes.physics;

import org.bukkit.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Collider implements Tickable {
    private static final ArrayList<Collider> colliders = new ArrayList<>();

    public static void startTicking(Plugin plugin) {
        //plugin.getServer().getScheduler().runTaskTimer(plugin, () -> colliders.forEach(Collider::renderOutline), 1, 1);
    }

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

        colliders.add(this);
    }

    public void moveCenter(Vector3f newLocation) {
        newLocation.sub(location, location);
        box.shift(location.x, location.y, location.z);
        location.set(newLocation);
    }

    public static void renderBounds(BoundingBox box, World world) {
        // corners
        final var eus = new Vector3d(box.getMaxX(), box.getMaxY(), box.getMaxZ());
        final var eds = new Vector3d(box.getMaxX(), box.getMinY(), box.getMaxZ());
        final var eun = new Vector3d(box.getMaxX(), box.getMaxY(), box.getMinZ());
        final var edn = new Vector3d(box.getMaxX(), box.getMinY(), box.getMinZ());
        final var wus = new Vector3d(box.getMinX(), box.getMaxY(), box.getMaxZ());
        final var wds = new Vector3d(box.getMinX(), box.getMinY(), box.getMaxZ());
        final var wun = new Vector3d(box.getMinX(), box.getMaxY(), box.getMinZ());
        final var wdn = new Vector3d(box.getMinX(), box.getMinY(), box.getMinZ());
        // minX, minY, minZ -> maxX, minY, minZ
        renderLine(world, eus, eds);
        renderLine(world, eus, eun);
        renderLine(world, eus, wus);

        renderLine(world, wds, wus);
        renderLine(world, wds, wdn);
        renderLine(world, wds, eds);

        renderLine(world, edn, eds);
        renderLine(world, edn, eun);
        renderLine(world, edn, wdn);

        renderLine(world, wun, wdn);
        renderLine(world, wun, wus);
        renderLine(world, wun, eun);
    }

    public void renderOutline() {
        renderBounds(box, world);
    }

    private static void renderLine(World world, Vector3d pos1, Vector3d pos2) {
        final Vector3d tempVector = new Vector3d();

        for (double i = 0; i <= 1.0; i += 0.5) {
            final var pos = pos1.lerp(pos2, i, tempVector);
            world.spawnParticle(Particle.SPELL_INSTANT, pos.x, pos.y, pos.z, 1);
        }
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
        return checkBounds(minX, maxX, minY, maxY, minZ, maxZ, direction, false);
    }

    private boolean checkBounds(double minX, double maxX, double minY, double maxY, double minZ, double maxZ,
                                CollisionDirection direction, boolean showDebugBox) {
        for (double ix = minX; ix <= maxX; ix++) {
            for (double iy = minY; iy <= maxY; iy++) {
                for (double iz = minZ; iz <= maxZ; iz++) {
                    final var blockLoc = new Location(world, ix, iy, iz);
                    final var block = blockLoc.getBlock();
                    final var type = block.getType();

                    if (showDebugBox) {
                        renderBounds(BoundingBox.of(block), world);
                    }

                    if (type == Material.AIR
                            || type == Material.CAVE_AIR
                            || type == Material.VOID_AIR
                            || type == Material.STRUCTURE_VOID)
                        continue;

                    final var blockBoundingBox = BoundingBox.of(block);

                    renderBounds(blockBoundingBox, world);

                    this.direction = direction;
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkUp() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMaxY(), box.getMaxY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.UP);
    }

    private boolean checkDown() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMinY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.DOWN);
    }

    private boolean checkNorth() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMinZ(),
                CollisionDirection.NORTH);
    }

    private boolean checkSouth() {
        return checkBounds(box.getMinX(), box.getMaxX(),
                box.getMinY(), box.getMaxY(),
                box.getMaxZ(), box.getMaxZ(),
                CollisionDirection.SOUTH);
    }

    private boolean checkEast() {
        return checkBounds(box.getMaxX(), box.getMaxX(),
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.EAST);
    }

    private boolean checkWest() {
        return checkBounds(box.getMinX(), box.getMinX(),
                box.getMinY(), box.getMaxY(),
                box.getMinZ(), box.getMaxZ(),
                CollisionDirection.WEST);
    }
}
