package de.chrgroth.smartcron.api;

/**
 * Base interface to implement task with dynamic scheduling.
 * 
 * @author Christian Groth
 */
public interface SmartcronTask {
	
	/**
	 * Executes the scheduled task and returns execution strategy for next schedule.
	 * 
	 * @return next execution strategy
	 */
	SmartcronResult run();
}
