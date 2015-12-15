Development: [![Build Status](https://secure.travis-ci.org/christiangroth/smartcron.svg)](http://travis-ci.org/christiangroth/smartcron)
[![Coverage Status](https://coveralls.io/repos/christiangroth/smartcron/badge.svg?branch=develop)](https://coveralls.io/r/christiangroth/smartcron?branch=develop)
[![Dependency Status](https://www.versioneye.com/user/projects/55389cb57f43bcd8890003e5/badge.svg?style=flat)](https://www.versioneye.com/user/projects/55389cb57f43bcd8890003e5)

Stable: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.chrgroth.smartcron/smartcron/badge.svg)](http://search.maven.org/#artifactdetails|de.chrgroth.smartcron|smartcron)

Smartcron
=========
Cron task handler allowing dynamic rescheduling after each execution.

Requirements
------------

- [Java SDK 1.8+][1]
- [slf4j][2]

Usage
-----
Implementing a new smartcron instance is fairly easy.

	public class MySmartcron implements Smartcron {
		
		@Override
		public Date run() {
			
			// do stuff ...
			
			return ...;
		}
	}

Result of invoked smartcron method is the next schedule date. You may calculate the date yourself or use one of interfaces default utility methods:

	@Override
	public Date run() {

		boolean somethingBad = ...;
		if(somethingBad) {
		
			// abort this instance, returning null is also valid
			return abort();
		}
		
		// next in 200ms
		return delay(200, ChronoUnit.MILLIS);
	}

In case the implemented smartcron throws any exception, execution will be aborted too unless you marked the smartcron implementation as recoverable:

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
	        	return abort();
	    	}
    	}
    	
		@Override
		public Date run() {
			
			// do stuff ...
			
			return ...;
		}
	}

As shown above you may also use some instance variables to count number of recoveries and abort after reaching a certain threshold or adapt recovery times.

Scheduling smartcron instances is done using an instance of Smartcrons:

	// create new controlling instance (only once)
	Smartcrons smartcrons = new Smartcrons();
	
	// schedule new smartcron
	smartcrons.schedule(new MySmartcron());
	
	// get information about all currently scheduled smartcrons
	for(SmartcronMetadata metadata : smartcrons.getMetadata()) {
		// ...
	}
	
	// cancel smartcrons of type
	smartcrons.cancel(MySmartcron.class);
	
	// shutdown (cancels all scheduled smartcrons)
	smartcrons.shutdown();

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[2]: http://www.slf4j.org/