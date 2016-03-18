package de.chrgroth.smartcron;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.api.SmartcronExecutionContext;
import de.chrgroth.smartcron.model.SmartcronExecution;
import de.chrgroth.smartcron.model.SmartcronMetadata;
import de.chrgroth.smartcron.model.SmartcronStatistics;

/**
 * Handler class responsible for controlling execution of a certain {@link Smartcron}.
 *
 * @author Christian Groth
 */
public class SmartcronHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SmartcronHandler.class);

    // shared from Smartcrons
    private final Timer timer;

    // local for this smartcron / timer task
    private final Smartcron smartcron;
    private final String smartcronName;
    private boolean active;
    private LocalDateTime scheduled;
    private SmartcronTimer task;
    private SmartcronStatistics statistics = new SmartcronStatistics();
    private final Map<String, SmartcronStatistics> statisticsPerMode = new HashMap<>();
    private final List<SmartcronExecution> history = new ArrayList<>();

    public SmartcronHandler(Timer timer, Smartcron smartcron) {

        // shared
        this.timer = timer;

        // local
        this.smartcron = smartcron;
        smartcronName = smartcron.getClass().getName();
        active = false;
    }

    public boolean isOfType(Class<? extends Smartcron> type) {
        return smartcron.getClass().equals(type);
    }

    public void activate() {

        // state guard
        if (active) {
            return;
        }

        // activate
        LOG.info("activating smartcron " + smartcronName);
        active = true;
        task = new SmartcronTimer(this, null);
        try {
            timer.schedule(task, 0l);
        } catch (Exception e) {
            LOG.error("scheduling smartcron " + smartcronName + " failed: " + e.getMessage(), e);
            active = false;
            task = null;
        }
    }

    public void deactivate() {

        // state guard
        if (!active) {
            return;
        }

        // deactivate
        LOG.info("deactivating smartcron " + smartcronName);
        try {
            task.cancel();
        } catch (Exception e) {
            LOG.error("failed to cancel smartcron " + smartcronName + ": " + e.getMessage(), e);
        } finally {
            task = null;
            scheduled = null;
            active = false;
        }

        // add deactivated marker execution entry to history
        if (smartcron.executionHistory()) {
            addToHistory(SmartcronExecution.createDeactivatedMarkerEntry());
        }
    }

    public void smartcronExecutionCallback(SmartcronExecutionContext context, SmartcronExecution execution) {

        // reset next execution status
        task = null;
        scheduled = null;

        // update statistics
        boolean isError = execution.getError() != null;
        statistics = update(statistics, execution.getDuration(), isError);
        String mode = context.getMode();
        SmartcronStatistics modeStatistics = statisticsPerMode.get(mode);
        if (modeStatistics == null) {
            modeStatistics = new SmartcronStatistics();
            statisticsPerMode.put(mode, modeStatistics);
        }
        statisticsPerMode.put(mode, update(modeStatistics, execution.getDuration(), isError));

        // update history
        if (smartcron.executionHistory() && !context.isIgnoreInHistory()) {
            addToHistory(execution);
        }

        // abort if no follow up is needed
        LocalDateTime nextExecution = execution.getNextExecution();
        if (nextExecution == null) {
            LOG.info("smartcron " + smartcronName + " did not return next execution time, disabling.");
            active = false;
            return;
        }

        // shutdown handling
        if (active) {

            // re-schedule next execution
            scheduled = nextExecution;
            task = new SmartcronTimer(this, scheduled);
            try {
                timer.schedule(task, Date.from(nextExecution.atZone(ZoneId.systemDefault()).toInstant()));
            } catch (Exception e) {
                LOG.error("rescheduling smartcron " + smartcronName + " failed: " + e.getMessage(), e);
                active = false;
                task = null;
            }
        }
    }

    private SmartcronStatistics update(SmartcronStatistics statistics, long duration, boolean isError) {

        // increment count and errors
        long count = statistics.getCount() + 1;
        long errors = statistics.getErrors();
        if (isError) {
            errors++;
        }

        // compute new average duration
        double avgDuration = (statistics.getAvgDuration() * (count - 1) + duration) / count;

        // done
        return new SmartcronStatistics(count, errors, avgDuration);
    }

    private void addToHistory(SmartcronExecution execution) {

        // check max history size
        int maxExecutionHistorySize = smartcron.maxExecutionHistorySize();
        if (history.size() == maxExecutionHistorySize) {
            history.remove(maxExecutionHistorySize - 1);
        }

        // add new entry
        history.add(0, execution);
    }

    public SmartcronMetadata cretaeMetadata() {
        return new SmartcronMetadata(smartcronName, active, scheduled, statistics, statisticsPerMode, history);
    }

    public Smartcron getSmartcron() {
        return smartcron;
    }

    public boolean isActive() {
        return active;
    }
}
