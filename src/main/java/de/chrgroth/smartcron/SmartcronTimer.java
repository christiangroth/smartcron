package de.chrgroth.smartcron;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.api.SmartcronExecutionContext;
import de.chrgroth.smartcron.model.SmartcronExecution;

/**
 * Timer task implementation for {@link Smartcron} instances.
 *
 * @author Christian Groth
 */
public class SmartcronTimer extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(SmartcronTimer.class);

    private final SmartcronHandler handler;
    private final LocalDateTime scheduled;

    SmartcronTimer(SmartcronHandler handler, LocalDateTime scheduled) {
        this.handler = handler;
        this.scheduled = scheduled;
    }

    @Override
    public void run() {

        // prepare
        Smartcron smartcron = handler.getSmartcron();
        String smartcronName = smartcron.getClass().getName();
        LocalDateTime started = null;
        long duration = 0;
        String error = null;
        LocalDateTime nextSchedule = null;

        // prepare
        SmartcronExecutionContext context = new SmartcronExecutionContext();
        started = LocalDateTime.now();
        try {

            // execute
            nextSchedule = smartcron.run(context);
            duration = ChronoUnit.MILLIS.between(started, LocalDateTime.now());

            // check for soft error
            String softError = context.getError();
            if (softError != null && !softError.trim().isEmpty()) {
                LOG.warn("smartcron " + smartcronName + " finished with error: " + softError);
                error = softError;
            }
        } catch (Exception e) {

            // finished with hard error
            duration = ChronoUnit.MILLIS.between(started, LocalDateTime.now());
            error = e.getMessage();
            LOG.warn("smartcron " + smartcronName + " crashed: " + error, e);
            if (smartcron.abortOnException()) {
                LOG.error("no further executions will be planned for " + smartcronName + ".", e);
            } else {
                LOG.info("recovering " + smartcronName, e);
                nextSchedule = smartcron.recover();
            }
        }

        // invoke callback
        handler.smartcronExecutionCallback(context, new SmartcronExecution(scheduled, started, context.getMode(), duration, error, nextSchedule));
    }
}
