package de.chrgroth.smartcron;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.model.SmartcronMetadata;

/**
 * Allows to control {@link Smartcron} instances. A new smartcron is scheduled using {@link #schedule(Smartcron)}. Before application shutdown
 * {@link #shutdown()} may be used to stop all running instances.
 *
 * @author Christian Groth
 */
public class Smartcrons {

    private static final Logger LOG = LoggerFactory.getLogger(Smartcrons.class);

    private final Timer timer;
    private final Set<SmartcronHandler> smartcrons = new HashSet<>();

    /**
     * Creates a new instance using default configuration.
     */
    public Smartcrons() {
        this(null);
    }

    /**
     * Creates a new instance using given thread name.
     *
     * @param threadName
     *            custom thread name
     */
    public Smartcrons(String threadName) {
        timer = threadName != null ? new Timer(threadName, true) : new Timer(true);
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
            LOG.warn("skip scheduling null smartcron");
            return;
        }

        // add handler
        LOG.info("creating handler for " + smartcron);
        SmartcronHandler handler = new SmartcronHandler(timer, smartcron);
        synchronized (smartcrons) {
            smartcrons.add(handler);
        }

        // execute now
        handler.activate();
    }

    /**
     * Activates all currently deactivated smartcrons of given type.
     *
     * @param type
     *            type to be activated
     */
    public void activate(Class<? extends Smartcron> type) {
        LOG.info("activating handlers for smartcrons of type: " + type);
        iterateSmartcrons((iterator, handler) -> {
            if (handler.isOfType(type) && !handler.isActive()) {
                handler.activate();
            }
        });
    }

    /**
     * Deactivates all currently active smartcrons of given type.
     *
     * @param type
     *            type to be cancelled
     */
    public void deactivate(Class<? extends Smartcron> type) {
        LOG.info("deactivating handlers for smartcrons of type: " + type);
        iterateSmartcrons((iterator, handler) -> {
            if (handler.isOfType(type) && handler.isActive()) {
                handler.deactivate();
            }
        });
    }

    /**
     * Removes all inactive smartcrons from system.
     *
     * @return metadata for all removed smartcrons, never null
     */
    public Set<SmartcronMetadata> purge() {
        Set<SmartcronMetadata> result = new HashSet<>();

        // purge
        LOG.info("removing all inactive smartcron handlers");
        iterateSmartcrons((iterator, handler) -> {
            if (!handler.isActive()) {
                LOG.info("removing handler: " + handler);
                iterator.remove();
                result.add(handler.cretaeMetadata());
            }
        });

        // done
        return result;
    }

    private interface IterationCallback {

        /**
         * Called per iteration.
         *
         * @param iterator
         *            current iterator
         * @param handler
         *            handler instance
         */
        void handle(Iterator<SmartcronHandler> iterator, SmartcronHandler handler);
    }

    private void iterateSmartcrons(IterationCallback callback) {

        // walk all timer tasks
        synchronized (smartcrons) {
            Iterator<SmartcronHandler> iterator = smartcrons.iterator();
            while (iterator.hasNext()) {

                // delegate
                callback.handle(iterator, iterator.next());
            }
        }
    }

    /**
     * Returns metadata for all smartcrons with matching type.
     *
     * @param type
     *            type to be cancelled
     * @return metadata, never null
     */
    public Set<SmartcronMetadata> getMetadata(Class<? extends Smartcron> type) {
        return new HashSet<>(smartcrons).stream().filter(s -> s.isOfType(type)).map(s -> s.cretaeMetadata()).collect(Collectors.toSet());
    }

    /**
     * Returns metadata for all currently scheduled smartcrons.
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
        smartcrons.forEach(s -> s.deactivate());
        smartcrons.clear();
        timer.cancel();
    }
}
