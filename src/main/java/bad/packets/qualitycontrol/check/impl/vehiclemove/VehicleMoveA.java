package bad.packets.qualitycontrol.check.impl.vehiclemove;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;

/**
 * Created by am noah
 * VehicleMoveA
 */

@CheckInfo(name = "VehicleMove", type = "A")
public class VehicleMoveA extends PacketCheck {

    public VehicleMoveA(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_FLYING);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION);
        listenedPacketsIncoming.add(PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION);
        listenedPacketsIncoming.add(PacketType.Play.Client.STEER_VEHICLE);
    }

    private boolean lastRotating = false;

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        /*
         * The idea here is that a player cannot send 2 Steer Vehicle packets without a rotation in between.
         * This may be able to identify simple vehicle cheats.
         */

        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            if (data.getRotationProcessor().hasRotationChanged()) {
                lastRotating = true;
            }
        } else {
            WrapperPlayClientSteerVehicle wrapper = new WrapperPlayClientSteerVehicle(event);
            if (wrapper.isUnmount() || wrapper.isJump()) return;

            //There has to have been a rotation in between the last steer vehicle packet.
            if (!lastRotating) {
                fail();
            }

            lastRotating = false;
        }
    }
}