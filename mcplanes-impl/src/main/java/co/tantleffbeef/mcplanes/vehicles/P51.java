package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.RigidDisplay;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class P51 implements PhysicsVehicle {
    public final Rigidbody rb;
    public final Entity entity;

    /**
     * *Precondition: location has a world*
     * @return a brand new p51!!!!!
     */
    public static P51 spawn(@NotNull PluginManager pluginManager, @NotNull Location location, ItemStack displayItem) {
        assert location.getWorld() != null;

        final var world = location.getWorld();
        final var armorStand = world.spawn(location, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setGravity(false);

            /*world.spawn(location, ItemDisplay.class, display -> {
                display.setItemStack(displayItem);
                final var transformation = display.getTransformation();
                transformation.getScale().set(5f, 5f, 5f);
                display.setTransformation(transformation);
                stand.addPassenger(display);
            });*/
        });

        final var itemDisplay = world.spawn(location, ItemDisplay.class, display -> {
            display.setItemStack(displayItem);
            final var transformation = display.getTransformation();
            transformation.getScale().set(5f, 5f, 5f);
            display.setTransformation(transformation);
        });

        return new P51(pluginManager, armorStand, itemDisplay);
    }

    public P51(PluginManager pluginManager, ArmorStand stand, Display model) {
        final var xPos = stand.getLocation().getX();
        final var yPos = stand.getLocation().getY();
        final var zPos = stand.getLocation().getZ();
        final var box = new BoundingBox(xPos, yPos, zPos, xPos, yPos, zPos);
        box.expand(2.0);
        this.rb = new Rigidbody(pluginManager, new RigidDisplay(stand, model), new Collider(box, new Vector3f((float) xPos, (float) yPos, (float) zPos), model.getWorld()), 1.0f);
        this.entity = stand;
    }

    private boolean firstTick = true;

    @Override
    public void tick(@Nullable Input input, float deltaTime) {
        rb.pretick();

        rb.velocity().set(0, 0, 2);

        rb.tick(deltaTime);
    }

    @Override
    public @Nullable Player driver() {
        return null;
    }
}
