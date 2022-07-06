package bad.packets.qualitycontrol.check.impl.aimassist;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * AimAssistC
 */

@CheckInfo(name = "AimAssist", type = "C")
public class AimAssistC extends RotationCheck {

    public AimAssistC(final QualityControlPlayer data) {
        super(data);
    }

    /**
     * Invalid Slowdown check based off of entirely magic values.
     * Surprisingly accurate. Have not been able to false flag yet but detects a lot of auras.
     */

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        if (!wrapper.hasRotationChanged() || !wrapper.hasPositionChanged()) return;
        if (data.getPositionProcessor().isTeleporting() || !data.getPositionProcessor().isTickingActive()) return;
        if (data.getPositionProcessor().getDeltaXZ() <= 0.15) return;

        final double deltaYaw = data.getRotationProcessor().getDeltaYaw();
        final double deltaPitch = data.getRotationProcessor().getDeltaPitch();

        // How much faster is the player moving than in the previous move?
        final double accelerationXZ = Math.abs(data.getPositionProcessor().getDeltaXZ() -
                data.getPositionProcessor().getLastDeltaXZ());

        // Literally just a magic value. I'm not sure why it works so well but it does.
        final double accelLimit = (deltaYaw / deltaPitch / 2000);

        if (accelerationXZ < accelLimit && deltaYaw > 15 && deltaPitch > 5 && data.getCombatProcessor().getTicksSinceAttack() <= 2) fail();
    }
}
