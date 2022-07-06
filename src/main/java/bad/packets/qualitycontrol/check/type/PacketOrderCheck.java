package bad.packets.qualitycontrol.check.type;

import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

public class PacketOrderCheck extends Check {
    public PacketOrderCheck(final QualityControlPlayer data) {
        super(data);
    }

    private boolean sentFlying = false;

    /**
     * Universal handling for Packet Receive on PacketOrder checks.
     * @param event PacketEvents Receive Packet event.
     */
    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            handleFlying();
            sentFlying = true;
        } else {
            switch (event.getPacketType()) {
                case WINDOW_CONFIRMATION:
                case PONG:
                    handleTransaction(this.sentFlying);
                    sentFlying = false;

                    break;
                default:
                    handlePacket(event.getPacketType(), this.sentFlying, event);
            }
        }
    }

    /**
     * Run all flying packet related sections of all extending checks.
     */
    public void handleFlying() {

    }

    /**
     * Run all transaction packet related sections of all extending checks.
     */
    public void handleTransaction(boolean sentFlying) {

    }

    /**
     * Run all other packet related sections of all extending checks.
     */
    public void handlePacket(PacketType.Play.Client packetType, boolean sentFlying, PacketPlayReceiveEvent event) {

    }
}
