package bad.packets.qualitycontrol.check.impl.returnorder;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientWindowConfirmation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;

import java.util.ArrayDeque;

/**
 * Created by am noah
 * ReturnOrderB
 */

@CheckInfo(name = "ReturnOrder", type = "B")
public class ReturnOrderB extends PacketCheck {

    public ReturnOrderB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.WINDOW_CONFIRMATION);

        listenedPacketsOutgoing.add(PacketType.Play.Server.WINDOW_CONFIRMATION);
    }

    private final ArrayDeque<Short> transactionOrder = new ArrayDeque<>();

    /*
     * Checks for wrongful return order of Transaction packets.
     */

    /**
     * NOTE: This does flag Lunar Client.
     * This is because Lunar Client is improperly made and cancels all packets in queue when a file access
     * Resource Pack Status packet is sent.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        WrapperPlayClientWindowConfirmation transaction = new WrapperPlayClientWindowConfirmation(event);
        if (transaction.getWindowId() != 0) return;

        if (transactionOrder.getFirst().equals(transaction.getActionId())) {
            transactionOrder.removeFirst();
        } else fail();
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        WrapperPlayServerWindowConfirmation transaction = new WrapperPlayServerWindowConfirmation(event);
        if (transaction.isAccepted() || transaction.getWindowId() != 0) return;

        transactionOrder.add(transaction.getActionId());
    }
}