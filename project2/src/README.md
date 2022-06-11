<h3>Assignment overview<h3/>
The purpose of this assignment is to learn to implement client and server communication by remote procedure call compared with project one which uses socket communication;    

By learning the concepts and implementation of RPC, I learn that RPC's role in distributed system and client-server application. 
It allows that client makes local method where the calling procedure doesn't need to locate with the same environment(address space, operating system); 
When client first initiated the calling, the request parameters are transferred across the network to the server side where the procedure is actually executed; 
The client stub helps to marshall the parameters and format converting into a message, then the message is passed to the transport layer sent to the remote server. 
On the server side, the message is unmarshalled then the and server process the procedure then handle it to server stub to marshall again to transport back to the client.

Since this project is written in Java(object-oriented), as a natural way to play the similar role with RPC, remote method invocation is used to pass object(a concurrent hashmap service object in this case) rather than data to implement this project, 
therefore instead of procedural programming in RPC, I use objected-oriented programming way to pass object to provide more efficiency at the expense of platform mobility, 
besides, RMI creates less overhead and could be considered as a updated version of RPC, 
I could also add security and policy on both client and server side using SecurityManager.  

Above all, using RMI to implement client-server communication provides me with deeper understanding about RMI and RPC, 
different communication methods for client-server request-response model.  
<br>

<h3>Technical impression<h3/> 

I start this assignment by learning the fundamental knowledge about RPC and RMI from a high level. 
Then create a general plan about code structure, following the general RMI process, starts a rmi registry from command line, 
then need a remote interface which declares the method of executing client request, then a server implement the interface and instantiates the remote object. 
Next get the registry then associates the object with self-defined name(RMIServer), basically, RMI creates a thread to handle all client requests for the remote object. 
Convenient for me, the registry system handles the marshall and unmarshall job. 
As for the client side, by providing it with hostname and registry number, 
it first asks the registry with the server object name(RMIServer), 
the registry returns the reference of the remote object on server, 
therefore clients can call the remote method(execute) with the request arguments passed to the stub, 
which sent to the server with the method invoked; 
Finally The method runs on the server and return the response in the same way.        

Additionally, I add exception handling and logging for both client and server.      

One thing worth attention is since RMI is multi-thread in nature, 
I didn't need to put extra effort to create multi-threads for request and response, 
all I need to worry is how to ensure mutual exclusion to avoid the inconsistency brought by multi-client writing on the same time.    

The solution to this is I use concurrent hashmap to provide thread-safe operation at the expense of performance
(for cases like thread needs to wait when other writing) to avoid concurrent modification situation.
For multi-threads read(get) on concurrent hashmap it won't lock the object while for update operation(delete, put), the thread will lock the segment where the changes happen to handle mutual exclusion.

<h3>How to Run:<h3/>

First compile all files within src folder
```
MacBook-Pro-5:src siyangzhang$ javac *.java
```
<br>
next start registry service at < port > e.g. 4399 at one separate console:    

```
MacBook-Pro-5:src siyangzhang$ rmiregistry 4399
```    
<br> 
next start server with < port > e.g. 4399 at another separate console:

```
MacBook-Pro-5:src siyangzhang$ java Server 4399
RPCServer ready
```    
<br>
start client with < address > and < port >:

```
MacBook-Pro-5:src siyangzhang$ java Client localhost 4399
Enter text:
```

could start another client in the same way to test multi-threading.

```
MacBook-Pro-5:src siyangzhang$ java Client localhost 4399
Enter text:
```

<br>
Note that client use ClientLogger to write logs into clientLog.txt file and server use ServerLogger to write logs into serverLog.txt

<h3>Examples with description:<h3/>
 
first pre-populate some key-value pairs before five of each operation: 

Client one console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost 4399
Enter text: put(alex,123)
Put key: alex, value: 123 pair in map
Enter text: put(alexy, 111)
Put key: alexy, value: 111 pair in map
Enter text: put(bill, 000)
Put key: bill, value: 000 pair in map
Enter text: 
```
Client two console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost 4399
Enter text: put(cindy, 222)
Put key: cindy, value: 222 pair in map
Enter text: put(wang, 333)
Put key: wang, value: 333 pair in map
Enter text: 
```
Serve side console:    
```
MacBook-Pro-5:src siyangzhang$ java Server 4399
RPCServer ready
Server received request: put(alex,123)
Server response: Put key: alex, value: 123 pair in map
Server received request: put(alexy, 111)
Server response: Put key: alexy, value: 111 pair in map
Server received request: put(bill, 000)
Server response: Put key: bill, value: 000 pair in map
Server received request: put(cindy, 222)
Server response: Put key: cindy, value: 222 pair in map
Server received request: put(wang, 333)
Server response: Put key: wang, value: 333 pair in map
```    
<h4>Five of each operation</h4> 
including cases like put(update) new value with existing/none-existing key, get value with existing/none-existing key, delete key-value with existing/none-existing key.

