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
		return delay(200);
	}

In case the implemented smartcron throws any exception, execution will be aborted too.

Scheduling smartcron instances is done using an instance of Smartcrons:

	// create new controlling instance (only one)
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