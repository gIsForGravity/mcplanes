package co.tantleffbeef.mcplanes.Listeners.protocol;

import co.tantleffbeef.mcplanes.Input;
import co.tantleffbeef.mcplanes.Plugin;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;

public class ServerboundPlayerInputListener extends PacketAdapter {
    public ServerboundPlayerInputListener(Plugin plugin, ListenerPriority listenerPriority) {
        // Use plugin and listener priority given by constructor and listen to STEER_VEHICLE packet
        // also known as ServerboundPlayerInputListener in mojang mappings
        super(plugin, listenerPriority, PacketType.Play.Client.STEER_VEHICLE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        // cast packet as ServerboundPlayerInputPacket
        final var packet = (ServerboundPlayerInputPacket) event.getPacket().getHandle();
        // cast plugin from bukkit plugin to mcplanes plugin and get vehiclemanager instance
        final var planesPlugin = (Plugin) plugin;
        final var vehicleManager = planesPlugin.getVehicleManager();

        // return early if player is not riding a vehicle
        if (!vehicleManager.checkIfRider(event.getPlayer().getUniqueId()))
            return;

        // create Input based on packet and then send it to the JVehicleRider
        final Input input = new Input(packet.getZza(), packet.getXxa(), packet.isJumping(), packet.isShiftKeyDown());
        vehicleManager.riderInput(event.getPlayer(), input);
    }
}
