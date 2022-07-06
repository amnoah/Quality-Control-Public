package bad.packets.qualitycontrol.check.impl.macro;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;

/**
 * Created by am noah
 * MacroB
 */

@CheckInfo(name = "Macro", type = "B")
public class MacroB extends PacketCheck {

    public MacroB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_SETTINGS);
        listenedPacketsIncoming.add(PacketType.Play.Client.CHAT_MESSAGE);
    }

    private boolean canChat = true;

    /**
     * This flags Lunar Client, but that is not a false flag.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);

            /*
             * You can only chat while your chat is visible.
             * It is impossible to chat with a vanilla client when your chat is Hidden.
             */

            switch (wrapper.getVisibility()) {
                case FULL:
                case SYSTEM:
                    canChat = true;

                    break;
                case HIDDEN:
                    canChat = false;

                    break;
            }
        } else {
            if (!canChat) fail();
        }
    }
}