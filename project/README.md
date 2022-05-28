<h3>Assignment overview<h3/>
The purpose of this assignment is to learn the characteristics, principles of TCP and UDP client-server model by implementing them respectively.    

TCP is a connection-based protocol where communication between client and server needs to set up first, and is guarantee the delivery and order of data sent, could be implemented with multiple error-checking mechanisms. Sequence of data is sent during transmission.    

As for UDP, which sends data by datagram packets, is a connection-less protocol therefore doesn't need set up before use, while the delivery and order of data sent is not reliable and only allow limited error checking mechanism like check-sum. Because of its relative-simpleness, the speed of UDP is faster than TCP and overhead is lower.    

Apart from the feature of TCP and UDP, I also practice implement server side logging mechanism, by setting up a basic hashmap including exception handling to see how server should handle client request, start from set up socket(stream for TCP and datagram for UDP), create buffer/read write stream to allow read client's request, process it, extract the specific operation type(get, delete, put) and perform on server's storage, and logging into log file with format and timestamp, finally sent response back to client.   

For the client side, I create socket with given server hostname and port number, add time-out mechanism and exception handling, use console to allow client input request, send client request to server and wait for response, log both request and response with timestamp.   

Above as a basic client-server model implementation provide me with deeper understanding about TCP/UDP, socket/datagram communication, client-server model, potential exception during data transmission process and its handling.  

<h3>Technical impression<h3/> 

I start this assignment by learning the fundamental knowledge about TCP/IP, socket/datagram communication from a high level. Then create a general plan about code structure, like two set of client-server models, one for TCP and the other for UDP, during the implementation process, I find that both TCP and UDP share some common code, like Logger code block and request handling part, therefore I get into refactoring after the general implementation is done. Another thing to mention is the logging part, I learn how to get timestamp and format it, refresh the knowledge about writing into a file, and exception handling, which all reminds me of the last internship I did at Index Exchange as software developer at cloud platform team, where the majority job is to get logging file into company's current ELK(elastic search, filebeat and Kibana) stack with certain formatting with json, the logging file is important for a product's operation monitoring, to see the workload performance, to avoid service failure or lagging.   

One problem I face is during set up UDP communication, because I want to enable client to be able to constantly send datagram instead of just send and quit (which implemented by a while loop, but lacking the check of datagram itself, leading to the scenario where client is continuously sending to server which quickly create a large log file at server side).

The solution to this is I add a simple if check to only send valid datagram which is not zero, and to avoid adding another loop within while loop, which could lead to infinite loop and quickly create a giant log file, dragging down the performance of the whole service.   

<h3>How to Run:<h3/>

I use two separate client-server pairs, one for TCP and the other for UDP.   

Go to src folder under client and server, first compile clients like below:    
```
MacBook-Pro-5:client siyangzhang$ javac TCPClient.java 
MacBook-Pro-5:client siyangzhang$ javac UDPClient.java
```   
then go to server folder and compile TCP server and UDP server like below:   
```
MacBook-Pro-5:server siyangzhang$ javac TCPServer.java 
MacBook-Pro-5:server siyangzhang$ javac UDPServer.java 
```    
next start server first then start client at another terminal:    
start server with given port number:
```
MacBook-Pro-5:server siyangzhang$ java TCPServer 3201
Server is listening on port: 3201
Client's connection success!
Received request: put(alex,123)
Server response: Put key: alex, value: 123 pair in map
```
start client and connect to server address and port:    
```
MacBook-Pro-5:client siyangzhang$ java TCPClient localhost 3201
Enter text: put(alex,123)
Server response: Put key: alex, value: 123 pair in map
Enter text: 
```   
same logic applies to UDP client and server.    
Note that both clients share the one logger(ClientLogger class) writing into one file name clientLog.txt   
both servers share one logger(ServerLogger class) and write into one file name serverLog.txt

<h3>Examples with description:<h3/>

