package bad.packets.qualitycontrol.check.impl.brandspoof;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

/**
 * Created by am noah
 * BrandSpoofA
 */

@CheckInfo(name = "BrandSpoof", type = "A")
public class BrandSpoofA extends PacketCheck {

    public BrandSpoofA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLUGIN_MESSAGE);
    }

    private boolean sentNonVanilla = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

        if (checkForNonVanilla(data.getPlayer(), wrapper.getChannelName())) sentNonVanilla = true;

        // The vanilla client cannot send any non-vanilla payloads, kinda a no-brainer.
        if (data.getClientBrand().equals("vanilla") && sentNonVanilla) fail();
    }

    /**
     * Outsource the work for better looking code.
     */
    private boolean checkForNonVanilla(User user, String channelName) {
        if (user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_13)) {
            return !channelName.startsWith("minecraft:");
        } else  {
            return !channelName.startsWith("MC|");
        }
    }
}