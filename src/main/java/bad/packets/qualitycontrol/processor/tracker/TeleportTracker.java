package bad.packets.qualitycontrol.processor.tracker;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.type.QCPositionOut;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fully lag compensated teleport tracker.
 * Not ideal design (ram eater), but works perfectly.
 */

public class TeleportTracker {

    private final QualityControlPlayer data;

    public TeleportTracker(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our Objects.
     */

    private boolean teleporting = false;

    private final Map<Integer, List<QCPositionOut>> pendingTeleports = new HashMap<>();

    private final List<Vector3d> activeTeleportLocations = new ArrayList<>();

    /**
     * Our Getters.
     */

    public boolean isTeleporting() {
        return teleporting;
    }

    /**
     * Handle positions.
     */

    public boolean handlePosition(Vector3d position, boolean positionChanged, boolean rotationChanged) {
        if (activeTeleportLocations.isEmpty() || !positionChanged || !rotationChanged) {
            teleporting = false;
            return false;
        }

        return activeTeleportLocations.contains(position);
    }

    /**
     * Handle adding teleports to our tracker.
     */

    public void handleTeleport(WrapperPlayServerPlayerPositionAndLook wrapper) {
        final int currentID = data.getTransactionProcessor().getCurrentID();
        final int nextID = data.getTransactionProcessor().getNextID();

        if (data.getTransactionProcessor().hasTransactionAlreadyArrived()) {
            activeTeleportLocations.add(new Vector3d(wrapper.getX(), wrapper.getY(), wrapper.getZ()));
        } else {
            if (pendingTeleports.containsKey(currentID)) {
                pendingTeleports.get(currentID).add(new QCPositionOut(wrapper.getX(), wrapper.getY(), wrapper.getZ(), true));
            } else {
                List<QCPositionOut> currentActions = new ArrayList<>();
                currentActions.add(new QCPositionOut(wrapper.getX(), wrapper.getY(), wrapper.getZ(), true));

                pendingTeleports.put(currentID, currentActions);
            }
        }

        if (pendingTeleports.containsKey(nextID)) {
            pendingTeleports.get(currentID).add(new QCPositionOut(wrapper.getX(), wrapper.getY(), wrapper.getZ(), false));
        } else {
            List<QCPositionOut> currentActions = new ArrayList<>();
            currentActions.add(new QCPositionOut(wrapper.getX(), wrapper.getY(), wrapper.getZ(), true));

            pendingTeleports.put(currentID, currentActions);
        }
    }


    /**
     * Handle transactions.
     */

    public void handleTransaction(int transactionID) {
        if (!pendingTeleports.containsKey(transactionID)) return;
        List<QCPositionOut> pending = pendingTeleports.remove(transactionID);

        for (QCPositionOut position : pending) {
            if (position.isAdd()) {
                activeTeleportLocations.add(position.getPosition());
            } else {
                activeTeleportLocations.remove(position.getPosition());
            }
        }
    }
}