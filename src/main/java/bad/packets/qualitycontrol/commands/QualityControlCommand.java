package bad.packets.qualitycontrol.commands;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.manager.ConfigManager;
import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.PlayerUtil;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class QualityControlCommand {

    private static final String noInput =
            MessageUtil.translate(
                    new StringBuilder("&9Available commands for &b&lQuality Control&r&7:\n \n")
                            .append("&7/&9QC Alerts &7- Toggle alerts.\n")
                            .append("&7/&9QC CheckFile (Username) (File Path)&7- Check for a file on a user's PC.\n")
                            .append("&7/&9QC Kick (Username) (Reason) &7- Packet based kick method.\n")
                            .append("&7/&9QC PerfProfiling &7 - Toggle performance profiling.\n")
                            .append("&7/&9QC Profile (Username) &7 - Display a user's AC profile.\n")
                            .append("&7/&9QC Reload &7- Reload the cached config settings.\n")
                            .append("&7/&9QC Version &7- Display the version of Quality Control.\n")
                            .toString());

    public void handleCommand(Player player, Command command, String string, String[] strings) {
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        if (strings.length != 0) {
            switch (strings[0].toLowerCase()) {
                case "alerts":
                    alerts(player, user);

                    break;
                case "checkfile":
                    checkFile(user, strings);

                    break;
                case "kick":
                    kick(user, strings);

                    break;
                case "perfprofiling":
                    perfProfiling(user);

                    break;
                case "profile":
                    if (strings.length <= 1) {
                        invalidProfile(user, "(No Input)");
                        return;
                    }

                    String profilePlayer = strings[1];
                    profile(user, profilePlayer);

                    break;
                case "reload":
                    reload(user);

                    break;
                case "version":
                    version(user);

                    break;
                default:
                    handleNoInput(user);

                    break;
            }
        } else handleNoInput(user);
    }

    /**
     * Handle toggling Alerts.
     */

    private void alerts(Player player, User user) {
        if (player.hasPermission(QualityControl.INSTANCE.alertsPermission))
            MessageUtil.toggleAlerts(user);
    }

    /**
     * Handle starting the FileCheckTest.
     */

    private void checkFile(User user, String[] input) {
        if (input.length <= 1) {
            invalidCheckFile(user, "(No Input)");
            return;
        }

        String location;

        if (input.length <= 2) {
            invalidCheckFileLocation(user);
            return;
        } else location = input[2];

        QualityControlPlayer player;

        String inputPlayer = input[1];

        try {
            player = QualityControl.INSTANCE.playerManager.getFromString(inputPlayer);
        } catch (Exception ignored) {
            invalidCheckFile(user, inputPlayer);
            return;
        }

        if (player == null) {
            invalidCheckFile(user, inputPlayer);
            return;
        }

        String filePath = "level://../" + location;
        String pretendLocation = "/.minecraft/" + location;

        user.sendMessage(MessageUtil.translate("" +
                "&7Starting a FileCheckTest for &b" + player.getUsername() + " &7for the file path &b" + pretendLocation + "&7."));
        player.getFileCheckTest().initializeTest(user, filePath, pretendLocation);
    }

    private void invalidCheckFile(User user, String input) {
        user.sendMessage(MessageUtil.translate("&7Invalid player! Cannot access &b" + input + "&7."));
    }

    private void invalidCheckFileLocation(User user) {
        user.sendMessage(MessageUtil.translate("&7No file location input!"));
    }

    /**
     * Handle kicking players.
     */

    private void kick(User user, String[] input) {
        QualityControlPlayer player;

        if (input.length <= 1) {
            invalidKick(user, "(No Input)");
            return;
        }

        String inputPlayer = input[1];

        try {
            player = QualityControl.INSTANCE.playerManager.getFromString(inputPlayer);
        } catch (Exception ignored) {
            invalidKick(user, inputPlayer);
            return;
        }

        if (player == null) {
            invalidKick(user, inputPlayer);
            return;
        }

        StringBuilder reason = new StringBuilder();
        int stringCount = 0;

        for (String string : input) {
            if (stringCount <= 1) {
                stringCount += 1;
            } else {
                reason.append(string).append(" ");
            }
        }

        PlayerUtil.kickPlayer(player.getPlayer(), MessageUtil.translate(reason.toString()));
        user.sendMessage(MessageUtil.translate("&7Successfully kicked &b" + inputPlayer + "&7!"));
    }

    private void invalidKick(User user, String input) {
        user.sendMessage(MessageUtil.translate("&7Invalid player! Cannot kick &b" + input + "&7."));
    }

    private void perfProfiling(User user) {
        QualityControlPlayer player = QualityControl.INSTANCE.getPlayerManager().get(user);

        player.toggleProfiling();
        user.sendMessage(MessageUtil.translate("&7You have &b" + (player.getProfiling() ? "Enabled" : "Disabled" + " &7performance profiling.")));
    }

    /**
     * Create a profile for users on request.
     */

    private void profile(User user, String input) {
        QualityControlPlayer player;

        try {
            player = QualityControl.INSTANCE.playerManager.getFromString(input);
        } catch (Exception ignored) {
            invalidProfile(user, input);
            return;
        }

        if (player == null) {
            invalidProfile(user, input);
            return;
        }

        StringBuilder profile = new StringBuilder("&9--------------------------");
        profile.append("\n&7Profile for &b").append(player.getUsername()).append("&7.");
        profile.append("\n \n&7Client-Version: &b").append(player.getClientVersion().toString());
        profile.append("\n&7Client-Brand: &b").append(player.getClientBrand());
        profile.append("\n&7Keep-Alive Ping: &b").append(player.getKeepAlivePing());
        profile.append("\n&7Transaction Ping: &b").append(player.getTransactionPing());

        int totalVL = 0;

        for (Check check : player.getChecks()) {
            totalVL += check.getVl();
        }

        profile.append("\n&7Current VL: &b").append(totalVL);
        profile.append("\n&9--------------------------");

        user.sendMessage(MessageUtil.translate(profile.toString()));
    }

    private void invalidProfile(User user, String input) {
        user.sendMessage(MessageUtil.translate("&7Invalid player! Cannot access profile for &b" + input + "&7."));
    }

    /**
     * Handle reloading configuration settings.
     */

    String reloadMessage = MessageUtil.translate("&7Configuration Reloaded.");

    private void reload(User user) {
        ConfigManager.reloadConfiguration();
        user.sendMessage(reloadMessage);
    }

    /**
     * Send a version message (for debugs).
     */

    String versionMessage = MessageUtil.translate(
            "&7You're running &bQuality Control &7version &b" + QualityControl.INSTANCE.getVersion() + "&7!");

    private void version(User user) {
        user.sendMessage(versionMessage);
    }

    /**
     * Send a help message when improper input is sent.
     */

    private void handleNoInput(User user) {
        user.sendMessage(noInput);
    }
}