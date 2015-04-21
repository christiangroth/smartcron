package de.chrgroth.smartcron;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.SmartcronExecution.Mode;

/**
 * Allows to control {@link SmartcronTask} instances. A new task is scheduled using {@link #schedule(SmartcronTask)}. Before application
 * shutdown {@link #shutdown()} may be used to cleanup all running instances.
 * 
 * @author Christian Groth
 */
public class Smartcron {
	private static final Logger LOG = LoggerFactory.getLogger(Smartcron.class);
	
	private static class Handler {
		
		// TODO use global timer for all executions?
		private final Timer timer;
		private final SmartcronTask task;
		
		private Handler(SmartcronTask task) {
			this.task = task;
			timer = new Timer();
			timer.schedule(create(), new Date());
		}
		
		// TODO simplify?!?
		private TimerTask create() {
			return new TimerTask() {
				
				@Override
				public void run() {
					try {
						
						// execute task
						SmartcronExecution nextExecution = task.run();
						
						// abort if no follow up is needed
						Mode mode = nextExecution.getMode();
						if (nextExecution == null || mode == Mode.ABORT) {
							return;
						}
						
						// get mode of follow up task
						if (mode == Mode.SCHEDULE) {
							timer.schedule(create(), nextExecution.getSchedule());
						} else if (mode == Mode.DELAY) {
							timer.schedule(create(), nextExecution.getDelay());
						} else {
							LOG.error("aborting rescheduling due to unimplemented timer follow up mode: " + mode + "!!");
						}
					} catch (Exception e) {
						LOG.error("aborting task rescheduling " + task.getClass().getName() + " due to uncaught exception: " + e.getMessage(), e);
					}
				}
			};
		}
		
		private void stop() {
			timer.cancel();
		}
	}
	
	private Set<Handler> handlers;
	
	public Smartcron() {
		handlers = new HashSet<>();
	}
	
	/**
	 * Starts execution of given task immediately. All further executions depend on tasks return value.
	 * 
	 * @param task
	 *            task to be scheduled
	 */
	public void schedule(SmartcronTask task) {
		handlers.add(new Handler(task));
	}
	
	/**
	 * Cancels all scheduled tasks.
	 */
	public void shutdown() {
		handlers.forEach(h -> h.stop());
		handlers.clear();
	}
}
