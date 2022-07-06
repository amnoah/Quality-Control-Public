package bad.packets.qualitycontrol.check.impl.brandspoof;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

/**
 * Created by am noah
 * BrandSpoofD
 */

@CheckInfo(name = "BrandSpoof", type = "D")
public class BrandSpoofD extends PacketCheck {

    public BrandSpoofD(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
    }

    /**
     * The client should send its brand nearly instantly after joining, way before any play state packets.
     * Because of this, we know if the player failed this by their first flying packet. Past that, it's not necessarily
     * important to flag again so we might as well remove the check from being run to save performance.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientBrand().equals("null")) {
            fail();
        }

        data.getChecks().remove(this);
    }
}