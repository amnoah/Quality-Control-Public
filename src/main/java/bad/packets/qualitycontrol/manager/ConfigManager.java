package bad.packets.qualitycontrol.manager;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.check.api.CheckInfo;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    /**
     * These are used for alerting users of an outdated/invalid configuration file, which completely disables the anticheat.
     * This is because we know what should be run based on the configuration, meaning we're lost if it's invalid.
     */

    public static boolean CONFIG_REQUIRES_UPDATE;

    /**
     * Basic settings for our alerts system.
     * I doubt anyone will change them from the default settings, but it's best to just do this I guess.
     */

    public static boolean AUTO_ENABLE_ALERTS;
    public static int ALERT_COOLDOWN;

    /**
     * These settings handle VL Decay.
     * Some people may not like it at all, others may want to change it up. It's best to just provide these settings.
     */

    public static boolean VL_DECAY_ENABLED;
    public static int VL_DECAY_PER;
    public static int VL_DECAY_START;


    /**
     * These are configurable messages.
     * Certain things, like alerts, may want to be customized by the end user.
     */

    public static String ALERT_MESSAGE;
    public static String ALERT_HOVER;
    public static String ALERT_COMMAND;
    public static String PLAYER_JOIN_MESSAGE;
    public static String DISABLE_ALERTS;
    public static String ENABLE_ALERTS;

    /**
     * These handle our settings for our individual checks.
     * We can add a check's name to this list in order for the check to know it's enabled.
     * We can add a check's name (the key) and the integer for at what point the punish command should be run.
     * We can add a check's name (they key) and the string for what command should be run at the punish-vl.
     * These are all pretty essential things to an anticheat, many people modify/appreciate this.
     */

    public static List<String> ENABLED_CHECKS = new ArrayList<>();
    public static Map<String, Integer> PUNISH_VL = new HashMap<>();
    public static Map<String, String> PUNISH_COMMANDS = new HashMap<>();


    public static void reloadConfiguration() {
        // Ensure the Config is as updated as possible, regenerating if need be.
        QualityControl.INSTANCE.plugin.saveDefaultConfig();
        QualityControl.INSTANCE.plugin.reloadConfig();
        FileConfiguration config = QualityControl.INSTANCE.plugin.getConfig();

        // Don't let the anticheat operate on an outdated config version, issues will arise.
        if (config.getDouble("config-version") != QualityControl.INSTANCE.version) {

            System.out.println("QC >");
            System.out.println("QC > Outdated configuration file!");
            System.out.println("QC > Please remove your current config.yml when possible, generating an updated configuration.");
            System.out.println("QC >");

            CONFIG_REQUIRES_UPDATE = true;
            return;
        } else CONFIG_REQUIRES_UPDATE = false;

        try {
            ENABLED_CHECKS.clear();
            PUNISH_VL.clear();
            PUNISH_COMMANDS.clear();

            // Alert Settings.
            AUTO_ENABLE_ALERTS = config.getBoolean("alerts.auto-enable-alerts");
            ALERT_COOLDOWN = config.getInt("alerts.alert-cooldown");

            // VL Decay Settings.
            VL_DECAY_ENABLED = config.getBoolean("alerts.decay-enabled");
            VL_DECAY_PER = config.getInt("alerts.decay-per");
            VL_DECAY_START = config.getInt("alerts.start-decay-after");

            // Configurable Messages.
            ALERT_MESSAGE = config.getString("messages.alert");
            ALERT_HOVER = config.getString("messages.alert-hover");
            ALERT_COMMAND = config.getString("messages.alert-command");
            PLAYER_JOIN_MESSAGE = config.getString("messages.player-join-alert");
            DISABLE_ALERTS = config.getString("messages.disable-alerts");
            ENABLE_ALERTS = config.getString("messages.enable-alerts");

            /*
             * This code isn't the most beautiful, but it's highly functional.
             * Check for the config equivalent of each check in our CheckManager, updating any info applicable.
             * Would likely be more optimized if I had less reliance on Strings, but oh well.
             */
            for (Class<?> check : CheckManager.CHECKS) {
                CheckInfo checkInfo = check.getAnnotation(CheckInfo.class);

                String checkLocation = new StringBuilder("checks.").append(checkInfo.name()).append(".").append(checkInfo.type()).toString().toLowerCase();
                String checkName = check.getName();

                if (config.getBoolean(checkLocation + ".enabled")) ENABLED_CHECKS.add(checkName);

                PUNISH_VL.put(checkName, config.getInt(checkLocation + ".punish-vl"));
                PUNISH_COMMANDS.put(checkName, config.getString(checkLocation + ".punish-command"));
            }

            /*
             * Again, not beautiful but functional.
             * For each player online have each of their currently enabled checks run the update config function.
             */
            if (!QualityControl.INSTANCE.playerManager.getAllData().isEmpty()) {
                for (QualityControlPlayer data : QualityControl.INSTANCE.playerManager.getAllData()) {
                    for (Check check : data.getChecks()) {
                        check.updateConfiguration();
                    }
                }
            }
        } catch (Exception ignored) {
            // If any misc errors arise, print out an error.

            System.out.println("QC >");
            System.out.println("QC > Invalid configuration file!");
            System.out.println("QC > Please remove your current config.yml when possible, generating an updated configuration.");
            System.out.println("QC >");

            CONFIG_REQUIRES_UPDATE = true;
        }
    }
}
