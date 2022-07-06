package bad.packets.qualitycontrol.check.impl.chat;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTabComplete;

/**
 * Created by am noah
 * ChatB
 */

@CheckInfo(name = "Chat", type = "B")
public class ChatB extends PacketCheck {

    public ChatB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.TAB_COMPLETE);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientTabComplete wrapper = new WrapperPlayClientTabComplete(event);

        // To tab complete you must have some text.
        if (wrapper.getText().isEmpty()) fail();
    }
}