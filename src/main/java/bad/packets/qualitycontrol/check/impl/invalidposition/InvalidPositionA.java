package bad.packets.qualitycontrol.check.impl.invalidposition;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.MovementCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidPositionA
 */

@CheckInfo(name = "InvalidPosition", type = "A")
public class InvalidPositionA extends MovementCheck {

    public InvalidPositionA(final QualityControlPlayer data) {
        super(data);
    }

    //Value from Grim
    private static final double border = 2.9999999E7D;

    @Override
    public void handlePosition(WrapperPlayClientPlayerFlying wrapper) {
        /*
         * Basic check for invalid positions.
         * You must send a real number and it cannot exceed the maximum border number.
         */

        final boolean invalidX = checkInvalid(wrapper.getLocation().getX());
        final boolean invalidZ = checkInvalid(wrapper.getLocation().getZ());

        if (invalidX || invalidZ) fail();
    }

    private boolean checkInvalid(double coordinate) {
        if (Math.abs(coordinate) > border && !data.getPositionProcessor().isTeleporting()) return true;
        return !Double.isFinite(coordinate);
    }
}
