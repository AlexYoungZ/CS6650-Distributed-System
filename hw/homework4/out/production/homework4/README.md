<h2>Step to run JMS:</h2>
First use `brew` to install ```apache-activemq ```  with this link [apache-activemq](https://formulae.brew.sh/formula/activemq)    
run `brew install apache-activemq` to install        
then run `brew services start activemq` to start the service with default address `http://localhost:8161/admin`,     
default username and password is both `admin`

```
javac -classpath /Users/siyangzhang/CS6650
-Distributed-System/hw/homework4/lib/javax.jms.jar:/Users/siyangzhang/CS6650-Distributed-System/hw/homework4/src/jms/activemq-all-5.17.1.jar Sender.java
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