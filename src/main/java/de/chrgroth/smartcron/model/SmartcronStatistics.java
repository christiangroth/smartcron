package de.chrgroth.smartcron.model;

/**
 * Execution statistics for smartcrons.
 *
 * @author Christian Groth
 */
public class SmartcronStatistics {

    private long count;
    private long errors;
    private double avgDuration;

    public SmartcronStatistics() {
        this(0, 0, 0.0d);
    }

    public SmartcronStatistics(long count, long errors, double avgDuration) {
        this.count = count;
        this.errors = errors;
        this.avgDuration = avgDuration;
    }

    public long getCount() {
        return count;
    }

    public long getErrors() {
        return errors;
    }

    public double getAvgDuration() {
        return avgDuration;
    }

    @Override
    public String toString() {
        return "SmartcronStatistics [count=" + count + ", errors=" + errors + ", avgDuration=" + avgDuration + "]";
    }
}
