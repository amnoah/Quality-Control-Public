package bad.packets.qualitycontrol.check.impl.skinblinker;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.SkinSection;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

import java.util.Set;

/**
 * Created by am noah
 * SkinBlinkerA
 */

@CheckInfo(name = "SkinBlinker", type = "A")
public class SkinBlinkerA extends PacketCheck {

    public SkinBlinkerA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_SETTINGS);
    }

    private SkinSection lastSkin = null;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);

        if (lastSkin != null) {
            if ((data.getActionProcessor().isSprinting() && !data.getActionProcessor().isSprintingQuestionable()) ||
                    (data.getActionProcessor().isSneaking() && !data.getActionProcessor().isSneakingQuestionable())) {

                // A player cannot change their skin layers while sprinting/sneaking.
                if (wrapper.getVisibleSkinSection().equals(lastSkin)) fail();
            }
        }

        lastSkin = wrapper.getVisibleSkinSection();
    }
}