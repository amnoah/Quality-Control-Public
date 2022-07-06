package bad.packets.qualitycontrol.listener.bukkit;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import io.github.retrooper.packetevents.util.GeyserUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This is the class where we listen for players joining and leaving the server. We do this to handle our PlayerData.
 *
 * You may ask why do we remove players when they exit, well that is a good question its to prevent memory leaks.
 * We already have no use for PlayerData when a player leaves and we are not gonna waste memory for 2 variables.
 */
public final class JoinLeaveListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (GeyserUtil.isGeyserPlayer(event.getPlayer().getUniqueId())) return;

        User user = PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer());

        try {
            QualityControl.INSTANCE.playerManager.get(user).setBukkitPlayer(event.getPlayer());
        } catch (Exception ignored) {
            // Sometimes PacketEvents fails to send events for early packets, leading to us missing them logging in initially.
            event.getPlayer().kickPlayer("Failed to inject.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        MessageUtil.handleLogOut(event.getPlayer());

        User user = PacketEvents.getAPI().getPlayerManager().getUser(event.getPlayer());
        QualityControl.INSTANCE.playerManager.removePlayer(user);
    }
}
