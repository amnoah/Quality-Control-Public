package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;

public class CombatProcessor {

    private final QualityControlPlayer data;

    public CombatProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    private int ticksSinceAttack = 20;

    public int getTicksSinceAttack() {
        return ticksSinceAttack;
    }

    public void handleFlying() {
        ticksSinceAttack = Math.min(ticksSinceAttack + 1, 20);
    }

    public void handleUseEntity() {
        ticksSinceAttack = 0;
    }
}
