package bad.packets.qualitycontrol.check.impl.invalidposition;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

/**
 * Created by am noah
 * InvalidPositionD
 */

@CheckInfo(name = "InvalidPosition", type = "D")
public class InvalidPositionD extends PacketCheck {

    public InvalidPositionD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private int samePositions = 0, flyings = 0;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // Mojang added this as a feature in 1.17+.
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_17)) {
            data.getChecks().remove(this);
            return;
        }

        /*
         * The client should do this once a second (every 20 flying packets).
         * Teleporting overrides this.
         * There's weird behavior on logins that I don't feel like dealing with.
         * 1.9+ clients may not be actively sending flying packets.
         */

        if (++flyings > 20 || data.getPositionProcessor().getTicksSinceTeleport() <= 3 || !data.isSafeToRun() ||
                !data.getPositionProcessor().isTickingActive()) {

            // Reset everything.
            flyings = 0;
            samePositions = 0;
            return;
        }

        if (!data.getPositionProcessor().hasPositionChanged()) return;

        final double deltaX = Math.abs(data.getPositionProcessor().getDeltaX());
        final double deltaY = Math.abs(data.getPositionProcessor().getDeltaY());
        final double deltaZ = Math.abs(data.getPositionProcessor().getDeltaZ());

        // From MCP.
        if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ <= 9.0E-4D) {

            /*
             * Every 20 flying packets the client sends a position packet regardless of if it moved or not. Due to
             * this, we check if theres been more than 1 less than 9.0E-4D position change since the last reset.
             */

            if (++samePositions > 1) fail();
        }
    }
}
