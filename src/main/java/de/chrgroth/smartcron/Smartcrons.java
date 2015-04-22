package de.chrgroth.smartcron;

import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.WeakHashMap;

import de.chrgroth.smartcron.api.SmartcronTask;

/**
 * Allows to control {@link SmartcronTask} instances. A new task is scheduled using {@link #schedule(SmartcronTask)}. Before application
 * shutdown {@link #shutdown()} may be used to cleanup all running instances.
 * 
 * @author Christian Groth
 */
public class Smartcrons {
	
	private final Timer timer;
	private final WeakHashMap<Smartcron, Boolean> smartcrons;
	
	public Smartcrons() {
		timer = new Timer();
		smartcrons = new WeakHashMap<>();
	}
	
	/**
	 * Starts execution of given task immediately. All further executions depend on tasks return value.
	 * 
	 * @param task
	 *            task to be scheduled
	 */
	public void schedule(SmartcronTask task) {
		
		// create internal smartcron object living in timer task instances
		Smartcron smartcron = new Smartcron(task);
		smartcrons.put(smartcron, Boolean.TRUE);
		
		// execute now
		SmartcronTimerTask timerTask = new SmartcronTimerTask(timer, smartcron);
		timer.schedule(timerTask, new Date());
	}
	
	/**
	 * Returns all currently scheduled tasks.
	 * 
	 * @return schedules tasks
	 */
	public HashSet<Smartcron> getScheduledTasks() {
		
		// TODO why do we need this??!?
		System.gc();
		
		return new HashSet<Smartcron>(smartcrons.keySet());
	}
	
	/**
	 * Cancels all scheduled tasks.
	 */
	public void shutdown() {
		timer.cancel();
		smartcrons.clear();
	}
}
