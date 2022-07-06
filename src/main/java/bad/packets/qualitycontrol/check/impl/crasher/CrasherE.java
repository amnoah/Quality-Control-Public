package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

/**
 * Created by am noah
 * CrasherE
 */

@CheckInfo(name = "Crasher", type = "E")
public class CrasherE extends PacketCheck {

    public CrasherE(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_SETTINGS);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);

        // The minimum view distance a vanilla player can send is 2.
        if (wrapper.getViewDistance() < 2) fail();
    }
}