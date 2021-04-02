# Stream Aggregator

## Prerequisites
1. Java 1.8
2. Maven 3.x+ (Any version should work, I am using 3.6.3)

## Clone the repository
```
git clone https://github.com/omkardeshpande8/streamaggregator.git
```

## Execute the project
```
mvn clean compile exec:java -Dexec.mainClass="com.mycompany.streamaggregator.Driver"
```
Output will be printed on terminal and also will be written to logs/output.txt

## Alternative
If maven is not installed on the system, following options are available
### Run the artifact from the releases
Download the jar from the latest release and run
```
java -jar streamaggregator-1.0.jar 
```
### Github actions workflow
If you don't feel comfortable downloading jars from untrusted sources, there is a github action set up 
[here]:(https://github.com/omkardeshpande8/streamaggregator/actions/workflows/run.yml)
that executes the project. It takes the timeout in seconds as input.
