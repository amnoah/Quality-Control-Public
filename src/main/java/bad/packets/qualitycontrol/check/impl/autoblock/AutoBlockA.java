package bad.packets.qualitycontrol.check.impl.autoblock;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

/**
 * Created by am noah
 * AutoBlockA
 */

@CheckInfo(name = "AutoBlock", type = "A")
public class AutoBlockA extends PacketCheck {

    public AutoBlockA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
    }

    private boolean placing = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        switch (event.getPacketType()) {
            //Keep track of whether the player sent an initial block place packet.
            case PLAYER_BLOCK_PLACEMENT:
                placing = true;

                break;
            case PLAYER_DIGGING:
                WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

                /*
                 * In 1.8 and below Mojang decided to go with an interesting approach to using an item:
                 * First, the client initiates it by sending a block place packet.
                 * Second, the client releases it by sending a block dig packet.
                 *
                 * This means that between all release use item digs there must be a block place.
                 */
                if (wrapper.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                    if (!placing) fail();

                    placing = false;
                }

                break;
        }
    }
}