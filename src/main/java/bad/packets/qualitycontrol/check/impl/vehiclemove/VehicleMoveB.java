package bad.packets.qualitycontrol.check.impl.vehiclemove;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

/**
 * Created by am noah
 * VehicleMoveB
 */

@CheckInfo(name = "VehicleMove", type = "B")
public class VehicleMoveB extends PacketCheck {

    public VehicleMoveB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.STEER_BOAT);
        listenedPacketsIncoming.add(PacketType.Play.Client.STEER_VEHICLE);
    }

    private boolean sentSteerBoat = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        // For every STEER BOAT packet there must be a previous STEER VEHICLE packet.

        switch (event.getPacketType()) {
            case STEER_BOAT:
                if (sentSteerBoat) fail();

                sentSteerBoat = true;
                break;
            case STEER_VEHICLE:
                if (!sentSteerBoat) fail();

                sentSteerBoat = false;
                break;
        }
    }
}