package bad.packets.qualitycontrol.check.impl.invalidrotation;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.check.type.RotationCheck;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import org.bukkit.GameMode;

/**
 * Created by am noah
 * InvalidRotationA
 */

@CheckInfo(name = "InvalidRotation", type = "A")
public class InvalidRotationA extends RotationCheck {

    public InvalidRotationA(final QualityControlPlayer data) {
        super(data);
    }

    @Override
    public void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        if (ticksSinceBoat < 5 || data.getPositionProcessor().getTicksSinceTeleport() < 7 ||
                data.getPositionProcessor().isTeleporting() ||
                data.getBukkitPlayer().getGameMode().equals(GameMode.SPECTATOR) ||
                data.getPositionProcessor().isStupidityPacket()) return;

        // The player cannot send a rotation without rotating.
        if (data.getRotationProcessor().getDeltaPitch() == 0 && data.getRotationProcessor().getDeltaYaw() == 0) fail();
    }
}
