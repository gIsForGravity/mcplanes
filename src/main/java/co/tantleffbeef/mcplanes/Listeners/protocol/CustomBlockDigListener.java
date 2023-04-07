package co.tantleffbeef.mcplanes.Listeners.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CustomBlockDigListener extends PacketAdapter {
    public CustomBlockDigListener(Plugin plugin) {
        super(plugin, ListenerPriority.MONITOR, PacketType.Play.Client.BLOCK_DIG);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = (ServerboundPlayerActionPacket) event.getPacket().getHandle();
        Bukkit.broadcastMessage("PacketType.Play.Client.BLOCK_DIG");
        Bukkit.broadcastMessage(packet.getAction().toString());
    }
}
