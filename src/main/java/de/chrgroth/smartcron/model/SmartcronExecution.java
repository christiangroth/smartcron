package de.chrgroth.smartcron.model;

import java.util.Date;

/**
 * Immutable metadata about smartcron executions.
 *
 * @author Christian Groth
 */
public class SmartcronExecution {
    private final Date scheduled;
    private final Date started;
    private final long duration;
    private final String error;
    private final Date nextExecution;

    public SmartcronExecution(Date scheduled, Date started, long duration, String error, Date nextExecution) {
        this.scheduled = scheduled;
        this.started = started;
        this.duration = duration;
        this.error = error;
        this.nextExecution = nextExecution;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public Date getStarted() {
        return started;
    }

    public long getDuration() {
        return duration;
    }

    public String getError() {
        return error;
    }

    public Date getNextExecution() {
        return nextExecution;
    }

    @Override
    public String toString() {
        return "SmartcronExecution [scheduled=" + scheduled + ", started=" + started + ", duration=" + duration + ", error=" + error + ", nextExecution=" + nextExecution + "]";
    }
}
