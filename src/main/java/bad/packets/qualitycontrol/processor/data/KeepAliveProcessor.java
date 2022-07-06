package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class KeepAliveProcessor {

    private final QualityControlPlayer data;

    public KeepAliveProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    /**
     * Our tracked objects.
     */

    public static Map<Long, Long> pendingKeepAlives = new HashMap<>();

    private boolean validKeepAlive = true;

    private long latestKeepAlivePing = 0, averagePastPing = 0;
    private final ArrayDeque<Long> pastKeepAlivePing = new ArrayDeque<>(50);

    /**
     * Our Getters.
     */

    public int getPendingKeepAlives() {
        return pendingKeepAlives.size();
    }

    public boolean isValidKeepAlive() {
        return validKeepAlive;
    }

    public long getKeepAlivePing() {
        return latestKeepAlivePing;
    }

    public ArrayDeque<Long> getPastKeepAlivePing() {
        return pastKeepAlivePing;
    }

    public long getAveragePastPing() {
        return averagePastPing;
    }

    /**
     * Handle verifying incoming Keep Alive packets.
     */

    public void handleReceiveKeepAlive(long id) {
        if (pendingKeepAlives.containsKey(id)) {
            validKeepAlive = true;

            // This allows us to track keep alive ping in a simplistic manner.
            latestKeepAlivePing = System.currentTimeMillis() - pendingKeepAlives.remove(id);
            pastKeepAlivePing.add(latestKeepAlivePing);

            // This calculates the average ping across the past 50 keep alive packets (Any better ways?).
            long total = 0;

            for (Long pastPing : pastKeepAlivePing) {
                total = total + pastPing;
            }

            averagePastPing = (total / pastKeepAlivePing.size());
        } else validKeepAlive = false;
    }



    /**
     * Track sent Keep Alive packets.
     */

    public void handleSendKeepAlive(long id) {
        pendingKeepAlives.put(id, System.currentTimeMillis());
    }
}
