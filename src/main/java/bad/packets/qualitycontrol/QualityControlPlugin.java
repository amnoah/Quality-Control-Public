package bad.packets.qualitycontrol;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Yes, we're GPL!
 */
public final class QualityControlPlugin extends JavaPlugin {

    /**
     * Gets called on load. And we redirect it to our main class.
     */
    @Override
    public void onLoad() {
        QualityControl.INSTANCE.load(this);
    }

    /**
     * Gets called on plugin enable. And we redirect it to our main class.
     */
    @Override
    public void onEnable() {
        QualityControl.INSTANCE.enable(this);
    }

    /**
     * Gets called on plugin disable. And we redirect it to our main class.
     */
    @Override
    public void onDisable() {
        QualityControl.INSTANCE.stop(this);
    }
}
