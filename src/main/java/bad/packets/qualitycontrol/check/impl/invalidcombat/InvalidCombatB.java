package bad.packets.qualitycontrol.check.impl.invalidcombat;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidCombatB
 */

@CheckInfo(name = "InvalidCombat", type = "B")
public class InvalidCombatB extends PacketCheck {

    public InvalidCombatB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
    }

    private int lastEnemy;
    private boolean active = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        /*
         * How this check works:
         * A player can send multiple use entity packets in a tick, but due to the fact that they will not adjust where
         * they're looking/receive entity positions updates until the next tick they can only hit 1 entity.
         */

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            active = false;
        } else {
            if (!data.getPositionProcessor().isTickingActive()) {
                active = false;
                return;
            }

            WrapperPlayClientInteractEntity interactEntity = new WrapperPlayClientInteractEntity(event);

            final int enemy = interactEntity.getEntityId();

            // Don't allow the check to flag until there is a "lastEnemy" set for this tick. Explanation above.
            if (active && lastEnemy != enemy) {
                fail();
            }

            active = true;
            lastEnemy = enemy;
        }
    }
}