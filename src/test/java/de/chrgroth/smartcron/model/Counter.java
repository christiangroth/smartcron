package de.chrgroth.smartcron.model;

import java.time.LocalDateTime;

import de.chrgroth.smartcron.api.Smartcron;

public abstract class Counter implements Smartcron {

    public int counter;

    @Override
    public LocalDateTime run() {
        counter++;
        return calc();
    }

    protected abstract LocalDateTime calc();
}
