package bad.packets.qualitycontrol.check.impl.fastbreak;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

/**
 * Created by am noah
 * FastBreakB
 */

@CheckInfo(name = "FastBreak", type = "B")
public class FastBreakB extends PacketCheck {

    public FastBreakB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
    }

    private boolean started = true, lastCanceled = false;

    /**
     * Minecraft works very iffy regarding insta break blocks. If you break an insta break block, you send a
     * Start Destroy Block but never a Stop Destroy Block. This means you can have 2 starts in a row. However,
     * Stop Destroy Block requires there to have been a Start Destroy Block sent previously. This means that if you
     * end twice with no start in between you're cheating.
     *
     * This also accounts for an extremely niche piece of weird net code that Nekroses found to false the check.
     * If a client is teleported while digging, it will send a Cancel. However, if it moves the client slightly
     * back in a position where it can still finish the digging it will send the Finish. This means you can
     * technically end twice, but it must go in the order of Start -> Cancelled -> Finished to do so.
     *
     * To account for this if the second ending is a Finish the previous must not be a Cancelled. If the second
     * ending is a Cancelled it doesn't matter what the previous is.
     *
     * Link to Nekroses (as a thank you): https://www.youtube.com/channel/UCyyX_xSdXDcKGDJCY4JqCrA
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

        switch (wrapper.getAction()) {
            case START_DIGGING:
                started = true;
                lastCanceled = false;

                break;
            case CANCELLED_DIGGING:
                if (!started) fail();

                started = false;
                lastCanceled = true;

                break;
            case FINISHED_DIGGING:
                if (!started && !lastCanceled) {
                    fail();
                }

                started = lastCanceled = false;

                break;
        }
    }
}