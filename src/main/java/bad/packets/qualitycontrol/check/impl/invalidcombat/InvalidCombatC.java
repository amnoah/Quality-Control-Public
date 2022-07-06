package bad.packets.qualitycontrol.check.impl.invalidcombat;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

/**
 * Created by am noah
 * InvalidCombatC
 */

@CheckInfo(name = "InvalidCombat", type = "C")
public class InvalidCombatC extends PacketCheck {

    public InvalidCombatC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        final int entityID = wrapper.getEntityId();

        //  You cannot hit yourself.
        if (entityID == data.getEntityID()) fail();
    }
}