package bad.packets.qualitycontrol.check.impl.macro;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

/**
 * Created by am noah
 * MacroA
 */

@CheckInfo(name = "Macro", type = "A")
public class MacroA extends PacketCheck {

    public MacroA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CHAT_MESSAGE);
    }

    /**
     * This flags Badlion Client and Lunar Client, but that is not a false flag.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // You cannot chat and sprint/sneak.

        if ((data.getActionProcessor().isSneaking() && !data.getActionProcessor().isSneakingQuestionable()) ||
                (data.getActionProcessor().isSprinting() && !data.getActionProcessor().isSprintingQuestionable()))
            fail();
    }
}