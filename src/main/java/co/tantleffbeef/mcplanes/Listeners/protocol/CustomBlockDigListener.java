package co.tantleffbeef.mcplanes.Listeners.protocol;

import co.tantleffbeef.mcplanes.BlockBreakProgress;
import co.tantleffbeef.mcplanes.BlockManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomBlockDigListener extends PacketAdapter {
    private final Map<UUID, BlockBreakProgress> playerBreakProgress;
    private final BlockManager blockManager;

    public CustomBlockDigListener(@NotNull Plugin plugin, @NotNull BlockManager blockManager) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.BLOCK_DIG);
        playerBreakProgress = new HashMap<>();
        this.blockManager = blockManager;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = (ServerboundPlayerActionPacket) event.getPacket().getHandle();
        final var action = packet.getAction();
        if (action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && action != ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK && action != ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK)
            return;

        final var player = event.getPlayer();
        final var world = player.getWorld();
        final var pos = packet.getPos();
        final var blockLocation = new Location(world, pos.getX(), pos.getY(), pos.getZ());
        final var block = blockLocation.getBlock();

        if (action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK || action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK)
            onAbortBlockDig(player, block);
        else
            onBlockDig(player, block);
    }

    private void onBlockDig(Player player, Block block) {
        // TODO: block breaking progress
        if (blockManager.isCustomBlock(block.getLocation()))
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> blockManager.deleteCustomBlock(block.getLocation()), 0);
    }

    private void finishBlockDig(Player player, Block block) {

    }

    private void onAbortBlockDig(Player player, Block block) {

    }
}
