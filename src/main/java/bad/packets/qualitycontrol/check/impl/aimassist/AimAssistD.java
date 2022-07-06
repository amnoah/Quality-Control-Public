package bad.packets.qualitycontrol.check.impl.aimassist;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by idk
 * AimAssistD
 */

@CheckInfo(name = "AimAssist", type = "D", buffered = true)
public class AimAssistD extends RotationCheck {

    public AimAssistD(final QualityControlPlayer data) {
        super(data);
    }

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        if (data.getCombatProcessor().getTicksSinceAttack() < 5) {

            final double moduloPitch = Math.abs(data.getRotationProcessor().getPitch() % data.getRotationProcessor().getGcdPitch());

            if (moduloPitch < 1.2E-5) {
                if (raiseBufferBy(1) > 5) fail();
            }
        } else lowerBufferBy(0.5);
    }
}
