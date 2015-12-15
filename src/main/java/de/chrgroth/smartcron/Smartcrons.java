package de.chrgroth.smartcron;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.model.SmartcronMetadata;

/**
 * Allows to control {@link Smartcron} instances. A new smartcron is scheduled using {@link #schedule(Smartcron)}. Before application shutdown
 * {@link #shutdown()} may be used to cleanup all running instances.
 *
 * @author Christian Groth
 */

public class Smartcrons {

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
        SmartcronTimer timerTask = new SmartcronTimer(timer, smartcrons, smartcron);
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
