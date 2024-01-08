package co.tantleffbeef.mcplanes;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Provides some tools that allow you to access MC internals
 */
@SuppressWarnings("unused")
public interface InternalsTools {
    /**
     * Modifies the useItemRemaining field of the player, which
     * is how many ticks it takes to use the current item
     * (i.e. how long to eat food)
     * @param player the player to modify
     */
    void setPlayerUseItemRemaining(@NotNull Player player);
}
