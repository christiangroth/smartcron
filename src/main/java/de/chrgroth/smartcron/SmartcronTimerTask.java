package de.chrgroth.smartcron;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.SmartcronResult;
import de.chrgroth.smartcron.api.SmartcronResult.Mode;

/**
 * Implementation of {@link TimerTask} to control dynamic scheduling after each invocation.
 * 
 * @author Christian Groth
 */
public class SmartcronTimerTask extends TimerTask {
	private static final Logger LOG = LoggerFactory.getLogger(SmartcronTimerTask.class);
	
	private final Timer timer;
	private final Set<Smartcron> smartcrons;
	private final Smartcron smartcron;
	
	public SmartcronTimerTask(Timer timer, Set<Smartcron> smartcrons, Smartcron smartcron) {
		this.timer = timer;
		this.smartcrons = smartcrons;
		this.smartcron = smartcron;
		smartcron.setTimerTask(this);
	}
	
	// TODO optimize performance
	@Override
	public void run() {
		
		// abort if no follow up is needed
		SmartcronResult result = executeTask();
		if (result == null || result.getMode() == Mode.ABORT) {
			smartcrons.remove(smartcron);
			return;
		}
		
		// calculate next execution date
		LocalDateTime nextExecutionDate = calculateNextExecutionDate(result);
		if (nextExecutionDate != null) {
			
			// reschedule
			try {
				Instant instant = nextExecutionDate.atZone(ZoneId.systemDefault()).toInstant();
				timer.schedule(new SmartcronTimerTask(timer, smartcrons, smartcron), Date.from(instant));
				smartcron.setNextExecution(nextExecutionDate);
			} catch (Exception e) {
				LOG.error("smartcron rescheduling failed for " + smartcron.getTask().getClass().getName() + ": " + e.getMessage(), e);
				smartcrons.remove(smartcron);
			}
		}
	}
	
	private SmartcronResult executeTask() {
		
		// execute
		SmartcronResult result = null;
		try {
			result = smartcron.getTask().run();
			smartcron.incExecutions();
		} catch (Exception e) {
			LOG.error("smartcron task crashed, no further executions will be planned: " + e.getMessage(), e);
		}
		
		// done
		return result;
	}
	
	private LocalDateTime calculateNextExecutionDate(SmartcronResult result) {
		
		// switch mode
		Mode mode = result.getMode();
		if (mode == Mode.SCHEDULE) {
			return result.getSchedule();
		} else if (mode == Mode.DELAY) {
			return LocalDateTime.now().plus(Duration.of(result.getDelay(), ChronoUnit.MILLIS));
		}
		
		// error
		LOG.error("aborting rescheduling due to unimplemented timer follow up mode: " + mode + "!!");
		return null;
	}
}
