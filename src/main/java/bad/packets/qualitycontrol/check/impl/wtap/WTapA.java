package bad.packets.qualitycontrol.check.impl.wtap;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * WTapA
 */

@CheckInfo(name = "WTap", type = "A")
public class WTapA extends PacketCheck {

    public WTapA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ENTITY_ACTION);
        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean sentEntityAction = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            sentEntityAction = false;
        } else {
            switch (event.getPacketType()) {
                case ENTITY_ACTION:
                    sentEntityAction = true;

                    break;
                case INTERACT_ENTITY:
                    // Only run when the player is attacking and when they're actively sending flying packets.
                    WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                    if (!wrapper.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) return;
                    if (!data.getPositionProcessor().isTickingActive()) sentEntityAction = false;

                    // You cannot update an entity action status before you attack.
                    if (sentEntityAction) fail();
            }
        }
    }
}