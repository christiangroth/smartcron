package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;

import de.chrgroth.smartcron.api.Smartcron;
import de.chrgroth.smartcron.api.SmartcronExecutionContext;

public abstract class Counter implements Smartcron {

    public int counter;

    @Override
    public LocalDateTime run(SmartcronExecutionContext context) {
        counter++;
        return calc(context);
    }

    protected abstract LocalDateTime calc(SmartcronExecutionContext context);
}
