package bad.packets.qualitycontrol.check.type;

import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class MovementCheck extends Check {
    public MovementCheck(final QualityControlPlayer data) {
        super(data);
    }

    /**
     * Universal handling for Packet Receive on PacketOrder checks.
     * @param event PacketEvents Receive Packet event.
     */
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

            if (!wrapper.hasPositionChanged()) {
                universalReset();
                return;
            }

            handlePosition(wrapper);
        }
    }

    public void handlePosition(WrapperPlayClientPlayerFlying wrapper) {

    }

    /**
     * If data might be inaccurate in all extending checks use this to reset it all.
     */
    public void universalReset() {

    }
}
