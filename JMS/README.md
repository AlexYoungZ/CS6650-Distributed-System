<h2>Step to run JMS:</h2>

<h3>build project use maven pom.xml file</h3>
within JMS folder run `mvn install` 

<h3>Install and run activemq locally:</h3>
First use `brew` to install ```apache-activemq ```  with this link [apache-activemq](https://formulae.brew.sh/formula/activemq)    
run `brew install apache-activemq` to install        
then run `brew services start activemq` to start the service with default address `http://localhost:8161/admin`,     
default username and password to login in admin console is both `admin`   
if it's not responding for some time, try restart it:
```
MacBook-Pro-5:~ siyangzhang$ brew services start activemq
==> Successfully started `activemq` (label: homebrew.mxcl.activemq)
MacBook-Pro-5:~ siyangzhang$ brew services start activemq
Service `activemq` already started, use `brew services restart activemq` to restart.
MacBook-Pro-5:~ siyangzhang$ brew services restart activemq
Stopping `activemq`... (might take a while)
==> Successfully stopped `activemq` (label: homebrew.mxcl.activemq)
==> Successfully started `activemq` (label: homebrew.mxcl.activemq)
MacBook-Pro-5:~ siyangzhang$ 
```
Then open `http://localhost:8161/admin` visit Queues to check details.
<h3>run Mailbox Sender:</h3>
after building project, run Sender use Intellij
```
"/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home/bin/java" -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=57606:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/siyangzhang/CS6650-Distributed-System/JMS/target/classes:/Users/siyangzhang/.m2/repository/org/apache/activemq/activemq-all/5.17.1/activemq-all-5.17.1.jar:/Users/siyangzhang/Downloads/javax.jms.jar:/Users/siyangzhang/.m2/repository/org/slf4j/slf4j-log4j12/2.0.0-alpha5/slf4j-log4j12-2.0.0-alpha5.jar:/Users/siyangzhang/.m2/repository/org/slf4j/slf4j-api/2.0.0-alpha5/slf4j-api-2.0.0-alpha5.jar:/Users/siyangzhang/.m2/repository/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar:/Users/siyangzhang/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar Sender
JMS Server is running on: failover://tcp://localhost:61616
 INFO | Successfully connected to tcp://localhost:61616
Enter the new notification to send or 'quit' to exit: 
final hi
Sent message: 'final hi' on queue: Mailbox
Enter the new notification to send or 'quit' to exit: 
second final hi
Sent message: 'second final hi' on queue: Mailbox
Enter the new notification to send or 'quit' to exit: 
thrid final hi
Sent message: 'thrid final hi' on queue: Mailbox
Enter the new notification to send or 'quit' to exit: 
quit
Stop the Mailbox service
Process finished with exit code 0
```
<h3>run Mailbox Receiver:</h3>
then run Receiver the same way
```
"/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home/bin/java" -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=57644:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8 -classpath /Users/siyangzhang/CS6650-Distributed-System/JMS/target/classes:/Users/siyangzhang/.m2/repository/org/apache/activemq/activemq-all/5.17.1/activemq-all-5.17.1.jar:/Users/siyangzhang/Downloads/javax.jms.jar:/Users/siyangzhang/.m2/repository/org/slf4j/slf4j-log4j12/2.0.0-alpha5/slf4j-log4j12-2.0.0-alpha5.jar:/Users/siyangzhang/.m2/repository/org/slf4j/slf4j-api/2.0.0-alpha5/slf4j-api-2.0.0-alpha5.jar:/Users/siyangzhang/.m2/repository/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar:/Users/siyangzhang/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar Receiver
Client receive message: final hi

Process finished with exit code 1
```
each time run Receiver will receive the message in order from queue, second run:
```
Client receive message: second final hi

Process finished with exit code 1
```
third run: 
```
Client receive message: thrid final hi

Process finished with exit code 1
```

<h2>Step to run RMI</h2> 
need 3 separate terminals to simulate registry, client and server:
1. go to ```rmi``` directory, compile 4 java files like below: 
```
MacBook-Pro-5:rmi siyangzhang$ javac Sorter.java 
MacBook-Pro-5:rmi siyangzhang$ javac SorterImpl.java 
MacBook-Pro-5:rmi siyangzhang$ javac SorterServer.java 
MacBook-Pro-5:rmi siyangzhang$ javac SorterClient.java 
```

2. at first terminal : start registry service at port 3000
```
MacBook-Pro-5:rmi siyangzhang$ rmiregistry 3000
```

3. at second terminal: run server
```
MacBook-Pro-5:rmi siyangzhang$ java SorterServer
```

3. at third terminal: run client
```
MacBook-Pro-5:rmi siyangzhang$ java SorterClient
```
origin input integer array is 
```
Integer[] intArray = {1, 5, 4, 3, 12, 22, 1, 2, 6, 222};
```
Then will see sorted integer array returned at client console like: 
```
MacBook-Pro-5:rmi siyangzhang$ java SorterClient
[1, 1, 2, 3, 4, 5, 6, 12, 22, 222]
```