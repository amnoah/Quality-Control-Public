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
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.ArrayDeque;

/**
 * Created by am noah
 * ReturnOrderC
 */

@CheckInfo(name = "ReturnOrder", type = "C")
public class ReturnOrderC extends PacketCheck {

    public ReturnOrderC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.TELEPORT_CONFIRM);

        listenedPacketsOutgoing.add(PacketType.Play.Server.PLAYER_POSITION_AND_LOOK);
    }

    private final ArrayDeque<Integer> teleportOrder = new ArrayDeque<>();

    /*
     * Checks for wrongful return order of Teleport packets.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9) ||
                PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        if (teleportOrder.getFirst().equals(new WrapperPlayClientTeleportConfirm(event).getTeleportId())) {
            teleportOrder.removeFirst();
        } else fail();
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9) ||
                PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        teleportOrder.add(new WrapperPlayServerPlayerPositionAndLook(event).getTeleportId());
    }
}