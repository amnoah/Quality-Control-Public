package bad.packets.qualitycontrol.check.impl.wtap;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * WTapB
 */

@CheckInfo(name = "WTap", type = "B")
public class WTapB extends PacketCheck {

    public WTapB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ENTITY_ACTION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean sprintChanged = false, sneakChanged = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sprintChanged = sneakChanged = false;
        } else {
            // Only check when the client is actively sending flying packets.
            if (!data.getPositionProcessor().isTickingActive()) {
                sprintChanged = sneakChanged = false;
                return;
            }

            WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);

            switch (wrapper.getAction()) {
                case STOP_SNEAKING:
                case START_SNEAKING:
                    //We should not get 2 sneaking changes in a row with no flying in between.
                    if (sneakChanged) fail();

                    sneakChanged = true;
                    break;
                case STOP_SPRINTING:
                case START_SPRINTING:
                    //We should not get 2 sprinting changes in a row with no flying in between.
                    if (sprintChanged) fail();

                    sprintChanged = true;
                    break;
            }
        }
    }
}