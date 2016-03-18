package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable smartcron instance metadata.
 *
 * @author Christian Groth
 */
public class SmartcronMetadata {

    private final String name;
    private final boolean active;
    private final LocalDateTime scheduled;
    private final SmartcronStatistics statistics;
    private final Map<String, SmartcronStatistics> statisticsPerMode = new HashMap<>();
    private final List<SmartcronExecution> history = new ArrayList<>();

    public SmartcronMetadata(String name, boolean active, LocalDateTime scheduled, SmartcronStatistics statistics, Map<String, SmartcronStatistics> statisticsPerMode,
            List<SmartcronExecution> history) {
        this.name = name;
        this.active = active;
        this.scheduled = scheduled;
        this.statistics = statistics;
        this.statisticsPerMode.putAll(statisticsPerMode);
        this.history.addAll(history);
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getScheduled() {
        return scheduled;
    }

    public SmartcronStatistics getStatistics() {
        return statistics;
    }

    public Map<String, SmartcronStatistics> getStatisticsPerMode() {
        return new HashMap<>(statisticsPerMode);
    }

    public List<SmartcronExecution> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "SmartcronMetadata [name=" + name + ", active=" + active + ", scheduled=" + scheduled + ", statistics=" + statistics + ", statisticsPerMode=" + statisticsPerMode
                + ", history.size()=" + history.size() + "]";
    }
}
