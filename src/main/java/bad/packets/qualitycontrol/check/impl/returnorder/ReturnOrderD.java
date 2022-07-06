package bad.packets.qualitycontrol.check.impl.returnorder;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientResourcePackStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerResourcePackSend;

import java.util.ArrayDeque;

/**
 * Created by am noah
 * ReturnOrderD
 */

@CheckInfo(name = "ReturnOrder", type = "D")
public class ReturnOrderD extends PacketCheck {

    public ReturnOrderD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.RESOURCE_PACK_STATUS);

        listenedPacketsOutgoing.add(PacketType.Play.Server.RESOURCE_PACK_SEND);
    }

    private final ArrayDeque<String> resourceOrder = new ArrayDeque<>();

    /*
     * Checks for wrongful return order of Resource Pack Status packets.
     */

    /**
     * NOTE: This does flag most PvP clients.
     * In order to block file access they cancel the packet entirely... screwing up the order of returned packets.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ||
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        if (resourceOrder.getFirst().equals(new WrapperPlayClientResourcePackStatus(event).getHash())) {
            resourceOrder.removeFirst();
        } else fail();
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ||
                PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        resourceOrder.add(new WrapperPlayServerResourcePackSend(event).getHash());
    }
}