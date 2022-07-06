package bad.packets.qualitycontrol.check.impl.scaffold;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;

/**
 * Created by am noah
 * ScaffoldC
 */

@CheckInfo(name = "Scaffold", type = "C")
public class ScaffoldC extends PacketCheck {

    public ScaffoldC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);

    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // ViaBackwards moment.
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        WrapperPlayClientPlayerBlockPlacement blockPlacement = new WrapperPlayClientPlayerBlockPlacement(event);

        final float x = blockPlacement.getCursorPosition().getX(),
                y = blockPlacement.getCursorPosition().getY(),
                z = blockPlacement.getCursorPosition().getZ();

        /*
         * The minimum value for a cursor pos is 0 and the maximum value is 1. Vanilla clients cannot breach this.
         */
        for (float f : new float[]{x,y,z}) {
            if (f > 1f || f < 0f) fail();
        }
    }
}