package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

/**
 * Created by am noah
 * CrasherA
 */

@CheckInfo(name = "Crasher", type = "A")
public class CrasherA extends PacketCheck {

    public CrasherA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLUGIN_MESSAGE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
        final int length = wrapper.getChannelName().length();

        /* Of course it shouldn't be empty, and according to Dinnerbone's blog it should be a maximum of 16 characters.
         * https://dinnerbone.com/blog/2012/01/13/minecraft-plugin-channels-messaging/
         *
         * This may have changed... I have no idea.
         */
        if (length == 0 || length > 16) fail();
    }
}