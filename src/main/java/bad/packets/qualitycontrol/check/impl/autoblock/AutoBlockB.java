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
 * AutoBlockB
 */

@CheckInfo(name = "AutoBlock", type = "B")
public class AutoBlockB extends PacketCheck {

    public AutoBlockB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
        listenedPacketsIncoming.add(PacketType.Play.Client.USE_ITEM);
    }

    private boolean useItem = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        switch (event.getPacketType()) {
            //Keep track of whether the player sent an initial use item packet.
            case USE_ITEM:
                useItem = true;

                break;
            case PLAYER_DIGGING:
                WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

                /*
                 * In 1.9 and above Mojang decided to go with an interesting approach to using an item:
                 * First, the client initiates it by sending a use item packet.
                 * Second, the client releases it by sending a block dig packet.
                 *
                 * This means that between all release use item digs there must be a use item.
                 */
                if (wrapper.getAction() == DiggingAction.RELEASE_USE_ITEM) {
                    if (!useItem) fail();

                    useItem = false;
                }

                break;
        }
    }
}