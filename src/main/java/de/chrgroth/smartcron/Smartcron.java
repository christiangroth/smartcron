package de.chrgroth.smartcron;

import java.time.LocalDateTime;

import de.chrgroth.smartcron.api.SmartcronTask;

/**
 * POJO representing scheduled smartcron task.
 * 
 * @author Christian Groth
 */
// TODO avoid changes to this instance!!
public class Smartcron {
	
	private final SmartcronTask task;
	
	private SmartcronTimerTask timerTask;
	private int executions;
	private LocalDateTime nextExecution;
	
	public Smartcron(SmartcronTask task) {
		this.task = task;
	}
	
	public SmartcronTask getTask() {
		return task;
	}
	
	public SmartcronTimerTask getTimerTask() {
		return timerTask;
	}
	
	public void setTimerTask(SmartcronTimerTask timerTask) {
		this.timerTask = timerTask;
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
