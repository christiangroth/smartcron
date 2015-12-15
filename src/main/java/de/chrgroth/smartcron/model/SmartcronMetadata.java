package de.chrgroth.smartcron.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Immutable metadata about smartcron instances.
 *
 * @author Christian Groth
 */
public class SmartcronMetadata {

    private final String name;
    private final List<SmartcronExecution> executions;
    private final Date scheduled;

    public SmartcronMetadata(String name, List<SmartcronExecution> executions, Date scheduled) {
        this.name = name;
        this.executions = new ArrayList<>();
        if (executions != null) {
            this.executions.addAll(executions);
        }
        this.scheduled = scheduled;

    }

    public String getName() {
        return name;
    }

    public List<SmartcronExecution> getExecutions() {
        return executions;
    }

    public Date getScheduled() {
        return scheduled;
    }
}
