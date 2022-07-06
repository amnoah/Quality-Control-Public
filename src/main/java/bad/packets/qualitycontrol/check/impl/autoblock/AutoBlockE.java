package bad.packets.qualitycontrol.check.impl.autoblock;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * AutoBlockE
 */

@CheckInfo(name = "AutoBlock", type = "E")
public class AutoBlockE extends PacketCheck {

    public AutoBlockE(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
        listenedPacketsIncoming.add(PacketType.Play.Client.USE_ITEM);
    }

    private boolean placing = false, digging = false, useItem = false;

    /**
     * This flags every PvP client ever, but that is not a false flag.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // This behavior is normal in 1.7.10 and below.
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
            data.getChecks().remove(this);
            return;
        }

        // We need active flying packets.
        if (!data.getPositionProcessor().isTickingActive()) {
            placing = digging = useItem = false;
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if ((digging && placing) || (digging && useItem)) fail();

            placing = digging = useItem = false;
        } else {
            switch (event.getPacketType()) {
                case PLAYER_BLOCK_PLACEMENT:
                    placing = true;

                    break;
                case PLAYER_DIGGING:
                    WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);
                    // Can false on instant break blocks because Mojang.
                    if (wrapper.getAction() == DiggingAction.START_DIGGING) return;

                    digging = true;

                    break;
                case USE_ITEM:
                    useItem = true;

                    break;
            }
        }
    }
}