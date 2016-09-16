package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;

/**
 * Immutable metadata about a single smartcron execution.
 *
 * @author Christian Groth
 */
public class SmartcronExecution {

    private final LocalDateTime scheduled;
    private final LocalDateTime started;
    private final String mode;
    private final long duration;
    private final String error;
    private final LocalDateTime nextExecution;
    private final boolean deactivated;

    public static SmartcronExecution createDeactivatedMarkerEntry() {
        return new SmartcronExecution();
    }

    private SmartcronExecution() {
        this(null, null, null, 0L, null, null, true);
    }

    public SmartcronExecution(LocalDateTime scheduled, LocalDateTime started, String mode, long duration, String error, LocalDateTime nextExecution) {
        this(scheduled, started, mode, duration, error, nextExecution, false);
    }

    private SmartcronExecution(LocalDateTime scheduled, LocalDateTime started, String mode, long duration, String error, LocalDateTime nextExecution, boolean deactivated) {
        this.scheduled = scheduled;
        this.started = started;
        this.mode = mode;
        this.duration = duration;
        this.error = error;
        this.nextExecution = nextExecution;
        this.deactivated = deactivated;
    }

    public LocalDateTime getScheduled() {
        return scheduled;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public String getMode() {
        return mode;
    }

    public long getDuration() {
        return duration;
    }

    public String getError() {
        return error;
    }

    public LocalDateTime getNextExecution() {
        return nextExecution;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public String toString() {
        return "SmartcronExecution [scheduled=" + scheduled + ", started=" + started + ", mode=" + mode + ", duration=" + duration + ", error=" + error + ", nextExecution="
                + nextExecution + ", deactivated=" + deactivated + "]";
    }
}
