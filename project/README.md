<h3>Assignment overview<h3/>   
The purpose of this assignment is to learn the characteristics, principles of TCP and UDP client-server model by implementing them respectively.    


<h3>Technical impression<h3/> 
(200–500)
One problem I face is during set up UDP communication, because I want to enable client to be able to constantly send datagram(which implemented by a while loop, but lacking the check of datagram itself, leading to the scenario where client is continuously sending to server which quickly create a large log file at server side).

The solution to this is I add a simple if check to only send valid datagram which is not zero.  
<h3>How to Run:<h3/>   

<h3>Examples with description:<h3/> 

<h4>Sample Input:<h4/>
```

```

<h4>Sample Output:<h4/>
```

```
<h4>Explanation:<h4/>  


<h3>Assumption:  <h3/>

<h3>Limitation:  <h3/>  

<h3>Citation:   <h3/>


I implement the communication protocol at server side since normally application won't limit what client request would be like(make it easy for client to use), instead let server decide if client request is valid or not.    

Operations should be matched in this format:
1) PUT (key, value) 
2) GET (key)
3) DELETE(key)

TCP ServerLog example:
