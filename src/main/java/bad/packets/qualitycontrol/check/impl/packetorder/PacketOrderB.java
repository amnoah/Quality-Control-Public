package bad.packets.qualitycontrol.check.impl.packetorder;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketOrderCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

/**
 * Created by am noah
 * PacketOrderB
 */

@CheckInfo(name = "PacketOrder", type = "B")
public class PacketOrderB extends PacketOrderCheck {

    public PacketOrderB(final QualityControlPlayer data) {
        super(data);
    }

    @Override
    public void handlePacket(PacketType.Play.Client packetType, boolean sentFlying, PacketPlayReceiveEvent event) {
        if (!data.getPositionProcessor().isTickingActive()) return;

        // Sneaking Actions are sent pre in 1.8.
        // Steer Vehicles are always sent pre.

        switch (event.getPacketType()) {
            case ENTITY_ACTION:
                WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);

                if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_8)) return;
                if (!wrapper.getAction().equals(WrapperPlayClientEntityAction.Action.STOP_SNEAKING) &&
                        !wrapper.getAction().equals(WrapperPlayClientEntityAction.Action.START_SNEAKING)) return;
            case STEER_VEHICLE:
                if (!sentFlying) fail();
        }
    }
}
