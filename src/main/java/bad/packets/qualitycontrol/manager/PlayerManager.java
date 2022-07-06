package bad.packets.qualitycontrol.manager;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This is the class where we store all of our player data and handle it.
 */
@Getter
public final class PlayerManager {

    private final ConcurrentHashMap<User, QualityControlPlayer> playerDataMap = new ConcurrentHashMap<>();

    public void addPlayer(final User user) {
        if (user != null) {
            playerDataMap.putIfAbsent(user, new QualityControlPlayer(user));
        }
    }

    public void removePlayer(final User user) {
        if (user != null) {
            playerDataMap.remove(user);
        }
    }

    public QualityControlPlayer get(final User user) {
        if (user != null) {
            return playerDataMap.get(user);
        }
        return null;
    }

    public QualityControlPlayer getFromBukkit(final Player player) {
        return get(PacketEvents.getAPI().getPlayerManager().getUser(player));
    }

    public QualityControlPlayer getFromString(final String string) {
        return getFromBukkit(Bukkit.getPlayer(string));
    }

    public QualityControlPlayer getFromUUID(final UUID uuid) {
        return getFromBukkit(Bukkit.getPlayer(uuid));
    }

    public Collection<QualityControlPlayer> getAllData() {
        return playerDataMap.values();
    }
}
