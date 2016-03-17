package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable metadata about smartcron instances.
 *
 * @author Christian Groth
 */
public class SmartcronMetadata {

    private final String name;
    private final List<SmartcronExecution> executions = new ArrayList<>();
    private final long avgDuration;
    private final double errorPercentage;
    private final LocalDateTime scheduled;

    public SmartcronMetadata(String name, List<SmartcronExecution> executions, LocalDateTime scheduled) {
        this.name = name;
        if (executions != null && !executions.isEmpty()) {
            this.executions.addAll(executions);
            avgDuration = Math.round((double) executions.stream().mapToLong(se -> se.getDuration()).sum() / (double) executions.size());
            errorPercentage = Math.round((double) executions.stream().filter(se -> se.getError() != null).count() / (double) executions.size() * 100) / 100;
        } else {
            avgDuration = 0;
            errorPercentage = 0;
        }
        this.scheduled = scheduled;
    }

    public String getName() {
        return name;
    }

    public List<SmartcronExecution> getExecutions() {
        return new ArrayList<>(executions);
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public double getErrorPercentage() {
        return errorPercentage;
    }

    public LocalDateTime getScheduled() {
        return scheduled;
    }

    @Override
    public String toString() {
        return "SmartcronMetadata [name=" + name + ", executions=#" + executions.size() + ", avgDuration=" + avgDuration + ", errorPercentage=" + errorPercentage + ", scheduled="
                + scheduled + "]";
    }
}
