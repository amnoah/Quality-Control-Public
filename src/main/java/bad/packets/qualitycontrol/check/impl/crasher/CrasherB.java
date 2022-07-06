package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

/**
 * Created by am noah
 * CrasherB
 */

@CheckInfo(name = "Crasher", type = "B")
public class CrasherB extends PacketCheck {

    public CrasherB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLUGIN_MESSAGE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
        final int length = wrapper.getData().length;

        // Verus value.
        if (length > 15000) fail();
    }
}