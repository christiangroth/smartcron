package de.chrgroth.smartcron;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;

import de.chrgroth.smartcron.api.SmartcronTask;

/**
 * Allows to control {@link SmartcronTask} instances. A new task is scheduled using {@link #schedule(SmartcronTask)}. Before application
 * shutdown {@link #shutdown()} may be used to cleanup all running instances.
 * 
 * @author Christian Groth
 */
public class Smartcrons {
	
	private final Timer timer;
	private final Set<Smartcron> smartcrons;
	
	public Smartcrons() {
		timer = new Timer();
		smartcrons = new HashSet<>();
	}
	
	/**
	 * Starts execution of given task immediately. All further executions depend on tasks return value.
	 * 
	 * @param task
	 *            task to be scheduled
	 */
	// TODO no valid statistics before first run!
	public void schedule(SmartcronTask task) {
		
		// null guard
		if (task == null) {
			return;
		}
		
		// create internal smartcron object living in timer task instances
		Smartcron smartcron = new Smartcron(task);
		synchronized (smartcrons) {
			smartcrons.add(smartcron);
		}
		
		// execute now
		SmartcronTimerTask timerTask = new SmartcronTimerTask(timer, smartcrons, smartcron);
		timer.schedule(timerTask, new Date());
	}
	
	/**
	 * Cancels all currently schedules tasks of given type.
	 * 
	 * @param type
	 *            type to be cancelled
	 * @return all cancelled tasks, never null
	 */
	public Set<Smartcron> cancel(Class<? extends SmartcronTask> type) {
		Set<Smartcron> cancelled = new HashSet<>();
		
		// remove and collect
		synchronized (smartcrons) {
			Iterator<Smartcron> iterator = smartcrons.iterator();
			while (iterator.hasNext()) {
				Smartcron smartcron = iterator.next();
				if (smartcron.getTask().getClass().equals(type)) {
					
					// cancel timer task
					smartcron.getTimerTask().cancel();
					
					// remove from internal set
					iterator.remove();
					cancelled.add(smartcron);
				}
			}
		}
		
		// done
		return cancelled;
	}
	
	/**
	 * Returns all currently scheduled tasks.
	 * 
	 * @return schedules tasks
	 */
	public Set<Smartcron> getScheduledTasks() {
		return new HashSet<Smartcron>(smartcrons);
	}
	
	/**
	 * Cancels all scheduled tasks.
	 */
	public void shutdown() {
		timer.cancel();
		smartcrons.clear();
	}
}
