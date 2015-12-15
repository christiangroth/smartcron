package de.chrgroth.smartcron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.model.SmartcronExecution;
import de.chrgroth.smartcron.model.SmartcronMetadata;

/**
 * Timer task handling execution of {@link Smartcron} instances controlled by {@link Smartcrons}.
 *
 * @author Christian Groth
 */
public class SmartcronTimer extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(SmartcronTimer.class);

    private final Timer timer;
    private final Set<SmartcronTimer> smartcrons;
    private final Smartcron smartcron;
    private final List<SmartcronExecution> executions;
    private final Date scheduled;

    SmartcronTimer(Timer timer, Set<SmartcronTimer> smartcrons, Smartcron smartcron) {
        this(timer, smartcrons, smartcron, null, null);
    }

    private SmartcronTimer(SmartcronTimer other, Date nextSchedule) {
        this(other.timer, other.smartcrons, other.smartcron, other.executions, nextSchedule);
    }

    private SmartcronTimer(Timer timer, Set<SmartcronTimer> smartcrons, Smartcron smartcron, List<SmartcronExecution> executions, Date scheduled) {
        this.timer = timer;
        this.smartcrons = smartcrons;
        this.smartcron = smartcron;
        this.executions = executions != null ? executions : new ArrayList<>();
        this.scheduled = scheduled;
    }

    public boolean isOfType(Class<? extends Smartcron> type) {
        return smartcron.getClass().equals(type);
    }

    public SmartcronMetadata cretaeMetadata() {
        return new SmartcronMetadata(smartcron.getClass().getName(), executions, scheduled);
    }

    @Override
    public void run() {

        // prepare
        String smartcronName = smartcron.getClass().getName();
        Date started = null;
        long duration = 0;
        String error = null;
        Date nextSchedule = null;

        // execute
        try {
            started = new Date();
            nextSchedule = smartcron.run();

            // finished successful
            duration = System.currentTimeMillis() - started.getTime();
        } catch (Exception e) {

            // finished with error
            duration = System.currentTimeMillis() - started.getTime();
            error = e.getMessage();
            if (smartcron.abortOnException()) {
                LOG.error("smartcron " + smartcronName + " crashed: " + e.getMessage() + ". no further executions will be planned.", e);
            } else {
                LOG.warn("smartcron " + smartcronName + " crashed: " + e.getMessage() + ". trying to reover.", e);
                nextSchedule = smartcron.recover();
            }
        }

        // add execution
        executions.add(new SmartcronExecution(scheduled, started, duration, error, nextSchedule));

        // abort if no follow up is needed
        smartcrons.remove(this);
        if (nextSchedule == null) {
            LOG.info("removing smartcron from scheduler: " + smartcronName);
            return;
        }

        // schedule next execution
        SmartcronTimer newTimer = new SmartcronTimer(this, nextSchedule);
        smartcrons.add(newTimer);
        try {
            timer.schedule(newTimer, nextSchedule);
        } catch (Exception e) {
            LOG.error("rescheduling failed for smartcron " + smartcronName + ": " + e.getMessage(), e);
            smartcrons.remove(newTimer);
        }
    }
}
