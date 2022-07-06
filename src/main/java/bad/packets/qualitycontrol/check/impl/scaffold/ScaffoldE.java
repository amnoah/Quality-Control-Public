package bad.packets.qualitycontrol.check.impl.scaffold;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * ScaffoldE
 */

@CheckInfo(name = "Scaffold", type = "E")
public class ScaffoldE extends PacketCheck {

    public ScaffoldE(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.HELD_ITEM_CHANGE);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean sentChangeItem = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // 1.9+ clients cannot send 2 slot changes in a tick (but 1.8 clients can, cause why not?)

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
                data.getChecks().remove(this);
                return;
            }

            sentChangeItem = false;
        } else {
            if (!data.getPositionProcessor().isTickingActive()) {
                sentChangeItem = false;
                return;
            }

            if (sentChangeItem) fail();

            sentChangeItem = true;
        }
    }
}