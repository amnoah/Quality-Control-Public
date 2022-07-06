package bad.packets.qualitycontrol.check.impl.autoblock;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.DiggingAction;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerDigging;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by DefineOutside
 * AutoBlockF
 */

@CheckInfo(name = "AutoBlock", type = "F")
public class AutoBlockF extends PacketCheck {

    public AutoBlockF(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_DIGGING);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // This behavior is expected on 1.7- clients.
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_8)) {
            data.getChecks().remove(this);
            return;
        }

        WrapperPlayClientPlayerDigging wrapper = new WrapperPlayClientPlayerDigging(event);

        if (wrapper.getAction().equals(DiggingAction.RELEASE_USE_ITEM)) {

            /*
             * Release packets have defined inputs that should never be different:
             * BlockFace = DOWN
             * X = 0
             * Y = 0
             * Z = 0
             */

            if (!wrapper.getFace().equals(BlockFace.DOWN) || wrapper.getBlockPosition().getX() != 0 ||
                    wrapper.getBlockPosition().getY() != 0 || wrapper.getBlockPosition().getZ() != 0) fail();
        }
    }
}