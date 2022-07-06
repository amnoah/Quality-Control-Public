package bad.packets.qualitycontrol.check.impl.scaffold;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerBlockPlacement;

/**
 * Created by am noah
 * ScaffoldD
 */

@CheckInfo(name = "Scaffold", type = "D")
public class ScaffoldD extends PacketCheck {

    public ScaffoldD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_BLOCK_PLACEMENT);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientPlayerBlockPlacement wrapper = new WrapperPlayClientPlayerBlockPlacement(event);

        final double x = wrapper.getBlockPosition().getX(), y = wrapper.getBlockPosition().getY(),
                z = wrapper.getBlockPosition().getZ();

        //This may false, I don't really know.
        switch (wrapper.getFace()) {
            case OTHER:
                if ((x != -1) && (y != 4095) && (z != -1)) fail();

                break;
            case NORTH:
                if ((z + 1.03) < data.getPositionProcessor().getZ()) fail();

                break;
            case SOUTH:
                if ((z - 0.03) > data.getPositionProcessor().getZ()) fail();

                break;
            case WEST:
                if ((x + 1.03) < data.getPositionProcessor().getX()) fail();

                break;
            case EAST:
                if ((x - 0.03) > data.getPositionProcessor().getX()) fail();

                break;
            case UP:
                if ((y - 2.03) > data.getPositionProcessor().getY()) fail();

                break;
            case DOWN:
                if ((y + 0.03) < data.getPositionProcessor().getY()) fail();

                break;
        }
    }
}