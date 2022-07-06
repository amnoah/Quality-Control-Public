package bad.packets.qualitycontrol.processor.packet;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import lombok.experimental.UtilityClass;

/**
 * Because we don't utilize Bukkit events we use raw packets.
 * This is where we go through those packets and update the data in our processors with the data in packets.
 * At the bottom, we run all checks as they may utilize the packet themself.
 * This processor specifically is for incoming packets, or packets the server is Receiving.
 */

@UtilityClass
public final class ReceivingPacketProcessor {
    public void process(final PacketPlayReceiveEvent event, final QualityControlPlayer data) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            final WrapperPlayClientPlayerFlying wrapperPlayClientPlayerFlying = new WrapperPlayClientPlayerFlying(event);

            data.getPositionProcessor().handleFlyingPacket(wrapperPlayClientPlayerFlying);
            data.getActionProcessor().handleFlying();
        } else {
            switch (event.getPacketType()) {
                case CLICK_WINDOW:

                    data.getActionProcessor().handleWindowClick();
                    break;
                case CLIENT_STATUS:
                    final WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);

                    data.getActionProcessor().handleClientCommand(wrapper);
                    break;
                case CLOSE_WINDOW:

                    data.getActionProcessor().handleCloseWindow();
                    break;
                case ENTITY_ACTION:
                    final WrapperPlayClientEntityAction wrappedPacketInEntityAction = new WrapperPlayClientEntityAction(event);

                    data.getActionProcessor().handleEntityAction(wrappedPacketInEntityAction);
                    break;
                case INTERACT_ENTITY:
                    final WrapperPlayClientInteractEntity wrapperPlayClientInteractEntity = new WrapperPlayClientInteractEntity(event);

                    if (wrapperPlayClientInteractEntity.getAction().equals(WrapperPlayClientInteractEntity.InteractAction.ATTACK)) data.getCombatProcessor().handleUseEntity();
                    break;
                case KEEP_ALIVE:
                    final WrapperPlayClientKeepAlive wrappedPacketInKeepAlive = new WrapperPlayClientKeepAlive(event);

                    data.getKeepAliveProcessor().handleReceiveKeepAlive(wrappedPacketInKeepAlive.getId());
                    break;
                case PLAYER_BLOCK_PLACEMENT:

                    data.getActionProcessor().handleBlockPlace();
                    break;
                case PLAYER_DIGGING:

                    data.getActionProcessor().handleBlockDig();
                    break;
                case PLUGIN_MESSAGE:
                    final WrapperPlayClientPluginMessage pluginMessageWrapper = new WrapperPlayClientPluginMessage(event);

                    data.getPayloadProcessor().handlePayload(pluginMessageWrapper);
                    break;
                case PONG:
                    final WrapperPlayClientPong pongWrapper = new WrapperPlayClientPong(event);

                    //THIS ORDER IS REQUIRED.
                    data.getTransactionProcessor().listenReceiveTransaction(pongWrapper.getId());
                    data.getPositionProcessor().checkTickingTransaction();
                    break;
                case RESOURCE_PACK_STATUS:
                    final WrapperPlayClientResourcePackStatus resourceWrapper = new WrapperPlayClientResourcePackStatus(event);

                    data.getFileCheckTest().listenReceiveResourceStatus(resourceWrapper);
                    break;
                case USE_ITEM:

                    data.getActionProcessor().handleUseItem();
                    break;
                case WINDOW_CONFIRMATION:
                    final WrapperPlayClientWindowConfirmation wrappedPacketInTransaction = new WrapperPlayClientWindowConfirmation(event);

                    //THIS ORDER IS REQUIRED.
                    data.getTransactionProcessor().listenReceiveTransaction(wrappedPacketInTransaction.getActionId());
                    data.getPositionProcessor().checkTickingTransaction();
                    break;
                default:
                    break;
            }
        }

        long startTime = 0;

        if (data.getProfiling()) {
            startTime = System.nanoTime();
        }

        data.runChecksReceive(event);

        if (data.getProfiling()) {
            double nanoSecondDif = System.nanoTime() - startTime;

            data.getPlayer().sendMessage("RECEIVE Run Time:" + nanoSecondDif + "ns, "
                    + (nanoSecondDif / 1000000D) + "ms");
        }
    }
}
