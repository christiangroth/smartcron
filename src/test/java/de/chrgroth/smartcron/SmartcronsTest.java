package de.chrgroth.smartcron;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import de.chrgroth.smartcron.api.SmartcronExecutionContext;
import de.chrgroth.smartcron.model.Counter;
import de.chrgroth.smartcron.model.SmartcronMetadata;

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
            public LocalDateTime run(SmartcronExecutionContext context) {
                throw new IllegalStateException("expected test exception.");
            }
        });
    }

    @Test
    public void scheduleRecoverable() {
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return null;
            }
        };

        smartcrons.schedule(new Smartcron() {

            @Override
            public boolean abortOnException() {
                return false;
            }

            @Override
            public LocalDateTime recover() {
                return delay(10, ChronoUnit.MILLIS);
            }

            @Override
            public LocalDateTime run(SmartcronExecutionContext context) {
                counter.counter++;
                if (counter.counter == 1) {
                    throw new IllegalStateException("expected test exception.");
                }
                return abort();
            }
        });
        await().until(counterCalled(2, true));
    }

    @Test
    public void nextExecutionNull() {

        // check execution
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return null;
            }
        };
        schedule();
        await().until(counterCalled(1, true));
    }

    @Test
    public void nextExecutionDelay() {

        // check execution
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return delay(10, ChronoUnit.MILLIS);
            }
        };
        schedule();
        for (int i = 1; i < 6; i++) {
            Assert.assertEquals(1, smartcrons.getMetadata().size());
            await().until(counterCalled(i, false));
        }

        // stop executions explicitly
        smartcrons.shutdown();
    }

    @Test
    public void nextExecutionDelayOverflow() throws Exception {

        // check single execution
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return delay(Long.MAX_VALUE, ChronoUnit.MILLIS);
            }
        };
        schedule();
        await().until(counterCalled(1, false));

        // stop executions explicitly
        smartcrons.shutdown();
    }

    @Test
    public void nextExecutionPastDate() {

        // check execution, past schedule date means direct execution
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return LocalDateTime.now().minus(10000, ChronoUnit.MILLIS);
            }
        };
        schedule();
        Assert.assertEquals(1, smartcrons.getMetadata().size());

        // stop executions implicitly
        smartcrons.shutdown();
    }

    @Test
    public void activateNull() {
        smartcrons.activate(null);
    }

    // TODO activate active

    @Test
    public void deactivateNull() {
        smartcrons.deactivate(null);
    }

    // TODO deactivate inactive

    @Test
    public void smartcronLifecycle() {

        // schedule
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return delay(5000, ChronoUnit.MILLIS);
            }
        };
        schedule();
        Assert.assertEquals(1, smartcrons.getMetadata().size());
        await().until(counterCalled(1, true));

        // deactivate
        smartcrons.deactivate(counter.getClass());
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertFalse(smartcronMetadata.isActive());

        // check history
        Assert.assertNull(smartcronMetadata.getScheduled());
        Assert.assertNotNull(smartcronMetadata.getHistory());
        Assert.assertEquals(2, smartcronMetadata.getHistory().size());
        Assert.assertFalse(smartcronMetadata.getHistory().get(1).isDeactivated());
        Assert.assertTrue(smartcronMetadata.getHistory().get(0).isDeactivated());

        // reset, restart and deactivate again after next execution
        counter.counter = 0;
        smartcrons.activate(counter.getClass());
        await().until(counterCalled(1, true));
        metadata = smartcrons.getMetadata();
        smartcrons.deactivate(counter.getClass());

        // check metadata before deactivation
        Assert.assertEquals(1, metadata.size());
        smartcronMetadata = metadata.iterator().next();
        Assert.assertTrue(smartcronMetadata.isActive());
        Assert.assertNotNull(smartcronMetadata.getScheduled());
        Assert.assertNotNull(smartcronMetadata.getHistory());
        Assert.assertEquals(3, smartcronMetadata.getHistory().size());
        Assert.assertFalse(smartcronMetadata.getHistory().get(0).isDeactivated());

        // check metadata after deactivation
        metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        smartcronMetadata = metadata.iterator().next();
        Assert.assertFalse(smartcronMetadata.isActive());
        Assert.assertNull(smartcronMetadata.getScheduled());
        Assert.assertNotNull(smartcronMetadata.getHistory());
        Assert.assertEquals(4, smartcronMetadata.getHistory().size());
        Assert.assertTrue(smartcronMetadata.getHistory().get(0).isDeactivated());

        // purge
        Set<SmartcronMetadata> purged = smartcrons.purge();
        Assert.assertNotNull(purged);
        Assert.assertEquals(1, purged.size());
    }

    @Test
    public void metadataUpdatedPerIteration() {
        List<SmartcronMetadata> metadataInstances = new ArrayList<>();

        // schedule smartcron
        counter = new Counter() {
            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                metadataInstances.add(smartcrons.getMetadata().iterator().next());
                int diff = 5 - counter;
                if (diff > 0) {
                    return delay(25, ChronoUnit.MILLIS);
                } else {
                    return abort();
                }
            }
        };
        schedule();

        // wait to finish
        Awaitility.await().pollInterval(250, TimeUnit.MILLISECONDS).atMost(Duration.FIVE_SECONDS).until(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean empty = smartcrons.getMetadata().stream().filter(m -> m.isActive()).count() == 0;
                return empty;
            }
        });

        // assert no identical instances
        Set<SmartcronMetadata> uniqueInstances = new HashSet<>();
        uniqueInstances.addAll(metadataInstances);
        Assert.assertEquals(metadataInstances.size(), uniqueInstances.size());
    }

    @Test
    public void noHistory() {

        // schedule smartcron
        counter = new Counter() {

            @Override
            public boolean executionHistory() {
                return false;
            }

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return delay(25, ChronoUnit.MILLIS);
            }
        };
        schedule();

        // wait for some executions
        await().until(counterCalled(5, true));

        // get metadata by deactivating instance
        smartcrons.deactivate(counter.getClass());
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata(counter.getClass());

        // assert metadata
        Assert.assertNotNull(metadata);
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertNotNull(smartcronMetadata);
        Assert.assertNull(smartcronMetadata.getScheduled());
        Assert.assertNotNull(smartcronMetadata.getHistory());
        Assert.assertTrue(smartcronMetadata.getHistory().isEmpty());
        Assert.assertEquals(counter.getClass().getName(), smartcronMetadata.getName());
    }

    @Test
    public void softError() {

        // schedule
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                context.setError("something happened");
                return null;
            }
        };
        smartcrons.schedule(counter);
        await().until(counterCalled(1, true));

        // take time to let statistics update
        sleep(100);

        // check metadata
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertFalse(smartcronMetadata.isActive());
        Assert.assertEquals(1, smartcronMetadata.getHistory().size());
        Assert.assertEquals("something happened", smartcronMetadata.getHistory().get(0).getError());
        Assert.assertEquals(1, smartcronMetadata.getStatistics().getCount());
        Assert.assertEquals(1, smartcronMetadata.getStatistics().getErrors());
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void historyOverflow() {

        // schedule
        counter = new Counter() {

            @Override
            public int maxExecutionHistorySize() {
                return 1;
            }

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                return delay(100, ChronoUnit.MILLIS);
            }
        };
        schedule();
        await().until(counterCalled(3, true));

        // check metadata
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertEquals(1, smartcronMetadata.getHistory().size());

        // shutdown
        smartcrons.shutdown();
    }

    @Test
    public void ignoreInHistory() {

        // schedule
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                context.setIgnoreInHistory(true);
                return delay(100, ChronoUnit.MILLIS);
            }
        };
        schedule();
        await().until(counterCalled(3, true));

        // check metadata
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertEquals(0, smartcronMetadata.getHistory().size());

        // shutdown
        smartcrons.shutdown();
    }

    @Test
    public void executionModes() {

        // schedule
        counter = new Counter() {

            @Override
            protected LocalDateTime calc(SmartcronExecutionContext context) {
                context.setMode("" + counter);
                return counter == 2 ? abort() : delay(100, ChronoUnit.MILLIS);
            }
        };
        schedule();
        await().until(counterCalled(2, true));
        smartcrons.deactivate(counter.getClass());

        // check metadata
        Set<SmartcronMetadata> metadata = smartcrons.getMetadata();
        Assert.assertEquals(1, metadata.size());
        SmartcronMetadata smartcronMetadata = metadata.iterator().next();
        Assert.assertEquals(2, smartcronMetadata.getStatistics().getCount());
        Assert.assertEquals(1, smartcronMetadata.getStatisticsPerMode().get("1").getCount());
        Assert.assertEquals(1, smartcronMetadata.getStatisticsPerMode().get("2").getCount());

        // shutdown
        smartcrons.shutdown();
    }

    private void schedule() {
        smartcrons.schedule(counter);
    }

    private Callable<Boolean> counterCalled(int count, boolean exact) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return exact ? counter.counter == count : counter.counter >= count;
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
                return smartcrons.getMetadata().stream().filter(m -> m.isActive()).count() == 0;
            }
        });
    }

    private ConditionFactory await() {
        return Awaitility.await().pollInterval(new Duration(10, TimeUnit.MILLISECONDS)).atMost(Duration.ONE_SECOND);
    }
}
