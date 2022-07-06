package bad.packets.qualitycontrol.check.impl.aimassist;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idk
 * AimAssistE
 */

@CheckInfo(name = "AimAssist", type = "E")
public class AimAssistE extends RotationCheck {

    public AimAssistE(final QualityControlPlayer data) {
        super(data);
    }

    private final List<Double> pitchSamples = new ArrayList<>();

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        double deltaPitch = data.getRotationProcessor().getDeltaPitch();
        double deltaYaw = data.getRotationProcessor().getDeltaYaw();

        if (deltaPitch == 0 || deltaYaw < 3 || data.getPositionProcessor().isTeleporting()) return;

        pitchSamples.add(deltaPitch);

        if (pitchSamples.size() == 150) {
            final long distinctRotations = pitchSamples.stream().distinct().count();
            final long duplicateRotations = 150 - distinctRotations;

            if (duplicateRotations <= 9) {
                if (raiseBufferBy(1) >= 2) fail();
                else resetBuffer();
            }
        }
    }
}
