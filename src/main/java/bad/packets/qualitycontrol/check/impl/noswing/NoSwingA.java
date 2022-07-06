package bad.packets.qualitycontrol.check.impl.noswing;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

/**
 * Created by Eths
 * NoSwingA
 */

@CheckInfo(name = "NoSwing", type = "A")
public class NoSwingA extends PacketCheck {

    public NoSwingA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ANIMATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
    }

    private boolean swung = true;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {

        /*
         * Yes, you can make more precise NoSwing checks... but what's the point?
         * This works for all versions and doesn't false.
         */

        switch (event.getPacketType()) {
            case ANIMATION:
                swung = true;

                break;
            case INTERACT_ENTITY:
                WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
                if (wrapper.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) return;

                if (!swung) fail();

                swung = false;
                break;
        }
    }
}