TCP example:    
first pre-populate some key-value pairs before five of each operation:    
Client side console:
```
MacBook-Pro-5:client siyangzhang$ java TCPClient localhost 3201
Enter text: put(alex, 1)
Server response: Put key: alex, value: 1 pair in map
Enter text: put(bill, 2)
Server response: Put key: bill, value: 2 pair in map
Enter text: PUT(Cindy, 3)
Server response: Put key: cindy, value: 3 pair in map
Enter text: PUT(Daniel, 4)
Server response: Put key: daniel, value: 4 pair in map
```
Serve side console:    
```
MacBook-Pro-5:server siyangzhang$ java TCPServer 3201
Server is listening on port: 3201
Client's connection success!
Received request: put(alex, 1)
Server response: Put key: alex, value: 1 pair in map
Received request: put(bill, 2)
Server response: Put key: bill, value: 2 pair in map
Received request: PUT(Cindy, 3)
Server response: Put key: cindy, value: 3 pair in map
Received request: PUT(Daniel, 4)
Server response: Put key: daniel, value: 4 pair in map
```    
<h4>Five of each operation, including cases like put(update) new value with existing/none-existing key, get value with existing/none-existing key, delete key-value with existing/none-existing key.</h4>    
Client side console:    
```
Enter text: get(alex    ) 
Server response: Didn't find matching value with given key: alex    
Enter text: get(alex)
Server response: Get value: 1 with given key: alex
Enter text: get(adfaaf)
Server response: Didn't find matching value with given key: adfaaf
Enter text: put(alex,3)
Server response: Put key: alex, value: 3 pair in map
Enter text: get(alex)
Server response: Get value: 3 with given key: alex
Enter text: put(adfadaf,5)
Server response: Put key: adfadaf, value: 5 pair in map
Enter text: delete(alex)
Server response: Delete value: 3 with given key: alex
Enter text: get(alex)
Server response: Didn't find matching value with given key: alex
Enter text: delete(adfadaf)
Server response: Delete value: 5 with given key: adfadaf
Enter text: delete(adfafafaaaaa)
Server response: Didn't find matching value with given key: adfafafaaaaa
Enter text: quit
Server response:  Received quit request from 127.0.0.1 : 3201
Closing connection
MacBook-Pro-5:client siyangzhang$ 
```
Server side console:    
```
MacBook-Pro-5:server siyangzhang$ java TCPServer 3201
Server is listening on port: 3201
Client's connection success!
Received request: put(alex, 1)
Server response: Put key: alex, value: 1 pair in map
Received request: put(bill, 2)
Server response: Put key: bill, value: 2 pair in map
Received request: PUT(Cindy, 3)
Server response: Put key: cindy, value: 3 pair in map
Received request: PUT(Daniel, 4)
Server response: Put key: daniel, value: 4 pair in map
Received request: get(alex    )
key: alex    
Server response: Didn't find matching value with given key: alex    
Received request: get(alex)
key: alex
Server response: Get value: 1 with given key: alex
Received request: get(adfaaf)
key: adfaaf
Server response: Didn't find matching value with given key: adfaaf
Received request: put(alex,3)
Server response: Put key: alex, value: 3 pair in map
Received request: get(alex)
key: alex
Server response: Get value: 3 with given key: alex
Received request: put(adfadaf,5)
Server response: Put key: adfadaf, value: 5 pair in map
Received request: delete(alex)
key: alex
Server response: Delete value: 3 with given key: alex
Received request: get(alex)
key: alex
Server response: Didn't find matching value with given key: alex
Received request: delete(adfadaf)
key: adfadaf
Server response: Delete value: 5 with given key: adfadaf
Received request: delete(adfafafaaaaa)
key: adfafafaaaaa
Server response: Didn't find matching value with given key: adfafafaaaaa
Received request: quit
Closing connection
Server response:  Received quit request from 127.0.0.1 : 3201
Closing connection
```

<h4>Timeout Mechanism, set waiting time to 20s:</h4>
```
MacBook-Pro-5:client siyangzhang$ java TCPClient localhost 3201
Enter text: get(alex)
Server timed out: Read timed out
MacBook-Pro-5:client siyangzhang$ 
```
![time-out](res/time-out.png)    

<h4>Invalid input example:</h4>    
Client side console:
```
MacBook-Pro-5:client siyangzhang$ java TCPClient localhost 3201
Enter text: put(alex, 123   )
Server response: Put key: alex, value: 123 pair in map
Enter text: gadfalfdk
Server response:  Received malformed request of length 9 from 127.0.0.1 : 3201
Enter text: 
```
Server side console:    
```
MacBook-Pro-5:server siyangzhang$ java TCPServer 3201
Server is listening on port: 3201
Client's connection success!
Received request: put(alex, 123   )
Server response: Put key: alex, value: 123 pair in map
Received request: gadfalfdk
Server response:  Received malformed request of length 9 from 127.0.0.1 : 3201
```
Exception handling example:    
Client side console:
```
MacBook-Pro-5:client siyangzhang$ java TCPClient localhost 3201
Enter text: put(adafda)
Invalid response
Closing connection
MacBook-Pro-5:client siyangzhang$ 
```
Server side console:
```
MacBook-Pro-5:server siyangzhang$ java TCPServer 3201
Server is listening on port: 3201
Client's connection success!
Received request: put(adafda)
Client input is invalid: Index 1 out of bounds for length 1
MacBook-Pro-5:server siyangzhang$ 
```    
client logging example:    
```

```
server logging example:
```

```
<h4>UDP follows the same logic, example list below:</h4>
UDP client console:
```

```
UDP server console:
```

```
UDP client logging:
```

```
UDP server logging:
```

```

<h3>Assumption:  <h3/>
No special assumption
<h3>Limitation:  <h3/>  
No special limitation
<h3>Citation:   <h3/>
Java Tutorial PDF from Canvas module 