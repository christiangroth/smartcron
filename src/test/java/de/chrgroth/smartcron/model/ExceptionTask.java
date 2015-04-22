package de.chrgroth.smartcron.model;

import de.chrgroth.smartcron.api.SmartcronResult;
import de.chrgroth.smartcron.api.SmartcronTask;

public class ExceptionTask implements SmartcronTask {
	
	@Override
	public SmartcronResult run() {
		throw new IllegalStateException("expected exception");
	}
}
