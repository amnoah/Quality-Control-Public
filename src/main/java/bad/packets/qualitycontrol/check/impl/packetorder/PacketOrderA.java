package bad.packets.qualitycontrol.check.impl.packetorder;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketOrderCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

import java.util.ArrayDeque;

/**
 * Created by Eths
 * PacketOrderA
 */

@CheckInfo(name = "PacketOrder", type = "A")
public class PacketOrderA extends PacketOrderCheck {

    public PacketOrderA(final QualityControlPlayer data) {
        super(data);
    }

    private final ArrayDeque<PacketType.Play.Client> post = new ArrayDeque<>();

    @Override
    public void handleTransaction(boolean sentFlying) {
        if (sentFlying && post.size() > 0) {
            fail();
        }
    }

    @Override
    public void handleFlying() {
        post.clear();
    }

    @Override
    public void handlePacket(PacketType.Play.Client packetType, boolean sentFlying, PacketPlayReceiveEvent event) {
        switch (packetType) {
            case ENTITY_ACTION:
                WrapperPlayClientEntityAction entityActionWrapper = new WrapperPlayClientEntityAction(event);

                // For some reason these are sent post?
                switch (entityActionWrapper.getAction()) {
                    case START_SNEAKING:
                    case STOP_SNEAKING:
                        return;
                }

                check(packetType, sentFlying);
                break;
            case CHAT_MESSAGE:
            case CLICK_WINDOW:
                // Async window clicks.
                // Async chat.
                if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)) break;

                check(packetType, sentFlying);
                break;

            case ANIMATION:
                // Arm swings are now sent at the beginning of the next tick in 1.19?
                if (data.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_19)) break;

                check(packetType, sentFlying);
                break;
            case CLOSE_WINDOW:
            case CREATIVE_INVENTORY_ACTION:
            case EDIT_BOOK:
            case HELD_ITEM_CHANGE:
            case INTERACT_ENTITY:
            case NAME_ITEM:
            case PLAYER_ABILITIES:
            case PLAYER_BLOCK_PLACEMENT:
            case PLAYER_DIGGING:
            case PLUGIN_MESSAGE:
            case SPECTATE:
            case TAB_COMPLETE:
            case UPDATE_SIGN:
                check(packetType, sentFlying);
        }
    }

    private void check(PacketType.Play.Client packetType, boolean sentFlying) {
        if (!data.getPositionProcessor().isTickingActive()) return;

        if (sentFlying) post.add(packetType);
    }
}
