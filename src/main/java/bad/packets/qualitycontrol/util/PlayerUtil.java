package bad.packets.qualitycontrol.util;

import bad.packets.qualitycontrol.QualityControl;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@UtilityClass
public class PlayerUtil {
    /**
     * Method to kick players using packets.
     */
    public void kickPlayer(User user, String reason) {
        user.sendPacket(new WrapperPlayServerDisconnect(Component.text(reason)));
        user.closeConnection();
    }

    /**
     * Method to kick players using Bukkit methods safely while running off of the main thread.
     */
    public void bukkitKickPlayer(UUID player, String reason) {
        Bukkit.getScheduler().runTask(QualityControl.INSTANCE.plugin, () ->
                Bukkit.getPlayer(player).kickPlayer(reason));
    }

    /**
     * Method to run commands safely while running off of the main thread.
     */
    public void runCommand(CommandSender commandSender, String command) {
        Bukkit.getScheduler().runTask(QualityControl.INSTANCE.plugin,
                () -> Bukkit.dispatchCommand(commandSender, command));
    }
}
