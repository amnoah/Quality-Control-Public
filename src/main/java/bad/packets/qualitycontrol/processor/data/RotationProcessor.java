package bad.packets.qualitycontrol.processor.data;

import bad.packets.qualitycontrol.player.QualityControlPlayer;
import bad.packets.qualitycontrol.util.GraphUtil;
import bad.packets.qualitycontrol.util.MathUtil;
import bad.packets.qualitycontrol.util.type.EvictingList;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

import java.util.ArrayList;
import java.util.List;

public class RotationProcessor {

    private final QualityControlPlayer data;

    public RotationProcessor(final QualityControlPlayer data) {
        this.data = data;
    }

    private static final double EXPANDER = Math.pow(2, 24);
    private final EvictingList<Double> yawSamples = new EvictingList<>(50);
    private final EvictingList<Double> pitchSamples = new EvictingList<>(50);

    /**
     * Our tracked objects.
     */

    private boolean rotationChanged;

    private float yaw, pitch, lastYaw, lastPitch;
    private double deltaYaw, deltaPitch, lastDeltaYaw, lastDeltaPitch;
    private double accelYaw, accelPitch, lastAccelYaw, lastAccelPitch;
    private double gcdYaw, gcdPitch;
    private double sensitivityYaw, sensitivityPitch, lastSensitivityYaw, lastSensitivityPitch;

    private boolean cinematic;
    private final List<Double> cinematicYaw = new ArrayList<>(20);
    private final List<Double> cinematicPitch = new ArrayList<>(20);
    private long lastSmooth = 0L, lastHighRate = 0L;

    /**
     * Our Getters.
     */

