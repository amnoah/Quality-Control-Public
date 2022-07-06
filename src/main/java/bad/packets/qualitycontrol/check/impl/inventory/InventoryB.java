package bad.packets.qualitycontrol.check.impl.inventory;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;

/**
 * Created by am noah
 * InventoryB
 */

@CheckInfo(name = "Inventory", type = "B")
public class InventoryB extends PacketCheck {

    public InventoryB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLICK_WINDOW);
        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_STATUS);
        listenedPacketsIncoming.add(PacketType.Play.Client.CLOSE_WINDOW);
        listenedPacketsOutgoing.add(PacketType.Play.Server.CLOSE_WINDOW);
    }

    private boolean inInventory, confirmed = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // 1.9+ players don't inform us about inventory status.
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        /*
         * Unfortunately, I do not have compensation for random things like this.
         * Luckily, the serverside Close Window is not used often.
         * This means we can run the check any time after there has been a change in inventory status by the client.
         */

        switch (event.getPacketType()) {
            case CLICK_WINDOW:
                WrapperPlayClientClickWindow wrapper = new WrapperPlayClientClickWindow(event);

                if (wrapper.getWindowId() == 0 && !inInventory && confirmed) fail();

                break;
            case CLIENT_STATUS:
                WrapperPlayClientClientStatus inventory = new WrapperPlayClientClientStatus(event);
                if (!inventory.getAction().equals(WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT)) return;

                inInventory = confirmed = true;

                break;
            case CLOSE_WINDOW:
                inInventory = false;
                confirmed = true;

                break;
        }

    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        inInventory = confirmed = false;
    }
}