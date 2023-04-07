package co.tantleffbeef.mcplanes.event;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockBreakEvent extends BlockEvent implements Cancellable {
    public CustomBlockBreakEvent(@NotNull Block theBlock) {
        super(theBlock);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
