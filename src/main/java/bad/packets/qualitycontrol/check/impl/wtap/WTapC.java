package bad.packets.qualitycontrol.check.impl.wtap;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

/**
 * Created by am noah
 * WTapC
 */

@CheckInfo(name = "WTap", type = "C")
public class WTapC extends PacketCheck {

    public WTapC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ENTITY_ACTION);
    }

    // We track independently of the ActionProcessor because it will have updated by the time this check runs.
    private boolean lastSprinting = true, lastSneaking = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);

        switch (wrapper.getAction()) {
            case START_SPRINTING:
                //If the player starts, they have to have been previously not sprinting.
                if (lastSprinting && !data.getActionProcessor().isLastSprintingQuestionable()) {
                    fail();
                }
                lastSprinting = true;
                break;

            case STOP_SPRINTING:
                //If the player stops, they have to have been previously sprinting.
                if (!lastSprinting && !data.getActionProcessor().isLastSprintingQuestionable()) {
                    fail();
                }
                lastSprinting = false;
                break;

            case START_SNEAKING:
                //If the player starts, they have to have been previously not sneaking.
                if (lastSneaking && !data.getActionProcessor().isLastSneakingQuestionable()) {
                    fail();
                }
                lastSneaking = true;
                break;

            case STOP_SNEAKING:
                //If the player stops, they have to have been previously sneaking.
                if (!lastSneaking && !data.getActionProcessor().isLastSneakingQuestionable()) {
                    fail();
                }
                lastSneaking = false;
                break;
        }
    }
}