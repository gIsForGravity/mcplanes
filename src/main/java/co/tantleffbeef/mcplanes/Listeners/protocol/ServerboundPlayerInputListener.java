package co.tantleffbeef.mcplanes.Listeners.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ServerboundPlayerInputListener extends PacketAdapter {
    public ServerboundPlayerInputListener(Plugin plugin, ListenerPriority listenerPriority) {
        super(plugin, listenerPriority, PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        var packet = (ServerboundPlayerInputPacket) event.getPacket().getHandle();
        Bukkit.broadcastMessage("Packet data:");
        Bukkit.broadcastMessage("Xxa: " + packet.getXxa());
        Bukkit.broadcastMessage("Zza: " + packet.getZza());
        Bukkit.broadcastMessage("isJumping: " + packet.isJumping());
        Bukkit.broadcastMessage("isShiftKeyDown: " + packet.isShiftKeyDown());
    }
}
