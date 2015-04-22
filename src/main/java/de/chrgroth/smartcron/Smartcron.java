package de.chrgroth.smartcron;

import java.time.LocalDateTime;

import de.chrgroth.smartcron.api.SmartcronTask;

/**
 * POJO representing scheduled smartcron task.
 * 
 * @author Christian Groth
 */
public class Smartcron {
	
	private final SmartcronTask task;
	
	private int executions;
	private LocalDateTime nextExecution;
	
	public Smartcron(SmartcronTask task) {
		this.task = task;
	}
	
	public SmartcronTask getTask() {
		return task;
	}
	
	public int getExecutions() {
		return executions;
	}
	
	public void incExecutions() {
		executions++;
	}
	
	public LocalDateTime getNextExecution() {
		return nextExecution;
	}
	
	public void setNextExecution(LocalDateTime nextExecution) {
		this.nextExecution = nextExecution;
	}
}
