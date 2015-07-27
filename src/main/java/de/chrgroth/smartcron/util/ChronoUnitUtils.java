package de.chrgroth.smartcron.util;

import java.time.temporal.ChronoUnit;

/**
 * Utility class to compute milliseconds based on different {@link ChronoUnit} values.
 * 
 * @author Christian Groth
 */
public final class ChronoUnitUtils {
	private ChronoUnitUtils() {
		// utility class, private constructor
	}
	
	/**
	 * Converts the given value in given unit to milliseconds. Only units from {@link ChronoUnit#WEEKS} {@link ChronoUnit#MILLIS} are
	 * accepted, {@link IllegalArgumentException} is thrown otherwise.
	 * 
	 * @param value
	 *          value in unit
	 * @param unit
	 *          unit of value
	 * @return value in milliseconds
	 */
	public static long toMillis(long value, ChronoUnit unit) {
		
		// compute multiplier
		int multiplier = 1;
		switch (unit) {
		case WEEKS:
			multiplier *= 7;
		case DAYS:
			multiplier *= 2;
		case HALF_DAYS:
			multiplier *= 12;
		case HOURS:
			multiplier *= 60;
		case MINUTES:
			multiplier *= 60;
		case SECONDS:
			multiplier *= 1000;
		case MILLIS:
			break;
		case FOREVER:
		case ERAS:
		case MILLENNIA:
		case CENTURIES:
		case DECADES:
		case YEARS:
		case MONTHS:
		case MICROS:
		case NANOS:
		default:
			throw new IllegalArgumentException("unit " + unit + " is not supported!!");
		}
		
		// done
		return value * multiplier;
	}
}
