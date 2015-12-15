package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;

/**
 * Immutable metadata about smartcron executions.
 *
 * @author Christian Groth
 */
public class SmartcronExecution {
    private final LocalDateTime scheduled;
    private final LocalDateTime started;
    private final long duration;
    private final String error;
    private final LocalDateTime nextExecution;

    public SmartcronExecution(LocalDateTime scheduled, LocalDateTime started, long duration, String error, LocalDateTime nextExecution) {
        this.scheduled = scheduled;
        this.started = started;
        this.duration = duration;
        this.error = error;
        this.nextExecution = nextExecution;
    }

    public LocalDateTime getScheduled() {
        return scheduled;
    }

    public LocalDateTime getStarted() {
        return started;
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

    @Override
    public String toString() {
        return "SmartcronExecution [scheduled=" + scheduled + ", started=" + started + ", duration=" + duration + ", error=" + error + ", nextExecution=" + nextExecution + "]";
    }
}
