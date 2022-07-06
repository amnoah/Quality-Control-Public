package bad.packets.qualitycontrol.check;

import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.manager.ConfigManager;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.PlayerUtil;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * This is our abstract superclass for all of our checks.
 * This class contains all the necessary stuff for a check.
 */
@Getter
public abstract class Check {

    /**
     * Initialize the class.
     */

    public Check(final QualityControlPlayer data) {
        this.data = data;

        updateConfiguration();
    }

    /**
     * When our configuration file is reloaded we must also access the updated information from each check.
     * To do this simply we have this setup in each check.
     */

    public void updateConfiguration() {
        punishVL = ConfigManager.PUNISH_VL.get(this.getClass().getName());
        punishCommand = ConfigManager.PUNISH_COMMANDS.get(this.getClass().getName());

        enabled = ConfigManager.ENABLED_CHECKS.contains(this.getClass().getName());
    }

    /**
     * This is our packet event system.
     */

    public void onPacketPlayReceive(final PacketPlayReceiveEvent event) { }
    public void onPacketPlaySend(final PacketPlaySendEvent event) { }

    /**
     * PlayerData contains all of the information we track in our processors.
     * This allows the checks to use that information.
     */

    protected final QualityControlPlayer data;

    private boolean enabled;

    private String punishCommand;
    private int punishVL;

    private int vl = 0;
    private double buffer = 0;

    private long lastFlag = -1;
    private long lastAlertTime = -1;

    private int customMaxBuffer = 10000;

    private final String name = this.getClass().getAnnotation(CheckInfo.class).name();
    private final String identifier = this.getClass().getAnnotation(CheckInfo.class).type();
    private final boolean buffered = this.getClass().getAnnotation(CheckInfo.class).buffered();

    /*
     * Buffers are not ideal and shouldn't be used... but if you must then this exists.
     */

    /**
     * Increase the current buffer by a specific amount.
     * To prevent memory leaks will stop at whatever the customMaxBuffer is set to (default 10,000).
     */

    public double raiseBufferBy(double increase) {
        buffer = Math.min(buffer + increase, customMaxBuffer);
        return buffer;
    }

    /**
     * Lower the current buffer by a specific amount.
     * You cannot go below 0 for obvious reasons.
     */

    public void lowerBufferBy(double lower) {
        buffer = Math.max(buffer - lower, 0);
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void setCustomMaxBuffer(int buffer) {
        customMaxBuffer = buffer;
    }

    /**
     * Fail void used when you have no debug to send.
     */
    public final void fail() {
        fail("Empty");
    }

    /**
     * Fail void used to send alerts/adjust VL/punish players.
     */
    public final void fail(Object debug) {
        final long now = System.currentTimeMillis();

        //Handle VL adjustment.
        adjustVL(now);

        //Handle alerts.
        if (now - lastAlertTime >= ConfigManager.ALERT_COOLDOWN || lastAlertTime == -1) {
            sendAlert(debug);
            lastAlertTime = now;
        }

        //Handle running punishments.
        if (vl >= punishVL && punishVL >= 0) {
            runCommand();
        }
    }

    /**
     * Handle VL adjustment - raising and decay.
     */
    private void adjustVL(long currentMS) {
        if (ConfigManager.VL_DECAY_ENABLED) {
            //Handle VL decay/growth
            final long timeSinceFlag = currentMS - lastFlag;

            //Very messy but works surprisingly well.
            if (timeSinceFlag >= ConfigManager.VL_DECAY_START && lastFlag != -1) {
                vl = Math.max(0, vl - Math.round(timeSinceFlag / (float) ConfigManager.VL_DECAY_PER));
            }

            lastFlag = currentMS;
        }

        //Set a cap to prevent it from raising infinitely (never going to be reached in realistic scenarios).
        vl = Math.min(10000, vl + 1);
    }

    /**
     * Handle alert creation - including text, hover, and click.
     */
    public void sendAlert(Object hoverInfo) {
        // Replace placeholders with appropriate information for the alert message.
        String alert = MessageUtil.translate(ConfigManager.ALERT_MESSAGE
                .replaceAll("%player%", data.getUsername())
                .replaceAll("%check%", name)
                .replaceAll("%identifier%", identifier)
                .replaceAll("%vl%", String.valueOf(vl))
                .replaceAll("%buffered%", buffered ? "*" : ""));

        // Replace placeholders with appropriate information for hovers.
        String hover = MessageUtil.translate(ConfigManager.ALERT_HOVER
                .replaceAll("%debug%", String.valueOf(hoverInfo))
                .replaceAll("%player%", data.getUsername())
                .replaceAll("%clientbrand%", data.getClientBrand()));

        // Replace placeholders with appropriate information for alert commands.
        String command = "/" + ConfigManager.ALERT_COMMAND
                .replaceAll("%player%", data.getUsername());

        // Send the message.
        MessageUtil.sendAlert(alert, hover, command);
    }

    /**
     * Execute the command listed in the configuration for this check.
     */
    private void runCommand() {
        //Prevent console spam from people leaving commands blank.
        if (punishCommand.isEmpty()) return;

        final String punishCommand = MessageUtil.translate(this.punishCommand.replaceAll("%player%", data.getUsername()));

        PlayerUtil.runCommand(Bukkit.getConsoleSender(), punishCommand);
    }
}