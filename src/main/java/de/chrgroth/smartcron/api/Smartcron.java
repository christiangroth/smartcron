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
	 * Aborts further execution of smartcron by returning null.
	 * 
	 * @return null to abort execution
	 */
	default Date abort() {
		return null;
	}
	
	/**
	 * Computes next execution date using given delay in milliseconds. Only units from {@link ChronoUnit#WEEKS} {@link ChronoUnit#MILLIS} are
	 * accepted, {@link IllegalArgumentException} is thrown otherwise.
	 * 
	 * @return computed next execution date
	 */
	default Date delay(long delay, ChronoUnit unit) {
		
		// validate delay
		if (delay < 1) {
		throw new IllegalArgumentException("delay must be >= 1ms!!");
		}
		
		// compute date
		return new Date(System.currentTimeMillis() + delay * ChronoUnitUtils.toMillis(delay, unit));
	}
}
