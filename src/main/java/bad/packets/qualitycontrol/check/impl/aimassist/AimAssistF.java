package bad.packets.qualitycontrol.check.impl.aimassist;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by DefineOutside
 * AimAssistF
 */

@CheckInfo(name = "AimAssist", type = "F")
public class AimAssistF extends RotationCheck {

    public AimAssistF(final QualityControlPlayer data) {
        super(data);
    }

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        if (data.getPositionProcessor().isTeleporting()) return;

        /*
         * This check looks for a specific flaw in many kill auras... such as LiquidBounce's.
         * These clients (improperly) % 360 their yaw movement, leading to insane snaps such as what this checks for.
         */

        final float yaw = data.getRotationProcessor().getYaw();
        final double deltaYaw = data.getRotationProcessor().getDeltaYaw(),
                lastDeltaYaw = data.getRotationProcessor().getLastDeltaYaw();

        if (yaw < 360 && yaw > -360 && deltaYaw > 320 && lastDeltaYaw < 30) fail();
    }
}
