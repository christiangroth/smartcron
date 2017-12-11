Release Notes
=====================

0.7.0 (in progress)
-------------------
- ...

0.6.0
-----
- replaced shared timer with instance per smartcron to allow parallel executions of smartcrons (each timer is backed by a single thread)
- added more logging

0.5.0
-----
- refactored internal smartcron handling
- smartcrons may be deactivated and activated, without removing
- added smartcron modes
- added smartcron statistics for better reporting even without history

0.4.0
-----
- limit execution history to prevent out of memory scenarios
- newest executions first in history
- added average duration and error percentage to execution metadata

0.3.0
-----
- fixed bug in delay calculation
- allowed smartcrons to be recoverable
- enhanced smartcron metadata
- switched to new Java 8 date time API

0.2.0
-----
- including java debug information
- thread name may be provided as parameter to Smartcrons constructor
- Smartcron delay working with ChroniUnit
- Bugfix: metadata list get's updated correctly

0.1.0
-----
- initial release