package bad.packets.qualitycontrol.check.impl.chat;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;

/**
 * Created by am noah
 * ChatA
 */

@CheckInfo(name = "Chat", type = "A")
public class ChatA extends PacketCheck {

    public ChatA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CHAT_MESSAGE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientChatMessage wrapper = new WrapperPlayClientChatMessage(event);
        String message = wrapper.getMessage();

        // You cannot send empty messages or messages starting with spaces.
        if (message.trim().isEmpty()) fail();
        else if (message.charAt(0) == ' ') fail();
    }
}