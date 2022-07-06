package bad.packets.qualitycontrol.check.impl.invalidposition;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientTeleportConfirm;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.HashMap;
import java.util.Map;

/**
 * Idea from DefineOutside
 * InvalidPositionE
 */

@CheckInfo(name = "InvalidPosition", type = "E")
public class InvalidPositionE extends PacketCheck {

    public InvalidPositionE(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.TELEPORT_CONFIRM);
        listenedPacketsOutgoing.add(PacketType.Play.Server.PLAYER_POSITION_AND_LOOK);
    }

    private static final Map<Integer, Location> pendingTeleports = new HashMap<>();
    private boolean nextPositionShouldBeTeleport = false;
    private int teleportID;


    /**
     * I have not tested if this works.
     */

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        // Teleport Confirm/teleportIDs are only present in 1.9+.
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) ||
                data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flyingWrapper = new WrapperPlayClientPlayerFlying(event);

            /*
             * If this position should be a teleport then we can easily track if the player actually did teleport.
             * Thank you Mojang for making good netcode for once!
             */
            if (nextPositionShouldBeTeleport) {
                if (!flyingWrapper.getLocation().equals(pendingTeleports.get(teleportID))) {
                    if (data.isSafeToRun()) fail();
                }

                nextPositionShouldBeTeleport = false;
                pendingTeleports.remove(teleportID);
            }
        } else {
            // If we want to track this teleport (not a rel-move) then prepare to compare the next position we receive.

            WrapperPlayClientTeleportConfirm teleportWrapper = new WrapperPlayClientTeleportConfirm(event);
            if (!pendingTeleports.containsKey(teleportWrapper.getTeleportId())) return;

            nextPositionShouldBeTeleport = true;
            teleportID = teleportWrapper.getTeleportId();
        }
    }

    @Override
    public void handleOutgoingPacket(PacketPlaySendEvent event) {
        // Teleport Confirm/teleportIDs are only present in 1.9+.
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9) ||
                data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            data.getChecks().remove(this);
            return;
        }

        /*
         * Just for simplicity and for a lack of lag compensation we are going to ignore relative teleports.
         * Relative teleports are basically where it just adds coordinates onto your current position.
         */

        WrapperPlayServerPlayerPositionAndLook wrapper = new WrapperPlayServerPlayerPositionAndLook(event);
        if ((wrapper.getRelativeMask() & 0b11100) != 0) return;

        // Why make something new when we can just use PacketEvents' existing Location variable type?
        pendingTeleports.put(wrapper.getTeleportId(),
                new Location(wrapper.getX(), wrapper.getY(), wrapper.getZ(), wrapper.getYaw(), wrapper.getPitch()));
    }
}
