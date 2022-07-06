package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

/**
 * Created by am noah
 * CrasherC
 */

@CheckInfo(name = "Crasher", type = "C")
public class CrasherC extends PacketCheck {

    public CrasherC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_SETTINGS);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);

        final int localeLength = wrapper.getLocale().length();

        // The Locale must be between 4 and 6 characters.
        if (localeLength < 4 || localeLength > 6) fail();
    }
}