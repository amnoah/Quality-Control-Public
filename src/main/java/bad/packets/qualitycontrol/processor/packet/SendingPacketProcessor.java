package bad.packets.qualitycontrol.processor.packet;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import lombok.experimental.UtilityClass;

/**
 * Because we don't utilize Bukkit events we use raw packets.
 * This is where we go through those packets and update the data in our processors with the data in packets.
 * At the bottom, we run all checks as they may utilize the packet themself.
 * This processor specifically is for outgoing packets, or packets the server is Sending.
 */

@UtilityClass
public final class SendingPacketProcessor {
    public void process(final PacketPlaySendEvent event, final QualityControlPlayer data) {
        switch (event.getPacketType()) {
            case CLOSE_WINDOW:

                data.getActionProcessor().handleCloseWindow();
                break;
            case KEEP_ALIVE:
                final WrapperPlayServerKeepAlive keepAliveWrapper = new WrapperPlayServerKeepAlive(event);

                data.getKeepAliveProcessor().handleSendKeepAlive(keepAliveWrapper.getId());
                break;
            case PING:
                final WrapperPlayServerPing pingWrapper = new WrapperPlayServerPing(event);

                data.getTransactionProcessor().listenSendPing(pingWrapper.getId());
                break;
            case PLAYER_POSITION_AND_LOOK:
                final WrapperPlayServerPlayerPositionAndLook teleportWrapper = new WrapperPlayServerPlayerPositionAndLook(event);

                data.getTeleportTracker().handleTeleport(teleportWrapper);
                break;
            case RESOURCE_PACK_SEND:
                final WrapperPlayServerResourcePackSend resourceWrapper = new WrapperPlayServerResourcePackSend(event);

                data.getFileCheckTest().listenSendResourceStatus(resourceWrapper);
                break;
            case WINDOW_CONFIRMATION:
                final WrapperPlayServerWindowConfirmation transactionWrapper = new WrapperPlayServerWindowConfirmation(event);

                data.getTransactionProcessor().listenSendTransaction(transactionWrapper);
                break;
            default:
                break;
        }

        long startTime = 0;

        data.runChecksSend(event);
    }
}
