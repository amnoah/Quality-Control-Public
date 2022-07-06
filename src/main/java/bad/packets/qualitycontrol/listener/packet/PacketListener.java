package bad.packets.qualitycontrol.listener.packet;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.processor.packet.ReceivingPacketProcessor;
import bad.packets.qualitycontrol.processor.packet.SendingPacketProcessor;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketLoginSendEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import io.github.retrooper.packetevents.util.GeyserUtil;

/**
 * In this class we listen to all of the packets being transmitted while we are playing the game.
 * In order to increase main thread and netty thread performance we run our checks on a separate thread.
 */
public final class PacketListener extends SimplePacketListenerAbstract {

    public PacketListener() {
        super();
    }

    /**
     * In order to start tracking the player the moment they join we need to listen to login packets. By using this
     * we can begin to track players before Bukkit even tells plugins that the player joined, allowing us to get some
     * early information like client brand without very messy methods.
     */
    @Override
    public void onPacketLoginSend(final PacketLoginSendEvent event) {
        if (event != null) {
            if (event.getPacketType() == PacketType.Login.Server.LOGIN_SUCCESS) {
                if (GeyserUtil.isGeyserPlayer(event.getUser().getProfile().getUUID())) return;

                QualityControl.INSTANCE.playerManager.addPlayer(event.getUser());
            }
        }
    }

    /**
     * Handle any incoming Play state packets, sending it to our Receiving Packet Processor to be handled appropriately.
     */
    @Override
    public void onPacketPlayReceive(final PacketPlayReceiveEvent event) {
        final QualityControlPlayer data = QualityControl.INSTANCE.playerManager.get(event.getUser());

        if (data != null) {
            ReceivingPacketProcessor.process(event, data);
        }
    }

    /**
     * Handle any outgoing Play state packets, sending it to our Sending Packet Processor to be handled appropriately.
     */
    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent event) {
        final QualityControlPlayer data = QualityControl.INSTANCE.playerManager.get(event.getUser());

        if (data != null) {
            SendingPacketProcessor.process(event, data);
        }
    }
}
