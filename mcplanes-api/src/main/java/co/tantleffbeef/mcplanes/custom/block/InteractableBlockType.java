package co.tantleffbeef.mcplanes.custom.block;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.NotNull;

public interface InteractableBlockType {
    /**
     * Called when a player tries to interact with the block
     * @param interacter the player who tried to interact
     * @param blockPos where the block is
     * @param interacted the spigot block
     * @param action what type of interaction
     * @return whether to cancel the spigot event
     */
    boolean interactBlock(@NotNull Player interacter, @NotNull Location blockPos, @NotNull Block interacted, @NotNull Action action);
}
