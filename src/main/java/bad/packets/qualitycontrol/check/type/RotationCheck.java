package bad.packets.qualitycontrol.check.type;

import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class RotationCheck extends Check {
    public RotationCheck(final QualityControlPlayer data) {
        super(data);
    }

    public int ticksSinceBoat = 0;

    /**
     * Universal handling for Packet Receive on PacketOrder checks.
     * @param event PacketEvents Receive Packet event.
     */
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            if (!wrapper.hasRotationChanged()) {
                universalReset();
                return;
            }

            ticksSinceBoat = Math.min(ticksSinceBoat + 1, 5);
            handleRotation(wrapper);
        } else if (event.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {
            ticksSinceBoat = 0;
            universalReset();
        }
    }

    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {

    }

    /**
     * If data might be inaccurate in all extending checks use this to reset it all.
     */
    public void universalReset() {

    }
}
