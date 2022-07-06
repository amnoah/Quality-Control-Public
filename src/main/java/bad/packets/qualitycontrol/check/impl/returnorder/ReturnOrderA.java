package bad.packets.qualitycontrol.check.impl.returnorder;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientKeepAlive;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerKeepAlive;

import java.util.ArrayDeque;

/**
 * Created by am noah
 * ReturnOrderA
 */

@CheckInfo(name = "ReturnOrder", type = "A")
public class ReturnOrderA extends PacketCheck {

    public ReturnOrderA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.KEEP_ALIVE);

        listenedPacketsOutgoing.add(PacketType.Play.Server.KEEP_ALIVE);
    }

    private final ArrayDeque<Long> keepAliveOrder = new ArrayDeque<>();

    /*
     * Checks for wrongful return order of Keep Alive packets.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (keepAliveOrder.getFirst().equals(new WrapperPlayClientKeepAlive(event).getId())) {
            keepAliveOrder.removeFirst();
        } else fail();
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        keepAliveOrder.add(new WrapperPlayServerKeepAlive(event).getId());
    }
}