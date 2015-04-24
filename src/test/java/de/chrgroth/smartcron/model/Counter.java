package de.chrgroth.smartcron.model;

import java.util.Date;

import de.chrgroth.smartcron.api.Smartcron;

public abstract class Counter implements Smartcron {
	
	public int counter;
	
	@Override
	public Date run() {
		counter++;
		return calc();
	}
	
	protected abstract Date calc();
}
