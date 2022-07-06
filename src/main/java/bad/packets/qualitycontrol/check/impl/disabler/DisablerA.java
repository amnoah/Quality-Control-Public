package bad.packets.qualitycontrol.check.impl.disabler;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;

/**
 * Created by am noah
 * DisablerA
 */

@CheckInfo(name = "Disabler", type = "A", buffered = true)
public class DisablerA extends PacketCheck {

    public DisablerA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.CLIENT_STATUS);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getBukkitPlayer() == null) return;

        WrapperPlayClientClientStatus wrapper = new WrapperPlayClientClientStatus(event);
        if (!wrapper.getAction().equals(WrapperPlayClientClientStatus.Action.PERFORM_RESPAWN)) return;

        // Theoretically a plugin continuously modifying the isDead value may be able to false this.
        if (!data.getBukkitPlayer().isDead()) fail();
    }
}