package bad.packets.qualitycontrol.check.type;

import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

import java.util.ArrayList;
import java.util.List;

/**
 * Useful for misc. checks that don't require a certain category.
 */
public class PacketCheck extends Check {
    public PacketCheck(final QualityControlPlayer data) {
        super(data);
    }

    public List<PacketType.Play.Client> listenedPacketsIncoming = new ArrayList<>();
    public List<PacketType.Play.Server> listenedPacketsOutgoing = new ArrayList<>();

    /**
     * Universal handling for Packet Receive on PacketOrder checks.
     * @param event PacketEvents Receive Packet event.
     */
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (listenedPacketsIncoming.contains(event.getPacketType())) handleIncomingPacket(event);
    }

    /**
     * Universal handling for Packet Send on PacketOrder checks.
     * @param event PacketEvents Send Packet event.
     */
    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (listenedPacketsOutgoing.contains(event.getPacketType())) handleOutgoingPacket(event);
    }

    /**
     * Pass the event along to the associated check.
     * @param event PacketEvents Receive Packet event.
     */
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {

    }

    /**
     * Pass the event along to the associated check.
     * @param event PacketEvents Send Packet event.
     */
    public void handleOutgoingPacket(PacketPlaySendEvent event) {

    }
}
