package de.chrgroth.smartcron;

import java.util.Date;

import de.chrgroth.smartcron.api.Smartcron;

/**
 * Immutable metadata about smartcron instances.
 * 
 * @author cgroth
 */
public class SmartcronMetadata {
	
	private final Smartcron smartcron;
	private final int executions;
	private final Date nextExecution;
	
	public SmartcronMetadata(Smartcron smartcron, int executions, Date nextExecution) {
		this.smartcron = smartcron;
		this.executions = executions;
		this.nextExecution = nextExecution;
	}
	
	public Smartcron getSmartcron() {
		return smartcron;
	}
	
	public int getExecutions() {
		return executions;
	}
	
	public Date getNextExecution() {
		return nextExecution;
	}
}