Client one console:    
```
Enter text: get(alex)
Didn't find matching value with given key: alex
Enter text: get(cindy)
Get value: 222 with given key: cindy
Enter text: put(alex,0)
Put key: alex, value: 0 pair in map
Enter text: put(Dendi,8)
Put key: dendi, value: 8 pair in map
Enter text: put(Eric, 99)
Put key: eric, value: 99 pair in map
Enter text: delete(alex)
Delete value: 0 with given key: alex
Enter text: delete(alex)
Didn't find matching value with given key: alex
Enter text: delete(cindy)
Delete value: 222 with given key: cindy
Enter text: quit
 Received quit request from 192.168.1.66
Closing connection
MacBook-Pro-5:src siyangzhang$ 
```

Client two console:
```
Enter text: get(alex)
Get value: 11 with given key: alex
Enter text: get(bill)
Get value: 88 with given key: bill
Enter text: get(cindy)
Get value: 222 with given key: cindy
Enter text: delete(alex)
Delete value: 11 with given key: alex
Enter text: get(alex)
Get value: 0 with given key: alex
Enter text: delete(dendi)
Delete value: 8 with given key: dendi
Enter text: delete(eric)
Delete value: 99 with given key: eric
Enter text: delete(alex)
Didn't find matching value with given key: alex
Enter text: quit
 Received quit request from 192.168.1.66
Closing connection
MacBook-Pro-5:src siyangzhang$ 
```
Server side console:    
```
Server received request: put(bill,88)
Server response: Put key: bill, value: 88 pair in map
Server received request: put(alex,11)
Server response: Put key: alex, value: 11 pair in map
Server received request: get(alex)
key: alex
Server response: Get value: 11 with given key: alex
Server received request: get(bill)
key: bill
Server response: Get value: 88 with given key: bill
Server received request: get(cindy)
key: cindy
Server response: Get value: 222 with given key: cindy
Server received request: delete(alex)
key: alex
Server response: Delete value: 11 with given key: alex
Server received request: get(alex)
key: alex
Server response: Didn't find matching value with given key: alex
Server received request: get(cindy)
key: cindy
Server response: Get value: 222 with given key: cindy
Server received request: put(alex,0)
Server response: Put key: alex, value: 0 pair in map
Server received request: get(alex)
key: alex
Server response: Get value: 0 with given key: alex
Server received request: put(Dendi,8)
Server response: Put key: dendi, value: 8 pair in map
Server received request: put(Eric, 99)
Server response: Put key: eric, value: 99 pair in map
Server received request: delete(dendi)
key: dendi
Server response: Delete value: 8 with given key: dendi
Server received request: delete(eric)
key: eric
Server response: Delete value: 99 with given key: eric
Server received request: delete(alex)
key: alex
Server response: Delete value: 0 with given key: alex
Server received request: delete(alex)
key: alex
Server response: Didn't find matching value with given key: alex
Server received request: delete(alex)
key: alex
Server response: Didn't find matching value with given key: alex
Server received request: delete(cindy)
key: cindy
Server response: Delete value: 222 with given key: cindy
Server received request: quit
Closing connection
Server response:  Received quit request from 192.168.1.66
Server received request: quit
Closing connection
Server response:  Received quit request from 192.168.1.66


```

 

<h4>Invalid input example:</h4>    
Client side console:
```
Enter text: adfafdfafd
 Received malformed request of length 10 from 192.168.1.66
Enter text: 
```

<h4>Exception handling example:</h4>    
Client side console:
```
Enter text: put(alex)
ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1
```
Server side console:
```
Server received request: put(alex)
Server received request: put(alex)
Server received request: adfafdfafd
Server response:  Received malformed request of length 10 from 192.168.1.66
```    
<h4>client logging example:</h4> 
client log has two types, send and receive  

