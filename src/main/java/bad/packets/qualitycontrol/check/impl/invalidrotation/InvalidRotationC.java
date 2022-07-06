package bad.packets.qualitycontrol.check.impl.invalidrotation;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidRotationC
 */

@CheckInfo(name = "InvalidRotation", type = "C")
public class InvalidRotationC extends RotationCheck {

    public InvalidRotationC(final QualityControlPlayer data) {
        super(data);
    }

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        // Rotations must be real numbers and not exceed boundaries.

        final boolean invalidPitch = checkForInvalid(wrapper.getLocation().getPitch(), true);
        final boolean invalidYaw = checkForInvalid(wrapper.getLocation().getYaw(), false);

        if (invalidPitch || invalidYaw) fail();
    }

    private boolean checkForInvalid(float rotation, boolean checkPitch) {
        if (Math.abs(rotation) > 90 && checkPitch) return true;
        return !Float.isFinite(rotation);
    }
}
