package bad.packets.qualitycontrol.check.impl.inventory;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InventoryA
 */

@CheckInfo(name = "Inventory", type = "A")
public class InventoryA extends PacketCheck {

    public InventoryA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_STATUS);
        listenedPacketsIncoming.add(PacketType.Play.Client.CLOSE_WINDOW);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    private boolean openInventory = false, closeInventory = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // 1.9+ doesn't inform the server if they enter their inventory.
        if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            // You cannot open your inventory and close it/open your inventory and click in it during the same tick.
            if (openInventory && (closeInventory || data.getActionProcessor().isClickingWindow())) fail();

            openInventory = closeInventory = false;
        } else {
            switch (event.getPacketType()) {
                case CLIENT_STATUS:
                    WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);

                    if (wrapper.getAction() != WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT) return;
                    openInventory = true;

                    break;
                case CLOSE_WINDOW:
                    closeInventory = true;

                    break;
            }
        }
    }
}