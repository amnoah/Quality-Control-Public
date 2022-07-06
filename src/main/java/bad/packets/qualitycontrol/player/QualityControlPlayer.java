package bad.packets.qualitycontrol.player;

import bad.packets.qualitycontrol.QualityControl;
import bad.packets.qualitycontrol.check.Check;
import bad.packets.qualitycontrol.manager.CheckManager;
import bad.packets.qualitycontrol.processor.data.*;
import bad.packets.qualitycontrol.processor.test.FileCheckTest;
import bad.packets.qualitycontrol.processor.tracker.TeleportTracker;
import bad.packets.qualitycontrol.util.MessageUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;

import java.util.List;
import java.util.UUID;

/**
 * This is our PlayerData class, as the name suggests we store data about the player here.
 * Every player should have this in order for the anti cheat to function as the entire plugin is based on this.
 */
public final class QualityControlPlayer {

    public QualityControlPlayer(final User player) {
        this.player = player;
    }

    /**
     * Objects we need to establish upon the player's join.
     */

    private String clientBrand = "null";
    private org.bukkit.entity.Player bukkitPlayer = null;

    private final User player;
    private final long joinTime = System.currentTimeMillis();
    private boolean timePassedSinceLogIn = false;

    boolean profiling = false;

    private final ServerVersion serverVersion = PacketEvents.getAPI().getServerManager().getVersion();

    private final ActionProcessor actionProcessor = new ActionProcessor(this);
    private final CombatProcessor combatProcessor = new CombatProcessor(this);
    private final KeepAliveProcessor keepAliveProcessor = new KeepAliveProcessor(this);
    private final PayloadProcessor payloadProcessor = new PayloadProcessor(this);
    private final PositionProcessor positionProcessor = new PositionProcessor(this);
    private final RotationProcessor rotationProcessor = new RotationProcessor(this);
    private final TransactionProcessor transactionProcessor = new TransactionProcessor(this);

    private final FileCheckTest fileCheckTest = new FileCheckTest(this);

    private final TeleportTracker teleportTracker = new TeleportTracker(this);

    /**
     * Check related functions.
     */

    private final List<Check> checks = CheckManager.loadChecks(this);

    public List<Check> getChecks() {
        return checks;
    }

    public void runChecksReceive(PacketPlayReceiveEvent event) {
        if (!checks.isEmpty()) {
            for (Check check : checks) {
                if (check.isEnabled()) check.onPacketPlayReceive(event);
            }
        }
    }

    public void runChecksSend(PacketPlaySendEvent event) {
        for (Check check : checks) {
            if (check.isEnabled()) check.onPacketPlaySend(event);
        }
    }

    /**
     * Our data processor Getters.
     */

    public ActionProcessor getActionProcessor() {
        return actionProcessor;
    }
    public CombatProcessor getCombatProcessor() {
        return combatProcessor;
    }
    public KeepAliveProcessor getKeepAliveProcessor() {
        return keepAliveProcessor;
    }
    public PayloadProcessor getPayloadProcessor() {
        return payloadProcessor;
    }
    public PositionProcessor getPositionProcessor() {
        return positionProcessor;
    }
    public RotationProcessor getRotationProcessor() {
        return rotationProcessor;
    }
    public TransactionProcessor getTransactionProcessor() {
        return transactionProcessor;
    }

    /**
     * Our test tracker Getters.
     */

    public FileCheckTest getFileCheckTest() {
        return fileCheckTest;
    }

    /**
     * Our tracker Getters.
     */

    public TeleportTracker getTeleportTracker() {
        return teleportTracker;
    }

    /**
     * Non-processor Getters
     */

    public org.bukkit.entity.Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public String getClientBrand() {
        return clientBrand;
    }

    public ClientVersion getClientVersion() {
        return player.getClientVersion();
    }

    public int getEntityID() {
        return bukkitPlayer.getEntityId();
    }

    public long getKeepAlivePing() {
        return getKeepAliveProcessor().getAveragePastPing();
    }

    public boolean getProfiling() {
        return profiling;
    }

    public long getTransactionPing() {
        return getTransactionProcessor().getAveragePastPing();
    }

    public User getPlayer() {
        return player;
    }

    public boolean isSafeToRun() {
        // Micro Optimization
        if (timePassedSinceLogIn) return true;

        if (getTimeSinceJoin() >= 2500) {
            timePassedSinceLogIn = true;
            return true;
        }

        return false;
    }

    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public long getTimeSinceJoin() {
        return (System.currentTimeMillis() - joinTime);
    }

    public String getUsername() {
        return player.getProfile().getName();
    }

    public UUID getUUID() {
        return player.getProfile().getUUID();
    }

    /**
     * Object Setters.
     */

    public void setBukkitPlayer(org.bukkit.entity.Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;

        if (bukkitPlayer.hasPermission(QualityControl.INSTANCE.getAlertsPermission())) MessageUtil.handleLogIn(bukkitPlayer);
    }

    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    public void handleTickTask() {
        getTransactionProcessor().sendTransaction();

        getFileCheckTest().handleTicking();
    }

    public void toggleProfiling() {
        profiling = !profiling;
    }
}
