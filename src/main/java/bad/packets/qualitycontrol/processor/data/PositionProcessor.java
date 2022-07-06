package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.ArrayDeque;

public class PositionProcessor {

    private final QualityControlPlayer data;

    public PositionProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our tracked objects.
     */

    private boolean teleporting;
    private int ticksSinceTeleport = 0;

    private boolean packetGround, lastPacketGround;

    private boolean positionChanged;
    private boolean stupidityPacket;

    private Location location, lastLocation;
    private double x, y, z, lastX, lastY, lastZ;
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;

    private boolean tickingActive;
    private boolean lastTickStable, tickStable;
    private boolean transactionArrived;

    /**
     * Our Getters.
     */

    public boolean hasPositionChanged() {
        return positionChanged;
    }

    public boolean isPacketGround() {
        return packetGround;
    }

    public boolean isLastPacketGround() {
        return lastPacketGround;
    }

    public boolean isStupidityPacket() {
        return stupidityPacket;
    }

    public boolean isTickingActive() {
        return tickingActive;
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public int getTicksSinceTeleport() {
        return ticksSinceTeleport;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getDeltaZ() {
        return deltaZ;
    }

    public double getDeltaXZ() {
        return deltaXZ;
    }

    public double getLastDeltaX() {
        return lastDeltaX;
    }

    public double getLastDeltaY() {
        return lastDeltaY;
    }

    public double getLastDeltaZ() {
        return lastDeltaZ;
    }

    public double getLastDeltaXZ() {
        return lastDeltaXZ;
    }

    public Location getLocation() {
        return location;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Update Flying Packet related information.
     */

    public void handleFlyingPacket(WrapperPlayClientPlayerFlying wrapper) {
        stupidityPacket = checkStupidityPacket(wrapper);

        if (wrapper.hasPositionChanged() && !stupidityPacket) handlePosition(wrapper);
        else positionChanged = false;

        data.getRotationProcessor().handleFlyingPacket(wrapper);

        lastPacketGround = packetGround;
        packetGround = wrapper.isOnGround();

        checkTicking();

        if (stupidityPacket) return;

        teleporting = data.getTeleportTracker().handlePosition(location.getPosition(), wrapper.hasPositionChanged(), wrapper.hasRotationChanged());
        handleTicks();
        data.getCombatProcessor().handleFlying();
    }

    private void handlePosition(WrapperPlayClientPlayerFlying wrapper) {
        positionChanged = true;

        lastLocation = location;
        lastX = x;
        lastY = y;
        lastZ = z;
        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        lastDeltaXZ = deltaXZ;

        location = wrapper.getLocation();
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        deltaX = x - lastX;
        deltaY = y - lastY;
        deltaZ = z - lastZ;
        deltaXZ = Math.hypot(deltaX, deltaZ);
    }

    private void handleTicks() {
        ticksSinceTeleport = teleporting ? 0 : ticksSinceTeleport + 1;
    }

    /**
     * Is the client currently sending a stupidity packet?
     * This is a 1.17+ "feature" where the client sends multiple of the same position within the same tick.
     */
    private boolean checkStupidityPacket(WrapperPlayClientPlayerFlying wrapper) {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_17)) return false;
        if (!wrapper.hasPositionChanged()) return false;

        //Not completely accurate but oh well.
        return (wrapper.getLocation().equals(location));
    }

    /**
     * A not-entirely-accurate method to see if a player is actively sending flying packets.
     */

    private void checkTicking() {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            tickingActive = true;
            return;
        }
        if (stupidityPacket) return;
        if (!transactionArrived) tickStable = false;

        tickingActive =  (lastTickStable && tickStable);

        lastTickStable = tickStable;
        transactionArrived = false;
    }

    public void checkTickingTransaction() {
        if (data.getClientVersion().isOlderThan(ClientVersion.V_1_9)) {
            return;
        }

        if (!data.getTransactionProcessor().isQualityControlTransaction()) return;

        if (transactionArrived) {
            tickStable = false;
            return;
        }

        transactionArrived = tickStable = true;
    }
}