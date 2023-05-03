package co.tantleffbeef.mcplanes.vehicles;

import co.tantleffbeef.mcplanes.physics.Collider;
import co.tantleffbeef.mcplanes.physics.RigidDisplay;
import co.tantleffbeef.mcplanes.physics.Rigidbody;
import co.tantleffbeef.mcplanes.pojo.Input;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
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
        final var display = location.getWorld().spawn(location, ItemDisplay.class);
        display.setItemStack(displayItem);

        return new P51(pluginManager, display);
    }

    public P51(PluginManager pluginManager, Display entity) {
        final var xPos = entity.getLocation().getX();
        final var yPos = entity.getLocation().getY();
        final var zPos = entity.getLocation().getZ();
        final var box = new BoundingBox(xPos, yPos, zPos, xPos, yPos, zPos);
        box.expand(2.0);
        this.rb = new Rigidbody(pluginManager, new RigidDisplay(entity), new Collider(box, new Vector3f((float) xPos, (float) yPos, (float) zPos), entity.getWorld()), 1.0f);
        this.entity = entity;
    }

    private boolean firstTick = true;

    @Override
    public void tick(@Nullable Input input, float deltaTime) {
        rb.pretick();

        if (firstTick) {
            //rb.addForce(new Vector3f(0, 0, 2));
            rb.velocity().set(0, 0, 2);
            firstTick = false;
        }

        rb.tick(deltaTime);
    }

    @Override
    public @Nullable Player driver() {
        return null;
    }
}
