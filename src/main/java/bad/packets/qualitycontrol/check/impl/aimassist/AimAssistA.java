package bad.packets.qualitycontrol.check.impl.aimassist;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

/**
 * Created by am noah
 * AimAssistA
 */

@CheckInfo(name = "AimAssist", type = "A")
public class AimAssistA extends RotationCheck {

    public AimAssistA(final QualityControlPlayer data) {
        super(data);
    }

    private int activeTicks = 0;
    private double llPitch;

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        // Don't run if the client is pulling a 1.17+.
        if (data.getPositionProcessor().isStupidityPacket()) {
            activeTicks = 0;
            return;
        }

        /*
         * This is an extremely weird looking aim check (I have a switch addiction), but it seems effective.
         * This behavior is hard to reproduce legitimately and is a sign of bad kill auras.
         */
        final double pitch = Math.abs(data.getRotationProcessor().getDeltaPitch());

        switch (activeTicks) {
            case 0:
                llPitch = pitch;
                activeTicks = 1;
                break;
            case 1:
                activeTicks = 2;
                break;
            case 2:
                final double lPitch = Math.abs(data.getRotationProcessor().getLastDeltaPitch());

                // 3 15 3
                if (llPitch < 3f && lPitch > 15f && pitch < 3f) {
                        fail();
                }

                llPitch = lPitch;

                break;
        }
    }

    @Override
    public void universalReset() {
        activeTicks = 0;
    }
}
