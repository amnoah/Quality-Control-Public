package bad.packets.qualitycontrol.check.impl.invalidposition;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidPositionB
 */

@CheckInfo(name = "InvalidPosition", type = "B")
public class InvalidPositionB extends PacketCheck {

    public InvalidPositionB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // Only run for 1.9+ users.
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);
        if (wrapper.hasPositionChanged() || wrapper.hasRotationChanged() || data.getPositionProcessor().isTeleporting()) return;

        // For a 1.9+ client to send a regular flying packet there must be a onGround status change.
        if (wrapper.isOnGround() == data.getPositionProcessor().isLastPacketGround()) fail();
    }
}
