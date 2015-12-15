package de.chrgroth.smartcron.api;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import de.chrgroth.smartcron.model.SmartcronExecution;
import de.chrgroth.smartcron.model.SmartcronMetadata;

/**
 * Base interface to implement smartcron with dynamic scheduling.
 *
 * @author Christian Groth
 */
public interface Smartcron {

    /**
     * Executes the scheduled smartcron and returns next execution date.
     *
     * @return next execution date
     */
    LocalDateTime run();

    /**
     * Tells whether all {@link SmartcronExecution} instances should be contained in {@link SmartcronMetadata}.
     *
     * @return true if {@link SmartcronExecution} instances should be saved, false otherwise
     */
    default boolean trackExecutions() {
        return true;
    }

    /**
     * Defines if smartcron execution will be aborted on uncaught exception. Overwrite to continue after uncaught exception and be sure to implement
     * {@link #recover()} to be able to start next execution;
     *
     * @return true if aborted on exception, false if to be continued
     */
    default boolean abortOnException() {
        return true;
    }

    /**
     * Called after uncaught exception broke last execution and {@link #abortOnException()} was set to true;
     *
     * @return date to run next execution
     */
    default LocalDateTime recover() {
        return abort();
    }

    /**
     * Aborts further execution of smartcron by returning null.
     *
     * @return null to abort execution
     */
    default LocalDateTime abort() {
        return null;
    }

    /**
     * Computes next execution date using given delay in given unit.
     *
     * @param delay
     *            delay value
     * @param unit
     *            delay unit
     * @return computed next execution date
     */
    default LocalDateTime delay(long delay, ChronoUnit unit) {

        // validate delay
        if (delay < 1) {
            throw new IllegalArgumentException("delay must be >= 1!!");
        }

        // compute date
        return LocalDateTime.now().plus(delay, unit);
    }
}
