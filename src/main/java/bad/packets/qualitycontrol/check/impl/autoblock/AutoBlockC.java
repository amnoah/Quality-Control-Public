package bad.packets.qualitycontrol.check.impl.autoblock;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;

/**
 * Created by am noah
 * AutoBlockC
 */

@CheckInfo(name = "AutoBlock", type = "C")
public class AutoBlockC extends PacketCheck {

    public AutoBlockC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.INTERACT_ENTITY);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (!data.getPositionProcessor().isTickingActive()) return;

        WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);
        if (!wrapper.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) return;

        // You cannot interact with an item while attacking.
        if (data.getActionProcessor().hasDug() ||
                data.getActionProcessor().hasPlaced() ||
                data.getActionProcessor().hasUsedItem()) fail();
    }
}