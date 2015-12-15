package de.chrgroth.smartcron;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.Smartcron;

/**
 * Allows to control {@link Smartcron} instances. A new smartcron is scheduled using {@link #schedule(Smartcron)}. Before application shutdown
 * {@link #shutdown()} may be used to cleanup all running instances.
 *
 * @author Christian Groth
 */

public class Smartcrons {
    private static final Logger LOG = LoggerFactory.getLogger(Smartcrons.class);

    private final class SmartcronTimer extends TimerTask {

        private final Smartcron smartcron;
        private int executions;
        private Date nextExecution;

        private SmartcronTimer(Smartcron smartcron) {
            this(smartcron, 0, null);
        }

        private SmartcronTimer(SmartcronTimer other) {
            this(other.smartcron, other.executions, other.nextExecution);
        }

        private SmartcronTimer(Smartcron smartcron, int executions, Date nextExecution) {
            this.smartcron = smartcron;
            this.executions = executions;
            this.nextExecution = nextExecution;
        }

        public boolean isOfType(Class<? extends Smartcron> type) {
            return smartcron.getClass().equals(type);
        }

        public SmartcronMetadata cretaeMetadata() {
            return new SmartcronMetadata(smartcron, executions, nextExecution);
        }

        @Override
        public void run() {
            String smartcronName = smartcron.getClass().getName();

            // execute
            nextExecution = null;
            try {
                nextExecution = smartcron.run();
                executions++;
            } catch (Exception e) {
                if (smartcron.abortOnException()) {
                    LOG.error("smartcron " + smartcronName + " crashed: " + e.getMessage() + ". no further executions will be planned.", e);
                } else {
                    LOG.warn("smartcron " + smartcronName + " crashed: " + e.getMessage() + ". trying to reover.", e);
                    nextExecution = smartcron.recover();
                }
            }

            // abort if no follow up is needed
            smartcrons.remove(this);
            if (nextExecution == null) {
                LOG.info("removing smartcron from scheduler: " + smartcronName);
                return;
            }

            // schedule next execution
            SmartcronTimer newTimer = new SmartcronTimer(this);
            smartcrons.add(newTimer);
            try {
                timer.schedule(newTimer, nextExecution);
            } catch (Exception e) {
                LOG.error("rescheduling failed for smartcron " + smartcronName + ": " + e.getMessage(), e);
                smartcrons.remove(newTimer);
            }
        }
    }

    private final Timer timer;
    private final Set<SmartcronTimer> smartcrons;

    public Smartcrons() {
        this(null);
    }

    public Smartcrons(String threadName) {
        if (threadName != null && !"".equals(threadName)) {
            timer = new Timer(threadName);
        } else {
            timer = new Timer();
        }
        smartcrons = new HashSet<>();
    }

    /**
     * Starts execution of given smartcron immediately. All further executions depend on smartcrons return value.
     *
     * @param smartcron
     *            smartcron to be scheduled
     */
    public void schedule(Smartcron smartcron) {

        // null guard
        if (smartcron == null) {
            return;
        }

        // create timer
        SmartcronTimer timerTask = new SmartcronTimer(smartcron);
        synchronized (smartcrons) {
            smartcrons.add(timerTask);
        }

        // execute now
        timer.schedule(timerTask, new Date());
    }

    /**
     * Cancels all currently scheduled smartcrons of given type. Returned metadata will still contain next scheduling date although timer was cancelled.
     *
     * @param type
     *            type to be cancelled
     * @return metadata for all cancelled smartcrons, never null
     */
    public Set<SmartcronMetadata> cancel(Class<? extends Smartcron> type) {
        Set<SmartcronMetadata> cancelled = new HashSet<>();

        // remove and collect
        synchronized (smartcrons) {
            Iterator<SmartcronTimer> iterator = smartcrons.iterator();
            while (iterator.hasNext()) {
                SmartcronTimer timerTask = iterator.next();
                if (timerTask.isOfType(type)) {

                    // cancel smartcron
                    timerTask.cancel();

                    // remove from internal set
                    iterator.remove();
                    cancelled.add(timerTask.cretaeMetadata());
                }
            }
        }

        // done
        return cancelled;
    }

    /**
     * Returns metadata about all currently scheduled smartcrons.
     *
     * @return metadata, never null
     */
    public Set<SmartcronMetadata> getMetadata() {
        return new HashSet<>(smartcrons).stream().map(s -> s.cretaeMetadata()).collect(Collectors.toSet());
    }

    /**
     * Cancels all scheduled smartcrons.
     */
    public void shutdown() {
        timer.cancel();
        smartcrons.clear();
    }
}
