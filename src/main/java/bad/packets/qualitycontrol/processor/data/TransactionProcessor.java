package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPing;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowConfirmation;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class TransactionProcessor {

    private final QualityControlPlayer data;

    public TransactionProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our tracked objects.
     */

    private boolean transactionAlreadyArrived = false;
    private boolean qualityControlTransaction = false;
    private boolean validTransaction = true;

    private int currentID, nextID = 1;
    private int answeredID;

    public static Map<Integer, Long> pendingTransactions = new HashMap<>();
    private final ArrayDeque<Integer> pendingQualityControlTrans = new ArrayDeque<>();

    private long latestTransactionPing = 0, averagePastPing = 0;
    private final ArrayDeque<Long> pastTransactionPing = new ArrayDeque<>(50);

    /**
     * Our Getters.
     */

    public boolean hasTransactionAlreadyArrived() {
        return transactionAlreadyArrived;
    }

    public boolean isQualityControlTransaction() {
        return qualityControlTransaction;
    }

    public boolean isValidTransaction() {
        return validTransaction;
    }

    public int getCurrentID() {
        return currentID;
    }

    public int getNextID() {
        return nextID;
    }

    public int getAnsweredID() {
        return answeredID;
    }

    public int getPendingTransactions() {
        return pendingTransactions.size();
    }

    public int getPendingQualityControlTransactions() {
        return pendingQualityControlTrans.size();
    }

    public long getTransactionPing() {
        return latestTransactionPing;
    }

    public ArrayDeque<Long> getPastTransactionPing() {
        return pastTransactionPing;
    }

    public long getAveragePastPing() {
        return averagePastPing;
    }

    /**
     * Handle sending a transaction to the player.
     */

    public void sendTransaction() {
        // Track our current and next ID. Useful for lag compensation.
        currentID = nextID;
        nextID = currentID + 1;

        // Send the correct packet according to server version.
        if (PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_17)) {
            data.getPlayer().sendPacket(new WrapperPlayServerPing(currentID));
        } else {
            data.getPlayer().sendPacket(new WrapperPlayServerWindowConfirmation((byte) 0, (short) currentID, false));
        }

        transactionAlreadyArrived = false;
        pendingQualityControlTrans.add(currentID);
    }

    /**
     * Handle confirming whether the transaction received was sent to the player/sent by Quality Control.
     */

    public void listenReceiveTransaction(int id) {
        if (pendingTransactions.containsKey(id)) {
            validTransaction = true;

            if (pendingQualityControlTrans.contains(id)) {
                if (id == currentID) transactionAlreadyArrived = true;

                answeredID = id;
                qualityControlTransaction = true;

                handleReceiveQCTransaction(id);

                pendingQualityControlTrans.remove(id);
            } else qualityControlTransaction = false;

            // This allows us to track transaction ping in a simplistic manner.
            latestTransactionPing = System.currentTimeMillis() - pendingTransactions.remove(id);
            pastTransactionPing.add(latestTransactionPing);

            // This calculates the average ping across the past 50 transaction packets (Any better ways?).
            long total = 0;

            for (Long pastPing : pastTransactionPing) {
                total = total + pastPing;
            }

            averagePastPing = (total / pastTransactionPing.size());
        } else {
            qualityControlTransaction = validTransaction = false;
        }
    }

    /**
     * Handle tracking what transaction IDs have been sent to the player.
     */

    public void listenSendPing(int id) {
        pendingTransactions.put(id, System.currentTimeMillis());
    }

    public void listenSendTransaction(WrapperPlayServerWindowConfirmation wrapper) {
        if (wrapper.getWindowId() != 0 || wrapper.isAccepted()) return;

        pendingTransactions.put((int) wrapper.getActionId(), System.currentTimeMillis());
    }

    /**
     * Run other tasks when a QC Transaction is received.
     */

    private void handleReceiveQCTransaction(int id) {
        data.getTeleportTracker().handleTransaction(id);
    }
}
