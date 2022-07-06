package bad.packets.qualitycontrol.util;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.manager.ConfigManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class MessageUtil {
    private final List<User> enabledAlerts = new ArrayList<>();

    /**
     * Send a message with text, hover text, and a click command to all people with alerts enabled.
     */
    public void sendAlert(String message, String hover, String click) {
        //Prepare the regular text portion of the message.
        Component alert = Component.text(message).
                //Add the hover component. Thank you Retrooper for this code.
                        hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(hover))).
                //Add the click event.
                        clickEvent(ClickEvent.runCommand(click));

        for (User user : enabledAlerts) {
            user.sendMessage(alert);
        }
    }

    /**
     * Send a text message to all people with alerts enabled.
     */
    public void sendMessage(String message) {
        for (User user : enabledAlerts) {
            user.sendMessage(message);
        }
    }

    /**
     * Toggle a player's alerts.
     */
    public void toggleAlerts(User user) {
        if (ConfigManager.CONFIG_REQUIRES_UPDATE) {
            user.sendMessage("§b§lQC > §r§c Quality Control will not operate normally until the configuration file is updated.");
        }

        if (!enabledAlerts.contains(user)) {
            enabledAlerts.add(user);
            user.sendMessage(translate(ConfigManager.ENABLE_ALERTS));
        } else {
            enabledAlerts.remove(user);
            user.sendMessage(translate(ConfigManager.DISABLE_ALERTS));
        }
    }

    /**
     * Handle auto-logins/config update warnings.
     */
    public void handleLogIn(Player player) {
        if (ConfigManager.CONFIG_REQUIRES_UPDATE) {
            player.sendMessage(
                    "§b§lQC > §r§c Quality Control will not operate normally until the configuration file is updated.");
        } else if (ConfigManager.AUTO_ENABLE_ALERTS) {
            toggleAlerts(getUser(player));
        }
    }

    /**
     * Remove logged out players from the alert-enabled user list.
     */
    public void handleLogOut(Player player) {
        enabledAlerts.remove(getUser(player));
    }

    /**
     * Simple conversion between Bukkit player and PacketEvents user (cleans up code).
     */
    private User getUser(Player player) {
        return PacketEvents.getAPI().getPlayerManager().getUser(player);
    }

    /**
     * Translate & symbols into proper color symbols.
     */
    public String translate(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
