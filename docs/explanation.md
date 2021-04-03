###Assumptions
1. Data fits in memory.
2. The 1-second window interval is for the processing time, not event time.

###Design
There are 3 main components
1. Event buffer
2. Event source - Receives the events and adds to the event buffer
3. Event aggregator - Runs at the scheduled interval(1-second). Polls events from the buffer and aggregates them.

###Drawbacks
1. If the processing takes more than 1-second, then the subsequent aggregations will be delayed.
2. If shutdown hook cannot be invoked, the data in the buffer is lost.