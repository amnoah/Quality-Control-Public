package bad.packets.qualitycontrol.check.impl.crasher;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

/**
 * Created by am noah
 * CrasherF
 */

@CheckInfo(name = "Crasher", type = "F")
public class CrasherF extends PacketCheck {

    public CrasherF(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_SETTINGS);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // This setting has existed in Minecraft but is only used in 1.19+
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) {
            data.getChecks().remove(this);
            return;
        }

        WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);

        // This should always be disabled as it's unused.
        if (wrapper.isTextFilteringEnabled()) fail();
    }
}