package co.tantleffbeef.mcplanes.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockBreakEvent extends BlockEvent implements Cancellable {
    private final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean dropItems;
    private boolean cancel;

    public CustomBlockBreakEvent(@NotNull Block theBlock, Player player, boolean dropItems, boolean cancelled) {
        super(theBlock);
        this.player = player;
        this.dropItems = dropItems;
        this.cancel = cancelled;
    }

    /**
     * Gets the Player that is breaking the block involved in this event.
     *
     * @return The Player that is breaking the block involved in this event
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets whether or not the block will attempt to drop items as it normally
     * would.
     *
     * @param dropItems Whether or not the block will attempt to drop items
     */
    public void setShouldDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }

    /**
     * Gets whether or not the block will attempt to drop items.
     *
     * @return Whether or not the block will attempt to drop items
     */
    public boolean shouldDropItems() {
        return this.dropItems;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
