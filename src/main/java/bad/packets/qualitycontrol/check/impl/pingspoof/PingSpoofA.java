package bad.packets.qualitycontrol.check.impl.pingspoof;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * PingSpoofA
 */

@CheckInfo(name = "PingSpoof", type = "A")
public class PingSpoofA extends PacketCheck {

    public PingSpoofA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.KEEP_ALIVE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (!data.isSafeToRun()) return;

        // Keep Alive ping should never be bigger than transaction ping in real circumstances.
        if ((data.getKeepAlivePing() - data.getTransactionPing()) > 120) fail();
    }
}