    public boolean hasRotationChanged() {
        return rotationChanged;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public double getDeltaPitch() {
        return deltaPitch;
    }

    public double getDeltaYaw() {
        return deltaYaw;
    }

    public double getLastDeltaPitch() {
        return lastDeltaPitch;
    }

    public double getLastDeltaYaw() {
        return lastDeltaYaw;
    }

    public double getAccelYaw() {
        return accelYaw;
    }

    public double getAccelPitch() {
        return accelPitch;
    }

    public double getLastAccelYaw() {
        return lastAccelYaw;
    }

    public double getLastAccelPitch() {
        return lastAccelPitch;
    }

    public double getGcdYaw() {
        return gcdYaw;
    }

    public double getGcdPitch() {
        return gcdPitch;
    }

    public double getSensitivityYaw() {
        return sensitivityYaw;
    }

    public double getSensitivityPitch() {
        return sensitivityPitch;
    }

    public double getLastSensitivityYaw() {
        return lastSensitivityYaw;
    }

    public double getLastSensitivityPitch() {
        return lastSensitivityPitch;
    }

    public boolean isCinematic() {
        return cinematic;
    }

    /**
     * Update Flying Packet related information.
     */

    public void handleFlyingPacket(WrapperPlayClientPlayerFlying wrapper) {
        if (!wrapper.hasRotationChanged() || data.getPositionProcessor().isStupidityPacket()) {
            rotationChanged = false;
            return;
        }

        rotationChanged = true;

        handleRotation(wrapper);
        checkCinematic();
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double yawToF2(double yawDelta) {
        return yawDelta / .15;
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double pitchToF3(double pitchDelta) {
        int b0 = pitchDelta >= 0 ? 1 : -1; //Checking for inverted mouse.
        return pitchDelta / .15 / b0;
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double getSensitivityFromPitchGCD(double gcd) {
        double stepOne = pitchToF3(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    /**
     * Credit: OverFlow 2.0
     */

    private static double getSensitivityFromYawGCD(double gcd) {
        double stepOne = yawToF2(gcd) / 8;
        double stepTwo = Math.cbrt(stepOne);
        double stepThree = stepTwo - .2f;
        return stepThree / .6f;
    }

    /**
     * Determine sensitivity and aim movement.
     * Code from OverFlow 2.0.
     */

    private void handleRotation(WrapperPlayClientPlayerFlying wrapper) {
        lastYaw = yaw;
        lastPitch = pitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        lastSensitivityYaw = sensitivityYaw;
        lastSensitivityPitch = sensitivityPitch;

        final float deltaYaw = Math.abs(wrapper.getLocation().getYaw() - lastYaw);
        final float deltaPitch = Math.abs(wrapper.getLocation().getPitch() - lastPitch);

        final double gcdYaw = MathUtil.getGcd((deltaYaw * EXPANDER), (lastDeltaYaw * EXPANDER));
        final double gcdPitch = MathUtil.getGcd((deltaPitch * EXPANDER), (lastDeltaPitch * EXPANDER));

        final double dividedYawGcd = gcdYaw / EXPANDER;
        final double dividedPitchGcd = gcdPitch / EXPANDER;

        if (gcdYaw > 90000 && gcdYaw < 2E7 && dividedYawGcd > 0.01f && deltaYaw < 8) {
            yawSamples.add(dividedYawGcd);
        }

        if (gcdPitch > 90000 && gcdPitch < 2E7 && deltaPitch < 8) {
            pitchSamples.add(dividedPitchGcd);
        }

        double modeYaw = 0.0;
        double modePitch = 0.0;

        if (pitchSamples.size() > 5 && yawSamples.size() > 5) {
            modeYaw = MathUtil.getMode(yawSamples);
            modePitch = MathUtil.getMode(pitchSamples);
        }

        sensitivityYaw = getSensitivityFromYawGCD(modeYaw);
        sensitivityPitch = getSensitivityFromPitchGCD(modePitch);

        this.gcdPitch = dividedPitchGcd;
        this.gcdYaw = dividedYawGcd;
        this.yaw = wrapper.getLocation().getYaw();
        this.pitch = wrapper.getLocation().getPitch();
        this.deltaYaw = Math.abs(yaw - lastYaw);
        this.deltaPitch = Math.abs(pitch - lastPitch);
        lastAccelYaw = accelYaw;
        lastAccelPitch = accelPitch;
        accelYaw = Math.abs(deltaYaw - lastDeltaYaw);
        accelPitch = Math.abs(deltaPitch - lastDeltaPitch);
    }

    /**
     * Check if the player has cinematic camera enabled.
     *
     * Taken from Frequency.
     */

    private void checkCinematic() {
        final long now = System.currentTimeMillis();

        final double differenceYaw = Math.abs(deltaYaw - lastDeltaYaw);
        final double differencePitch = Math.abs(deltaPitch - lastDeltaPitch);

        final double joltYaw = Math.abs(differenceYaw - deltaYaw);
        final double joltPitch = Math.abs(differencePitch - deltaPitch);

        final boolean cinematic = (now - lastHighRate > 250L) || now - lastSmooth < 9000L;

        if (joltYaw > 1.0 && joltPitch > 1.0) {
            this.lastHighRate = now;
        }

        if (deltaYaw > 0.0 && deltaPitch > 0.0) {
            cinematicYaw.add(deltaYaw);
            cinematicPitch.add(deltaPitch);
        }

        if (yawSamples.size() == 20 && pitchSamples.size() == 20) {
            // Get the cerberus/positive graph of the sample-lists
            final GraphUtil.GraphResult resultsYaw = GraphUtil.getGraphNoString(cinematicYaw);
            final GraphUtil.GraphResult resultsPitch = GraphUtil.getGraphNoString(cinematicPitch);

            // Negative values
            final int negativesYaw = resultsYaw.getNegatives();
            final int negativesPitch = resultsPitch.getNegatives();

            // Positive values
            final int positivesYaw = resultsYaw.getPositives();
            final int positivesPitch = resultsPitch.getPositives();

            // Cinematic camera usually does this on *most* speeds and is accurate for the most part.
            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) {
                this.lastSmooth = now;
            }

            cinematicYaw.clear();
            cinematicPitch.clear();
        }

        this.cinematic = cinematic;

        this.lastDeltaYaw = deltaYaw;
        this.lastDeltaPitch = deltaPitch;
    }
}