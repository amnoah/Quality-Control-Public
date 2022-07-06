package bad.packets.qualitycontrol.check.impl.invalidrotation;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * InvalidRotationB
 */

@CheckInfo(name = "InvalidRotation", type = "B")
public class InvalidRotationB extends RotationCheck {

    public InvalidRotationB(final QualityControlPlayer data) {
        super(data);
    }

    private boolean hasBeenStable = false;

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        final double sensitivity = data.getRotationProcessor().getSensitivityPitch();
        if (sensitivity > 0D && sensitivity < 1D) hasBeenStable = true;

        // Sensitivity cannot be below 0 (1%) or above 1 (200%)
        if (hasBeenStable && (sensitivity > 1.0001D || sensitivity < 0)) {
            fail();
        }
    }
}
