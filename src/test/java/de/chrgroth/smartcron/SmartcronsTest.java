package de.chrgroth.smartcron;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionFactory;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.model.Counter;

public class SmartcronsTest {
	
	private Smartcrons smartcrons;
	private Counter counter;
	
	@Before
	public void setUp() {
		smartcrons = new Smartcrons();
		
		// assert clean start
		noPendingSmartcrons();
	}
	
	@Test
	public void scheduleNull() {
		smartcrons.schedule(null);
	}
	
	@Test
	public void scheduleException() {
		smartcrons.schedule(new Smartcron() {
		
		@Override
		public Date run() {
			throw new IllegalStateException("expected test exception.");
		}
		});
	}
	
	@Test
	public void nextExecutionNull() {
		
		// check execution
		counter = new Counter() {
		
		@Override
		protected Date calc() {
			return null;
		}
		};
		schedule();
		await().until(counterCalled(1));
	}
	
	@Test
	public void nextExecutionDlay() {
		
		// check execution
		counter = new Counter() {
		
		@Override
		protected Date calc() {
			return delay(50, ChronoUnit.MILLIS);
		}
		};
		schedule();
		for (int i = 1; i < 6; i++) {
		Assert.assertEquals(1, smartcrons.getMetadata().size());
		await().until(counterCalled(i));
		}
		
		// stop executions explicitly
		smartcrons.shutdown();
	}
	
	@Test
	public void nextExecutionDelayOverflow() throws Exception {
		
		// check single execution
		counter = new Counter() {
		
		@Override
		protected Date calc() {
			return delay(Long.MAX_VALUE, ChronoUnit.MILLIS);
		}
		};
		schedule();
		await().until(counterCalled(1));
	}
	
	@Test
	public void nextExecutionPastDate() {
		
		// check execution, past schedule date means direct execution
		counter = new Counter() {
		
		@Override
		protected Date calc() {
			return new Date(System.currentTimeMillis() - 10000);
		}
		};
		schedule();
		Assert.assertEquals(1, smartcrons.getMetadata().size());
		
		// stop executions explicitly
		smartcrons.shutdown();
	}
	
	@Test
	public void cancelNull() {
		smartcrons.cancel(null);
	}
	
	@Test
	public void cancelSmartcron() {
		
		// check no execution
		counter = new Counter() {
		
		@Override
		protected Date calc() {
			return delay(50, ChronoUnit.MILLIS);
		}
		};
		schedule();
		Assert.assertEquals(1, smartcrons.getMetadata().size());
		await().until(counterCalled(1));
		Set<SmartcronMetadata> cancelled = cancel();
		Assert.assertEquals(1, cancelled.size());
	}
	
	private void schedule() {
		smartcrons.schedule(counter);
	}
	
	private Set<SmartcronMetadata> cancel() {
		return smartcrons.cancel(counter.getClass());
	}
	
	private ConditionFactory await() {
		return Awaitility.await().pollInterval(new Duration(10, TimeUnit.MILLISECONDS)).atMost(Duration.TWO_HUNDRED_MILLISECONDS);
	}
	
	private Callable<Boolean> counterCalled(int count) {
		return new Callable<Boolean>() {
		@Override
		public Boolean call() throws Exception {
			return counter.counter == count;
		}
		};
	}
	
	@After
	public void tearDown() {
		noPendingSmartcrons();
	}
	
	private void noPendingSmartcrons() {
		await().until(new Callable<Boolean>() {
		@Override
		public Boolean call() throws Exception {
			return smartcrons.getMetadata().isEmpty();
		}
		});
	}
}
