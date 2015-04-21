package de.chrgroth.smartcron;

import java.util.Date;

/**
 * Simple POJO to control execution of {@link SmartcronTask} methods.
 * 
 * @author Christian Groth
 */
public class SmartcronExecution {
	
	/**
	 * Enumeration defining all possible schedule modes.
	 * 
	 * @author Christian Groth
	 */
	public static enum Mode {
		SCHEDULE, DELAY, ABORT;
	}
	
	/**
	 * Creates an execution for given date.
	 * 
	 * @param schedule
	 *            date when next execution should happen
	 * @return execution object
	 */
	public static SmartcronExecution SCHEDULE(Date schedule) {
		return new SmartcronExecution(Mode.SCHEDULE, schedule, 0l);
	}
	
	/**
	 * Creates an execution for number of given milliseconds.
	 * 
	 * @param delay
	 *            number of milliseconds until next execution
	 * @return execution object
	 */
	public static SmartcronExecution DELAY(long delay) {
		return new SmartcronExecution(Mode.DELAY, null, delay);
	}
	
	/**
	 * Blocks further executions.
	 * 
	 * @return execution object
	 */
	public static SmartcronExecution ABORT() {
		return new SmartcronExecution(Mode.ABORT, null, 0l);
	}
	
	private final Mode mode;
	private final Date schedule;
	private final long delay;
	
	private SmartcronExecution(Mode mode, Date schedule, long delay) {
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
	public Date getSchedule() {
		return schedule;
	}
	
	// TODO add local date time
	
	/**
	 * Returns schedule delay, if any.
	 * 
	 * @return schedule delay or null
	 */
	public long getDelay() {
		return delay;
	}
}
