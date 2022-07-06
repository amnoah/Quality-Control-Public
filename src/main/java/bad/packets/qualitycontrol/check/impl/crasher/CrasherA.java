package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;

/**
 * Created by am noah
 * CrasherA
 */

@CheckInfo(name = "Crasher", type = "A")
public class CrasherA extends PacketCheck {

    public CrasherA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.UPDATE_SIGN);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);

        /*
         * As far as I can tell the maximum length is 45, but just in case there are any smaller characters than a
         * period which can go on signs I went with 50.
         */
        for (String line : wrapper.getTextLines()) {
            if (line.length() > 50) fail();
        }
    }
}