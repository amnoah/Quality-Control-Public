package bad.packets.qualitycontrol.check.impl.scaffold;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

/**
 * Created by am noah
 * ScaffoldA
 */

@CheckInfo(name = "Scaffold", type = "A")
public class ScaffoldA extends PacketCheck {

    public ScaffoldA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.HELD_ITEM_CHANGE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (!data.getPositionProcessor().isTickingActive()) return;

        // Slot changes must come before placing.
        if (data.getActionProcessor().hasPlaced()) fail();
    }
}