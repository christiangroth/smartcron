package de.chrgroth.smartcron;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionFactory;

import de.chrgroth.smartcron.api.SmartcronResult;
import de.chrgroth.smartcron.model.CounterTask;
import de.chrgroth.smartcron.model.ExceptionTask;

// TODO test metadata
public class SmartcronsTest {
	
	private Smartcrons smartcrons;
	private CounterTask task;
	
	@Before
	public void setUp() {
		smartcrons = new Smartcrons();
		task = new CounterTask();
		
		// assert clean start
		Assert.assertEquals(0, task.counter);
		noPendingTasks();
	}
	
	@Test
	public void nullTask() {
		smartcrons.schedule(null);
	}
	
	@Test
	public void exceptionTask() {
		smartcrons.schedule(new ExceptionTask());
	}
	
	@Test
	public void nullResult() {
		
		// check execution of scheduled task
		task.execution = null;
		schedule();
		await().until(taskCalled(1));
	}
	
	@Test
	public void abortResult() {
		
		// check execution of scheduled task
		task.execution = SmartcronResult.ABORT();
		schedule();
		await().until(taskCalled(1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void zeroDelay() {
		SmartcronResult.DELAY(0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeDelay() {
		SmartcronResult.DELAY(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeMinDelay() {
		SmartcronResult.DELAY(Long.MIN_VALUE);
	}
	
	@Test
	public void delayResult() {
		
		// check execution of scheduled task
		task.execution = SmartcronResult.DELAY(50);
		schedule();
		for (int i = 1; i < 6; i++) {
			Assert.assertEquals(1, smartcrons.getScheduledTasks().size());
			await().until(taskCalled(i));
		}
		
		// stop executions explicitly
		smartcrons.shutdown();
	}
	
	@Test
	public void delayOverflowResult() throws Exception {
		
		// check single execution of scheduled task
		task.execution = SmartcronResult.DELAY(Long.MAX_VALUE);
		schedule();
		await().until(taskCalled(1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nullSCheduleDelay() {
		SmartcronResult.SCHEDULE(null);
	}
	
	@Test
	public void pastScheduleResult() {
		
		// check execution of scheduled task, past schedule means direct execution
		task.execution = SmartcronResult.SCHEDULE(LocalDateTime.now().minusSeconds(10));
		schedule();
		Assert.assertEquals(1, smartcrons.getScheduledTasks().size());
		
		// stop executions explicitly
		smartcrons.shutdown();
	}
	
	@Test
	public void scheduleResult() {
		
		// create task rescheduling at now + 50ms using time API
		task = new CounterTask() {
			@Override
			public SmartcronResult run() {
				super.run();
				return SmartcronResult.SCHEDULE(LocalDateTime.now().plus(50, ChronoUnit.MILLIS));
			}
		};
		
		// check execution of scheduled task
		schedule();
		for (int i = 1; i < 6; i++) {
			Assert.assertEquals(1, smartcrons.getScheduledTasks().size());
			await().until(taskCalled(i));
		}
		
		// stop executions explicitly
		smartcrons.shutdown();
	}
	
	private void schedule() {
		smartcrons.schedule(task);
	}
	
	private ConditionFactory await() {
		return Awaitility.await().pollInterval(new Duration(10, TimeUnit.MILLISECONDS)).atMost(Duration.TWO_HUNDRED_MILLISECONDS);
	}
	
	private Callable<Boolean> taskCalled(int count) {
		return new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return task.counter == count;
			}
		};
	}
	
	@After
	public void tearDown() {
		noPendingTasks();
	}
	
	private void noPendingTasks() {
		await().until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return smartcrons.getScheduledTasks().isEmpty();
			}
		});
	}
}
