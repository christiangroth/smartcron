package de.chrgroth.smartcron.api;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import de.chrgroth.smartcron.util.ChronoUnitUtils;

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
    Date run();

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
    default Date recover() {
        return abort();
    }

    /**
     * Aborts further execution of smartcron by returning null.
     *
     * @return null to abort execution
     */
    default Date abort() {
        return null;
    }

    /**
     * Computes next execution date using given delay in milliseconds. Only units from {@link ChronoUnit#WEEKS} to {@link ChronoUnit#MILLIS} are accepted,
     * {@link IllegalArgumentException} is thrown otherwise.
     *
     * @param delay
     *            delay value
     * @param unit
     *            delay unit
     * @return computed next execution date
     */
    default Date delay(long delay, ChronoUnit unit) {

        // validate delay
        if (delay < 1) {
            throw new IllegalArgumentException("delay must be >= 1!!");
        }

        // compute date
        return new Date(System.currentTimeMillis() + ChronoUnitUtils.toMillis(delay, unit));
    }
}
