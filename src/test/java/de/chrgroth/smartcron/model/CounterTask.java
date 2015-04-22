package de.chrgroth.smartcron.model;

import de.chrgroth.smartcron.api.SmartcronResult;
import de.chrgroth.smartcron.api.SmartcronTask;

public class CounterTask implements SmartcronTask {
	
	public SmartcronResult execution;
	public int counter;
	
	@Override
	public SmartcronResult run() {
		counter++;
		return execution;
	}
}
