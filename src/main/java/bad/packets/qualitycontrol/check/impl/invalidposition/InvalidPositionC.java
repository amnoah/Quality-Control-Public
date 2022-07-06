package bad.packets.qualitycontrol.check.impl.invalidposition;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidPositionC
 */

@CheckInfo(name = "InvalidPosition", type = "C")
public class InvalidPositionC extends PacketCheck {

    public InvalidPositionC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.STEER_VEHICLE);
    }

    private int flyingPackets = 0;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            // Reset our counter on scenarios which may be invalid.
            if (wrapper.hasPositionChanged() ||
                    data.getPositionProcessor().getTicksSinceTeleport() <= 2) {
                flyingPackets = 0;
            } else {

                // We cannot have more than 20 flying packets without a position packet.
                flyingPackets++;

                if (flyingPackets > 20) fail();
            }
        } else {
            flyingPackets = 0;
        }
    }
}
