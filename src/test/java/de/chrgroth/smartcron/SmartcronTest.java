package de.chrgroth.smartcron;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.chrgroth.smartcron.api.Smartcron;

public class SmartcronTest {
	
	private Smartcron smartcron;
	
	@Before
	public void setUp() {
		smartcron = new Smartcron() {
		
		@Override
		public Date run() {
			return null;
		}
		};
	}
	
	@Test()
	public void abortExecution() {
		Assert.assertNull(smartcron.abort());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nextExecutionZeroDelay() {
		smartcron.delay(0, ChronoUnit.MILLIS);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nextExecutionNegativeDelay() {
		smartcron.delay(-1, ChronoUnit.MILLIS);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void nextExecutionNegativeMinDelay() {
		smartcron.delay(Long.MIN_VALUE, ChronoUnit.MILLIS);
	}
}
