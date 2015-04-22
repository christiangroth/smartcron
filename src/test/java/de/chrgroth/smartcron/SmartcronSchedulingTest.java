package de.chrgroth.smartcron;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.smartcron.api.SmartcronResult;
import de.chrgroth.smartcron.model.CounterTask;

public class SmartcronSchedulingTest {
	
	private Smartcrons smartcron;
	private CounterTask task;
	
	@Before
	public void init() {
		smartcron = new Smartcrons();
		task = new CounterTask();
	}
	
	@Test
	public void nullExecution() {
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
		task.execution = null;
		Assert.assertEquals(0, task.counter);
		schedule();
		Assert.assertEquals(1, task.counter);
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
	}
	
	@Test
	public void abortExecution() {
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
		task.execution = SmartcronResult.ABORT();
		Assert.assertEquals(0, task.counter);
		schedule();
		Assert.assertEquals(1, task.counter);
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
	}
	
	@Test
	public void delayExecution() {
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
		task.execution = SmartcronResult.DELAY(50);
		Assert.assertEquals(0, task.counter);
		schedule();
		Assert.assertEquals(1, task.counter);
		sleep(100);
		Assert.assertEquals(2, task.counter);
		Assert.assertEquals(1, smartcron.getScheduledTasks().size());
		smartcron.shutdown();
		Assert.assertTrue(smartcron.getScheduledTasks().isEmpty());
	}
	
	// TODO dateExecution
	
	// TODO error cases
	
	private void schedule() {
		smartcron.schedule(task);
		// TODO dirty
		sleep(10);
	}
	
	private void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
