package bad.packets.qualitycontrol.check.impl.elytra;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

/**
 * Created by am noah
 * ElytraA
 */

@CheckInfo(name = "Elytra", type = "A")
public class ElytraA extends PacketCheck {

    public ElytraA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.ENTITY_ACTION);
    }

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9) ||
                PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        if (!data.getPositionProcessor().isTickingActive()) return;

        WrapperPlayClientEntityAction wrapper = new WrapperPlayClientEntityAction(event);

        // To start flying with an elytra you must be in the air. Potentially catches some cheats?
        if (wrapper.getAction().equals(WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA) &&
                data.getPositionProcessor().isPacketGround()) fail();
    }
}