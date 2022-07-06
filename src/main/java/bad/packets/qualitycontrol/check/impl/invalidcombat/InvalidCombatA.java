package bad.packets.qualitycontrol.check.impl.invalidcombat;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidCombatA
 */

@CheckInfo(name = "InvalidCombat", type = "A")
public class InvalidCombatA extends PacketCheck {

    public InvalidCombatA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
        listenedPacketsIncoming.add(PacketType.Play.Client.HELD_ITEM_CHANGE);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean sentSlotChange = false, sentAttack = false, itemChangeBeforeAttack = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        /*
         * Basic PacketOrder check.
         * Slot changes must be sent after Interact Entity packets, otherwise it's being sent too early.
         */

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (sentSlotChange && sentAttack && data.getPositionProcessor().isTickingActive()) {
                if (!itemChangeBeforeAttack) fail();
            }

            sentAttack = sentSlotChange = itemChangeBeforeAttack = false;
        } else {
            switch (event.getPacketType()) {
                case HELD_ITEM_CHANGE:
                    if (!sentAttack) itemChangeBeforeAttack = true;

                    sentSlotChange = true;
                    break;
                case INTERACT_ENTITY:
                    sentAttack = true;
                    break;
            }
        }
    }
}