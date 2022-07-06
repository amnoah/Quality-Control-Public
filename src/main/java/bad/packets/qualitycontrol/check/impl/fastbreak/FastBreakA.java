package bad.packets.qualitycontrol.check.impl.fastbreak;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;

/**
 * Created by am noah
 * FastBreakA
 */

@CheckInfo(name = "FastBreak", type = "A")
public class FastBreakA extends PacketCheck {

    public FastBreakA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
    }

    private Vector3i latestStartPosition = null;
    private boolean hasStarted = false;

    /**
     * This check patches a very interesting bypass mechanic in many cheats.
     *
     * Some clients bypass classic fast break checks by following this process:
     * Start1 -> Start2 -> Finish1 -> Start3 -> Finish2 -> Start4 -> etc
     *
     * It starts digging multiple blocks at the same time, waiting until the proper time has passed to finish any.
     *
     * This also isn't easily detectable by checking if the last action and current action are starts because the
     * vanilla client does that when breaking insta-break blocks.
     *
     * This check ends up being heavily beneficial.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

        switch (wrapper.getAction()) {
            case START_DIGGING:

                latestStartPosition = wrapper.getBlockPosition();
                hasStarted = true;
                break;
            case FINISHED_DIGGING:
                // Patch for weird issue I'm having.
                if (hasStarted) {
                    if (!wrapper.getBlockPosition().equals(latestStartPosition)) fail();
                }

                break;
        }
    }
}