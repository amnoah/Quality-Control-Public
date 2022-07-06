package bad.packets.qualitycontrol.check.impl.noslow;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * NoSlowA
 */

@CheckInfo(name = "NoSlow", type = "A")
public class NoSlowA extends PacketCheck {

    public NoSlowA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
    }

    private boolean blocking = false;
    private boolean cheating = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        /*
         * I haven't found any public NoSlow checks which use this method yet.
         * By design it's fully lag compensated and fully Bukkit independent.
         */

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // If the player is "blocking" and sprinting then they're cheating.

            if (blocking && data.getActionProcessor().isSprinting() && !data.getActionProcessor().isSprintingQuestionable()) cheating = true;
        } else {
            switch (event.getPacketType()) {
                case PLAYER_BLOCK_PLACEMENT:
                    if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) return;
                case USE_ITEM:

                    blocking = true;
                    cheating = false;

                    break;
                case PLAYER_DIGGING:
                    WrapperPlayClientPlayerDigging diggingWrapper = new WrapperPlayClientPlayerDigging(event);
                    if (diggingWrapper.getAction() != DiggingAction.RELEASE_USE_ITEM) return;

                    // Only flag on use item release as previously we don't know if the player is actually blocking.

                    if (blocking && cheating) fail();
                    blocking = cheating = false;

                    break;
            }
        }
    }
}