<h5>Send Format:</h5>    
Client send: `<client input>` from `<hostname>` at `<port>` to perform `<operation type>` operation at `<client time>` client time    
<h5>Receive Format: </h5>    
Client receive server response: `<server response>` from `<server address>` with `<operation type>` operation at `<client time>` client time
```
Client send: put(alex,123) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:28:35.613 client time
Client receive server response: Put key: alex, value: 123 pair in map from localhost with PUT operation at 2022-06-10 22:28:35.627 client time
Client send: put(alexy, 111) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:28:43.629 client time
Client receive server response: Put key: alexy, value: 111 pair in map from localhost with PUT operation at 2022-06-10 22:28:43.629 client time
Client send: put(bill, 000) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:28:51.781 client time
Client receive server response: Put key: bill, value: 000 pair in map from localhost with PUT operation at 2022-06-10 22:28:51.782 client time
Client send: put(cindy, 222) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:29:10.370 client time
Client receive server response: Put key: cindy, value: 222 pair in map from localhost with PUT operation at 2022-06-10 22:29:10.383 client time
Client send: put(wang, 333) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:29:17.488 client time
Client receive server response: Put key: wang, value: 333 pair in map from localhost with PUT operation at 2022-06-10 22:29:17.488 client time
Client send: put(bill,88) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:31:14.594 client time
Client receive server response: Put key: bill, value: 88 pair in map from localhost with PUT operation at 2022-06-10 22:31:14.595 client time
Client send: put(alex,11) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:31:20.714 client time
Client receive server response: Put key: alex, value: 11 pair in map from localhost with PUT operation at 2022-06-10 22:31:20.714 client time
Client send: get(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:31:39.998 client time
Client receive server response: Get value: 11 with given key: alex from localhost with GET operation at 2022-06-10 22:31:39.999 client time
Client send: get(bill) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:31:46.411 client time
Client receive server response: Get value: 88 with given key: bill from localhost with GET operation at 2022-06-10 22:31:46.412 client time
Client send: get(cindy) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:31:52.947 client time
Client receive server response: Get value: 222 with given key: cindy from localhost with GET operation at 2022-06-10 22:31:52.948 client time
Client send: delete(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:32:01.139 client time
Client receive server response: Delete value: 11 with given key: alex from localhost with DELETE operation at 2022-06-10 22:32:01.139 client time
Client send: get(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:32:06.340 client time
Client receive server response: Didn't find matching value with given key: alex from localhost with GET operation at 2022-06-10 22:32:06.341 client time
Client send: get(cindy) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:32:20.939 client time
Client receive server response: Get value: 222 with given key: cindy from localhost with GET operation at 2022-06-10 22:32:20.939 client time
Client send: put(alex,0) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:32:30.299 client time
Client receive server response: Put key: alex, value: 0 pair in map from localhost with PUT operation at 2022-06-10 22:32:30.300 client time
Client send: get(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform GET operation at 2022-06-10 22:32:34.789 client time
Client receive server response: Get value: 0 with given key: alex from localhost with GET operation at 2022-06-10 22:32:34.789 client time
Client send: put(Dendi,8) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:32:56.536 client time
Client receive server response: Put key: dendi, value: 8 pair in map from localhost with PUT operation at 2022-06-10 22:32:56.537 client time
Client send: put(Eric, 99) from MacBook-Pro-5.local at 192.168.1.66 to perform PUT operation at 2022-06-10 22:33:03.027 client time
Client receive server response: Put key: eric, value: 99 pair in map from localhost with PUT operation at 2022-06-10 22:33:03.027 client time
Client send: delete(dendi) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:11.702 client time
Client receive server response: Delete value: 8 with given key: dendi from localhost with DELETE operation at 2022-06-10 22:33:11.702 client time
Client send: delete(eric) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:18.315 client time
Client receive server response: Delete value: 99 with given key: eric from localhost with DELETE operation at 2022-06-10 22:33:18.316 client time
Client send: delete(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:25.437 client time
Client receive server response: Delete value: 0 with given key: alex from localhost with DELETE operation at 2022-06-10 22:33:25.437 client time
Client send: delete(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:29.073 client time
Client receive server response: Didn't find matching value with given key: alex from localhost with DELETE operation at 2022-06-10 22:33:29.073 client time
Client send: delete(alex) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:34.618 client time
Client receive server response: Didn't find matching value with given key: alex from localhost with DELETE operation at 2022-06-10 22:33:34.619 client time
Client send: delete(cindy) from MacBook-Pro-5.local at 192.168.1.66 to perform DELETE operation at 2022-06-10 22:33:41.727 client time
Client receive server response: Delete value: 222 with given key: cindy from localhost with DELETE operation at 2022-06-10 22:33:41.728 client time
Client send: quit from MacBook-Pro-5.local at 192.168.1.66 to perform No valid operation operation at 2022-06-10 22:33:48.727 client time
Client receive server response:  Received quit request from 192.168.1.66 from localhost with No valid operation operation at 2022-06-10 22:33:48.727 client time
Client quit at 2022-06-10 22:33:48.728 client time
Client send: quit from MacBook-Pro-5.local at 192.168.1.66 to perform No valid operation operation at 2022-06-10 22:33:53.845 client time
Client receive server response:  Received quit request from 192.168.1.66 from localhost with No valid operation operation at 2022-06-10 22:33:53.845 client time
Client quit at 2022-06-10 22:33:53.845 client time
Client send: get(eric) from MacBook-Pro-5.local at 127.0.0.1 to perform GET operation at 2022-06-10 22:37:17.458 client time
Client receive server response: Didn't find matching value with given key: eric from localhost with GET operation at 2022-06-10 22:37:17.470 client time
Client get: java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1 at 2022-06-10 22:40:08.322 client time
Client send: adfafdfafd from MacBook-Pro-5.local at 127.0.0.1 to perform No valid operation operation at 2022-06-10 22:40:38.595 client time
Client receive server response:  Received malformed request of length 10 from 192.168.1.66 from localhost with No valid operation operation at 2022-06-10 22:40:38.606 client time
```
<h4>server logging example:</h4>
<h5>Server receive log format:</h5>

