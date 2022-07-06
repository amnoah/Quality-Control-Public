package bad.packets.qualitycontrol.check.impl.inventory;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

/**
 * Created by am noah
 * InventoryC
 */

@CheckInfo(name = "Inventory", type = "C")
public class InventoryC extends PacketCheck {

    public InventoryC(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_STATUS);
        listenedPacketsIncoming.add(PacketType.Play.Client.CLOSE_WINDOW);
        listenedPacketsIncoming.add(PacketType.Play.Client.ENTITY_ACTION);
    }

    private boolean sentAction = false, inInventory = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        switch (event.getPacketType()) {
            case CLIENT_STATUS:
                WrapperPlayClientClientStatus clientStatus = new WrapperPlayClientClientStatus(event);

                if (clientStatus.getAction().equals(WrapperPlayClientClientStatus.Action.OPEN_INVENTORY_ACHIEVEMENT)) {
                    sentAction = false;
                    inInventory = true;
                }

                break;
            case CLOSE_WINDOW:
                WrapperPlayClientCloseWindow closeWindow = new WrapperPlayClientCloseWindow(event);

                /*
                 * By checking on close window we can avoid bad 1.8 net code.
                 * 1.8 users can close their inventory without telling the server using the F6 menu, but if we only
                 * check when they tell the server they exited we can avoid this.
                 */
                if (closeWindow.getWindowId() == 0 && sentAction) fail();

                inInventory = false;

                break;
            case ENTITY_ACTION:
                WrapperPlayClientEntityAction actionWrapper = new WrapperPlayClientEntityAction(event);

                // I prefer the look of this, no actual functionality difference from an if statement.
                switch (actionWrapper.getAction()) {
                    case START_SNEAKING:
                    case START_SPRINTING:
                        if (inInventory) sentAction = true;
                }

                break;
            case CLICK_WINDOW:
                WrapperPlayClientClickWindow windowClickWrapper = new WrapperPlayClientClickWindow(event);
                if (windowClickWrapper.getWindowId() != 0) return;

                // This allows us to also track 1.9+ users.

                inInventory = true;
        }
    }
}