package bad.packets.qualitycontrol.check.impl.autoblock;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

/**
 * Created by am noah
 * AutoBlockD
 */

@CheckInfo(name = "AutoBlock", type = "D")
public class AutoBlockD extends PacketCheck {

    public AutoBlockD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
        listenedPacketsIncoming.add(PacketType.Play.Client.USE_ITEM);
    }

    private boolean blocking = false, attacked = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        /*
         * Basically AutoBlockC but with higher accuracy.
         * Rather than checking for blocking in the same tick as the attack, we actively track blocking over longer periods.
         * However, we don't know if the player is actually blocking. That's why it's important that we only flag if the
         * player released their used item... meaning they were blocking.
         */

        switch (event.getPacketType()) {
            case INTERACT_ENTITY:
                WrapperPlayClientInteractEntity interactWrapper = new WrapperPlayClientInteractEntity(event);
                if (interactWrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

                attacked = true;

                break;
            // 1.8- players use BLOCK_PLACE to initiate blocking while 1.9+ users use USE_ITEM to initiate blocking.
            case PLAYER_BLOCK_PLACEMENT:
                if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) return;
            case USE_ITEM:

                blocking = true;
                attacked = false;

                break;
            case PLAYER_DIGGING:
                WrapperPlayClientPlayerDigging diggingWrapper = new WrapperPlayClientPlayerDigging(event);
                if (diggingWrapper.getAction() != DiggingAction.RELEASE_USE_ITEM) return;

                // Only check for blocking and attacking on release to ensure no falses.

                if (blocking && attacked) fail();
                blocking = attacked = false;

                break;
        }
    }
}