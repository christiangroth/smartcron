Development: [![Build Status](https://secure.travis-ci.org/christiangroth/smartcron.svg)](http://travis-ci.org/christiangroth/smartcron)
[![Coverage Status](https://coveralls.io/repos/christiangroth/smartcron/badge.svg?branch=develop)](https://coveralls.io/r/christiangroth/smartcron?branch=develop)
[![Dependency Status](https://www.versioneye.com/user/projects/55389cb57f43bcd8890003e5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55389cb57f43bcd8890003e5)

Stable: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.smartcron/smartcron/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.smartcron/smartcron)

Smartcron
=========
Cron task handler allowing dynamic rescheduling after each execution.

Requirements
------------

- [Java SDK 1.8+][1]
- [slf4j][2]

Creating Smartcrons
-------------------
Implementing a new Smartcron instance is fairly easy.

	public class MySmartcron implements Smartcron {
		
		@Override
		public LocalDateTime run(SmartcronExecutionContext context) {
			
			// do stuff ...
			
			return ...;
		}
	}

Result of invoked Smartcron method is the next schedule date. You may calculate the date yourself or use one of interfaces default utility methods:

	@Override
	public Date run(SmartcronExecutionContext context) {

		boolean somethingBad = ...;
		if(somethingBad) {
		
			// abort this instance, returning null is also valid
			return abort();
		}
		
		// next in 200ms
		return delay(200, ChronoUnit.MILLIS);
	}

Scheduling Smartcron instances is done using an instance of Smartcrons:

	// create new controlling instance (only once)
	Smartcrons smartcrons = new Smartcrons();
	
	// schedule new smartcron
	smartcrons.schedule(new MySmartcron());
	
	// get information about your smartcron
	SmartcronMetadata myMeadata = smartcrons.getMetadata(MySmartcron.class);
	// ...
	
	// get information about all currently scheduled smartcrons
	for(SmartcronMetadata metadata : smartcrons.getMetadata()) {
	
	}
	
	// shutdown (cancels all scheduled smartcrons)
	smartcrons.shutdown();

Smartcrons Lifecycle
---------------------
Smartcron instances may be deactivated and activated again. Deactivated instances are also contained in metadata and not removed from internal list.

	// schedule new smartcron
	smartcrons.schedule(new MySmartcron());
	
	// deactivate / activate 
	smartcrons.deactivate(MySmartcron.class);
	smartcrons.activate(MySmartcron.class);
	
The activation status is also available via SmartcronMetadata. Keep in mind that deactivating a Smartcron and then rescheduling creates a second Smartcron of same type, the old deactivated one and the new scheduled and active one.

To cleanup deactivated Smartcrons and remove them just use the purge method. Metadata for these instances will no longer be available:

	// removes all deactivated smartcrons
	smartcrons.purge();

Modes, Reporting and History
----------------------------
Smartcrons framework contains an implicit but real simple reporting mechanism based on SmartcronMetadata. It provides current active status, next planned execution date and simple execution statistics (global and per mode).

Due to the fact that dynamic scheduling may lead to Smartcron implementations running in different modes with varying reschedule times you may use this fact for reporting, it is called mode. You may set the execution mode during execution using the SmartcronExecutionContext. Modes can be used to refine reporting i.e. considering execution count, average execution times and error ratio:

	public class MySmartcron implements Smartcron {
		
		@Override
		public LocalDateTime run(SmartcronExecutionContext context) {
			
			// check what to do
			if(needActionToday()) {
				
				// do something
				context.setMode("working");
				return rescheduleForTomorrow();
			}
			
			// nothing to do
			context.setMode("checking");
			return rescheduleForTomorrow();
		}
	}

Beside modes and execution statistics the metadata also contains configurable execution history. The history is enabled by default, but you may adapt this to your needs overwriting the following methods:

	public class MySmartcron implements Smartcron {
		
		@Override
		default boolean executionHistory() {
        	return true;
    	}
    	
		@Override
	    default int maxExecutionHistorySize() {
    	    return 10;
    	}
		
		@Override
		public LocalDateTime run(SmartcronExecutionContext context) {
			
			// do stuff ...
			
			return ...;
		}
	}

The execution context allows to ignore certain executions in history:

	if(whateverYourConditionIs()) {
		context.setIgnoreInHistory(true);
	}

Error Handling
--------------
You may mark an execution as failed providing an error message in th eexecution context.

	try {
		// ...
	} catch(Exception e) {
		context.setError(e.getMessage());
	}

In case the implemented Smartcron throws any exception, further executions will be aborted and Smartcron will be left in inactive state. You may mark the implementation as recoverable:

	public class MySmartcron implements Smartcron {
		
		private int recoveries = 0;
		
		@Override
		public boolean abortOnException() {
        	return false;
    	}
    	
		@Override
		public Date recover() {
			
			// count
			recoveries++;
			
			// decide
			if(recoveries < 10) {
				return delay(30, ChronoUnit.SECONDS);
			} else if(recoveries < 20 ) {
				return delay(180, ChronoUnit.SECONDS);
			} else {
				recoveries = 0;
	        	return abort();
	    	}
    	}
    	
		@Override
		public Date run(SmartcronExecutionContext context) {
			
			// do stuff ...
			
			return ...;
		}
	}

As shown above you may also use some instance variables to count number of recoveries and abort after reaching a certain threshold or adapt recovery times. Otherwise it may be possible to end up in an endless exception-recovery-loop.

Stateful Smartcrons
-------------------
If you need stateful Smartcrons, i.e. holding state between executions you may either resolve the state in the run method using some service or your simply use class members. The Smartcron instance won't be changed or (re-)instantiated by Smartcrons framework in any way.

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.slf4j.org/
