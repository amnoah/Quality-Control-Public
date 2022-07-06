package bad.packets.qualitycontrol.check.impl.brandspoof;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

/**
 * Created by am noah
 * BrandSpoofB
 */

@CheckInfo(name = "BrandSpoof", type = "B")
public class BrandSpoofB extends PacketCheck {

    public BrandSpoofB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLUGIN_MESSAGE);
    }

    /**
     * Note: This check is not based off of confirmed behavior, rather commonplace.
     * Every legitimate client (that I'm aware of) has their client brand entirely in lowercase.
     * ie: vanilla, lunarclient:34ba433
     * This can catch some bad clients that put things like Vanilla or Lunar-Client.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

        String channel = wrapper.getChannelName();
        if (!channel.equals("MC|Brand") && !channel.equals("minecraft:brand")) return;

        String brandLowercase = data.getClientBrand().toLowerCase();
        if (!data.getClientBrand().equals(brandLowercase) || data.getClientBrand().equals("null")) fail();
    }
}