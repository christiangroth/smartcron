package de.chrgroth.smartcron.api;

import java.time.LocalDateTime;

/**
 * Simple POJO to control execution of {@link SmartcronTask} methods.
 * 
 * @author Christian Groth
 */
public class SmartcronResult {
	
	/**
	 * Enumeration defining all possible schedule modes.
	 * 
	 * @author Christian Groth
	 */
	public static enum Mode {
		SCHEDULE, DELAY, ABORT;
	}
	
	/**
	 * Creates an execution for given schedule date.
	 * 
	 * @param schedule
	 *            date when next execution should happen
	 * @return execution object
	 */
	public static SmartcronResult SCHEDULE(LocalDateTime schedule) {
		return new SmartcronResult(Mode.SCHEDULE, schedule, 0l);
	}
	
	/**
	 * Creates an execution for number of given milliseconds.
	 * 
	 * @param delay
	 *            number of milliseconds until next execution
	 * @return execution object
	 */
	public static SmartcronResult DELAY(long delay) {
		return new SmartcronResult(Mode.DELAY, null, delay);
	}
	
	/**
	 * Aborts further executions.
	 * 
	 * @return execution object
	 */
	public static SmartcronResult ABORT() {
		return new SmartcronResult(Mode.ABORT, null, 0l);
	}
	
	private final Mode mode;
	private final LocalDateTime schedule;
	private final long delay;
	
	private SmartcronResult(Mode mode, LocalDateTime schedule, long delay) {
		this.mode = mode;
		this.schedule = schedule;
		this.delay = delay;
	}
	
	/**
	 * Returns execution mode.
	 * 
	 * @return execution mode
	 */
	public Mode getMode() {
		return mode;
	}
	
	/**
	 * Returns schedule date, if any.
	 * 
	 * @return schedule date or null
	 */
	public LocalDateTime getSchedule() {
		return schedule;
	}
	
	/**
	 * Returns schedule delay, if any.
	 * 
	 * @return schedule delay or null
	 */
	public long getDelay() {
		return delay;
	}
}
