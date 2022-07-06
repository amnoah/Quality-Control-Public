package bad.packets.qualitycontrol.check.impl.disabler;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.PacketCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;

import java.util.Arrays;
import java.util.List;

/**
 * Created by am noah
 * DisablerB
 */

@CheckInfo(name = "Disabler", type = "B")
public class DisablerB extends PacketCheck {

    public DisablerB(final QualityControlPlayer data) {
        super(data);

        listenedPacketsIncoming.add(PacketType.Play.Client.STEER_VEHICLE);
    }

    //All possible values.
    private static final List<Float> validTurns = Arrays.asList(
            0.29400003f,
            0.98f,
            0f
    );

    @Override
    public void handleIncomingPacket(PacketPlayReceiveEvent event) {
        final WrapperPlayClientSteerVehicle wrapper = new WrapperPlayClientSteerVehicle(event);

        //I know that dismounting causes weird stuff to happen.
        if (!wrapper.isUnmount()) {
            final float forward = Math.abs(wrapper.getForward()), side = Math.abs(wrapper.getSideways());

            if (!validTurns.contains(forward) || !validTurns.contains(side)) fail();
        }
    }
}