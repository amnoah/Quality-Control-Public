package bad.packets.qualitycontrol.check.impl.noswing;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * NoSwingB
 */

@CheckInfo(name = "NoSwing", type = "B")
public class NoSwingB extends PacketCheck {

    public NoSwingB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ANIMATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean started = false;
    private boolean swungThisTick = false;
    private boolean constantSwinging = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {

        /*
         * Digging NoSwing check.
         * Works really well... as long as the player is sending flying packets actively.
         */

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // Stop tracking if the client fails to send flying packets actively.
            if (!data.getPositionProcessor().isTickingActive()) started = false;

            // Track if the player has been constantly swinging.
            if (started && !swungThisTick) constantSwinging = false;

            swungThisTick = false;
        } else {
            switch (event.getPacketType()) {
                case ANIMATION:

                    if (started) swungThisTick = true;

                    break;
                case PLAYER_DIGGING:
                    WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

                    // Switches > if statements.
                    switch (wrapper.getAction()) {
                        case START_DIGGING:

                            started = true;
                            constantSwinging = true;

                            break;
                        case FINISHED_DIGGING:
                        case CANCELLED_DIGGING:

                            // If a player reaches the end of digging they must have been actively swinging.
                            if (started && !constantSwinging) fail();
                            started = false;

                            break;
                    }

                    break;
            }
        }
    }
}