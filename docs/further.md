### Scalability
The current design involves a queue and a consumer. In order to scale this design, we can use a distributed queue and multiple consumer instances consuming from it. The data can be partitioned using the grouping key - device, title and country.
So all the records for the same key will be processed on the same instance.

### Variable volume
There are 2 ways to handle variation in volume - 
1. Proactive  - If the variation in volume is predictable or deterministic then we can scale the consumer instances up and down based on that information.
2. Reactive - We can monitor the traffic volume and/or backlog and scale up or down the consumer instances. 

In the current implementation, the buffer size is printed. If the buffer size keeps increasing, then we know that input rate is higher than processing rate.

### Productization
The output can be stored into the time-series database like Prometheus and then used to create dashboards using grafana. Prometheus will also allow us to set up alerting.

### Functional Testing
I have stored a few records in a text file, and it is used as input for correctness testing. The data in a file is meant to simulate the event buffer.