Server receive: `<client request>` from `<client address>` at `port` and perform `<operation type>` operation at `<server time>` with server response `<server response>`

<h5>Server exception log format:</h5>
Server get: `<exception>` at `<server time>` server time
```
Server receive: put(alex,123) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:28:35.551 server time with server response: Put key: alex, value: 123 pair in map
Server receive: put(alexy, 111) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:28:43.628 server time with server response: Put key: alexy, value: 111 pair in map
Server receive: put(bill, 000) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:28:51.781 server time with server response: Put key: bill, value: 000 pair in map
Server receive: put(cindy, 222) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:29:10.347 server time with server response: Put key: cindy, value: 222 pair in map
Server receive: put(wang, 333) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:29:17.487 server time with server response: Put key: wang, value: 333 pair in map
Server receive: put(bill,88) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:31:14.594 server time with server response: Put key: bill, value: 88 pair in map
Server receive: put(alex,11) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:31:20.713 server time with server response: Put key: alex, value: 11 pair in map
Server receive: get(alex) from 192.168.1.66 and perform GET operation at 2022-06-10 22:31:39.998 server time with server response: Get value: 11 with given key: alex
Server receive: get(bill) from 192.168.1.66 and perform GET operation at 2022-06-10 22:31:46.411 server time with server response: Get value: 88 with given key: bill
Server receive: get(cindy) from 192.168.1.66 and perform GET operation at 2022-06-10 22:31:52.947 server time with server response: Get value: 222 with given key: cindy
Server receive: delete(alex) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:32:01.138 server time with server response: Delete value: 11 with given key: alex
Server receive: get(alex) from 192.168.1.66 and perform GET operation at 2022-06-10 22:32:06.339 server time with server response: Didn't find matching value with given key: alex
Server receive: get(cindy) from 192.168.1.66 and perform GET operation at 2022-06-10 22:32:20.938 server time with server response: Get value: 222 with given key: cindy
Server receive: put(alex,0) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:32:30.299 server time with server response: Put key: alex, value: 0 pair in map
Server receive: get(alex) from 192.168.1.66 and perform GET operation at 2022-06-10 22:32:34.788 server time with server response: Get value: 0 with given key: alex
Server receive: put(Dendi,8) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:32:56.536 server time with server response: Put key: dendi, value: 8 pair in map
Server receive: put(Eric, 99) from 192.168.1.66 and perform PUT operation at 2022-06-10 22:33:03.026 server time with server response: Put key: eric, value: 99 pair in map
Server receive: delete(dendi) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:11.702 server time with server response: Delete value: 8 with given key: dendi
Server receive: delete(eric) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:18.315 server time with server response: Delete value: 99 with given key: eric
Server receive: delete(alex) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:25.436 server time with server response: Delete value: 0 with given key: alex
Server receive: delete(alex) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:29.072 server time with server response: Didn't find matching value with given key: alex
Server receive: delete(alex) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:34.618 server time with server response: Didn't find matching value with given key: alex
Server receive: delete(cindy) from 192.168.1.66 and perform DELETE operation at 2022-06-10 22:33:41.727 server time with server response: Delete value: 222 with given key: cindy
Server receive: quit from 192.168.1.66 and perform No operation operation at 2022-06-10 22:33:48.727 server time with server response:  Received quit request from 192.168.1.66
Server receive: quit from 192.168.1.66 and perform No operation operation at 2022-06-10 22:33:53.844 server time with server response:  Received quit request from 192.168.1.66
Server receive: get(eric) from 192.168.1.66 and perform GET operation at 2022-06-10 22:37:17.434 server time with server response: Didn't find matching value with given key: eric
Server receive: adfafdfafd from 192.168.1.66 and perform No operation operation at 2022-06-10 22:40:38.559 server time with server response:  Received malformed request of length 10 from 192.168.1.66
```

<h3>Assumption:  <h3/>
No special assumption
<h3>Limitation:  <h3/>  
No special limitation
<h3>Citation:   <h3/>
Java Tutorial PDF from Canvas module 