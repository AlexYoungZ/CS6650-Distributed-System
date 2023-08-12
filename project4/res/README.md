<h3>Assignment overview<h3/>
The purpose of this assignment is to learn to implement fault tolerance while achieving consensus of update among multiple replicas by Paxos,
compared with project three which involves two phase commit, lacking the ability of handling failure cases such as coordinators fails;    

By learning the concepts and implementation of Paxos, 
I first learn the purpose of consensus, in this project, we aim to achieve consistency among replicated server nodes within asynchronous network, especially the abortable consensus to handle contention among nodes.

Naturally, assign different roles(proposer, acceptor, learner) across servers and use a central database server as permanent and stable storage come as a solution.  

Since this project is written in Java(object-oriented), 
remote method invocation is used to pass object(various tasks, used to indicate the phase of Paxos, added on task queue on server)
to pass client request between client and servers, allowing replicas to communicate and share states with each other.    


Paxos is a two phase protocol, during the first prepare-promise phase, 
a proposer first receives an update request from a client, 
along with unique, increasing proposal number which is sent to at least majority of acceptors.
Then each acceptor receives the prepare message will check local ID, 
if first receive or receive a bigger ID, 
it will update this bigger ID in local map and response with a promise message to the proposer.    

Then during next propose-accept phase, after proposer receiving majority promise from alive acceptors. 
It will send propose(ID, task) request to all alive acceptors. 
If the ID is biggest at acceptor side, it will reply the accepted(ID, task) response to the proposer and learners.
 
<br>

<h3>Technical impression<h3/> 

I start this assignment by learning the fundamental knowledge about Paxos from a high level. 
Then create a general plan about code structure, following the general Paxos process, 
start from set up and clarify the roles of acceptor, proposer and learner along with whose methods and phases(indicate by different task type). 
then clarify the fields of proposal, task, and client request and try to start the skeleton of class
then create interface for client, server, database server and proposer which are required during casting and RMI lookup step.
then implement those interfaces with classes and some helper methods.    

For the client request to be sent, it is designed to be an object, due to RMI request, both client request and task need to implement Serializable interface.    
Each server could play different roles and will store the tasks within its task queue, by taking advantages of thread, one server could switch different roles to handle different tasks(with process method declaring in the abstract server class). The worker thread will handle tasks.
Each server is configured to have some random down time and after the failure could recovery and restart by a time scheduler, which enable simulating the case where some acceptors fail to response to check majority.


Additionally, I add exception handling and logging for client(each client has individual log files to separate).      

Similar to former project, synchronized method and concurrent hashmap are used to ensure mutual exclusion. 

<h3>How to Run:<h3/>

<h4>Make sure delete all log text files under `logs` folder before start<h4/>

First go within src folder to compile all files using the script `compile.sh`, which may need access and password, or you can click run button within Intellij project
```
MacBook-Pro-5:src siyangzhang$ sudo chmod +x ./compile.sh 
```
<br>

next within src folder start registry service at one separate console:    

```
MacBook-Pro-5:src siyangzhang$ rmiregistry 
```    

<br> 

next start servers with different roles `<DatabaseServer>`,`<Acceptor>`,`<Learner>`  at another separate console:    
if succeeds, the initial state of coordinator would show up like below

```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost DatabaseServer Acceptor Acceptor Acceptor Learner Learner Learner
Start DatabaseServer1
Start Acceptor1
Start Acceptor2
Start Acceptor3
Start Learner1
Start Learner2
Start Learner3
```    

<br> 

next start proposers in the same way as other roles above at a separate console:    
if succeeds, console would show up like below

```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost Proposer Proposer Proposer
Start Proposer1
Start Proposer2
Start Proposer3
```

<br>

Next start client1 with `<address>`:

```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client1
Enter text: 
```

could start another client in the same way to test multi-threading or consensus consistency.

```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client2
Enter text: 
```

<h3>Examples with description:<h3/>
 
first pre-populate some key-value pairs before five of each operation:
feel free to pre-populate any pair you want, no hard coding for this part. 

Client one console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client1
Enter text: put(alex,1)
Create put task: put(alex,1)
```
Client two console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client2
Enter text: put(bill,2)
Create put task: put(bill,2)
```
Server side console:

Server of acceptor and learner:
```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost DatabaseServer Acceptor Acceptor Acceptor Learner Learner Learner
Start DatabaseServer1
Start Acceptor1
Start Acceptor2
Start Acceptor3
Start Learner1
Start Learner2
Start Learner3
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server2
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server3
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server2
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server3
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Write, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server2
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server3
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server2
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server3
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Write, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} to Server1
```    
Server of proposer:
```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost Proposer Proposer Proposer
Start Proposer1
Start Proposer2
Start Proposer3
Add task: Task{Type=Request, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposal with key: alex not found in promise map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposal with key: bill not found in promise map.
Could because it's first time receive this key
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposal key bill not found in accepted map.
Could because it's first time receive this key
```

<h4>Five of each operation</h4> 
including cases like put(update) new value with existing/none-existing key, get value with existing/none-existing key, delete key-value with existing/none-existing key.
Client one console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client1
Enter text: put(alex,1)
Create put task: put(alex,1)
Enter text: put(alex,2)
Create put task: put(alex,2)
Enter text: put(alex,3)
Create put task: put(alex,3)
Enter text: get(alex)
key: alex
Client receive database server response(alex,3) of get request.
Enter text: put(cindy,1)
Create put task: put(cindy,1)
Enter text: get(dendy,5)
key: dendy,5
Client receive database server response(dendy,5,null) of get request.
Enter text: get(alex)
key: alex
Client receive database server response(alex,null) of get request.
Enter text: get(bill)
key: bill
Client receive database server response(bill,null) of get request.
Enter text: delete(cindy)
key: cindy
Create delete task: Delete(cindy,)
```
Client two console:
```
MacBook-Pro-5:src siyangzhang$ java Client localhost
Start client2
Enter text: put(bill,2)
Create put task: put(bill,2)
Enter text: put(alex,4)
Create put task: put(alex,4)
Enter text: get(cindy)
key: cindy
Client receive database server response(cindy,1) of get request.
Enter text: put(dendy,5)
Create put task: put(dendy,5)
Enter text: get(dendy)
key: dendy
Client receive database server response(dendy,5) of get request.
Enter text: delete(alex)
key: alex
Create delete task: Delete(alex,)
Enter text: delete(bill)
key: bill
Create delete task: Delete(bill,)
Enter text: get(cindy)
key: cindy
Client receive database server response(cindy,null) of get request.
Enter text: delete(cindy)
key: cindy
Enter text: get(cindy)
key: cindy
Client receive database server response(cindy,null) of get request.
```
Server side console:

Server of acceptor and learner:
```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost DatabaseServer Acceptor Acceptor Acceptor Learner Learner Learner
Start DatabaseServer1
Start Acceptor1
Start Acceptor2
Start Acceptor3
Start Learner1
Start Learner2
Start Learner3
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server2
Add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server3
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server2
Add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server3
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Write, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server2
Add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server3
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server2
Add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server3
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Write, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} to Server1
Server2 fails while expected to recovery in 0s.
Start Acceptor2
Server2 expected to fail in 96s.
Server1 fails while expected to recovery in 4s.
Start DatabaseServer1
Server1 expected to fail in 63s.
Server1 fails while expected to recovery in 1s.
Start Acceptor1
Server1 expected to fail in 65s.
Server3 fails while expected to recovery in 3s.
Start Learner3
Server3 expected to fail in 86s.
Server3 fails while expected to recovery in 3s.
Server1 fails while expected to recovery in 6s.
Start Acceptor3
Server3 expected to fail in 103s.
Server2 fails while expected to recovery in 5s.
Start Learner1
Server1 expected to fail in 69s.
Start Learner2
Server2 expected to fail in 74s.
Server1 fails while expected to recovery in 9s.
Start DatabaseServer1
Server1 expected to fail in 113s.
Server1 fails while expected to recovery in 4s.
Start Acceptor1
Server1 expected to fail in 61s.
Server2 fails while expected to recovery in 8s.
Start Acceptor2
Server2 expected to fail in 106s.
Server3 fails while expected to recovery in 6s.
Start Learner3
Server3 expected to fail in 110s.
Server1 fails while expected to recovery in 3s.
Start Learner1
Server1 expected to fail in 64s.
Server2 fails while expected to recovery in 7s.
Start Learner2
Server2 expected to fail in 89s.
Server3 fails while expected to recovery in 6s.
Server1 fails while expected to recovery in 6s.
Start Acceptor1
Server1 expected to fail in 91s.
Start Acceptor3
Server3 expected to fail in 96s.
Server1 fails while expected to recovery in 7s.
Start Learner1
Server1 expected to fail in 60s.
Server1 fails while expected to recovery in 1s.
Start DatabaseServer1
Server1 expected to fail in 114s.
Server2 fails while expected to recovery in 2s.
Start Acceptor2
Server2 expected to fail in 117s.
Server2 fails while expected to recovery in 1s.
Start Learner2
Server2 expected to fail in 60s.
Add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server2
Add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server3
Add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server2
Add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server3
Add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Write, clientRequest=put(alex,2), ProposerID=0, ProposalID=0} to Server1
Server3 fails while expected to recovery in 2s.
Start Learner3
Server3 expected to fail in 61s.
Add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server2
Add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server3
Add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server2
Add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server3
Add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Write, clientRequest=put(alex,3), ProposerID=0, ProposalID=0} to Server1
Server1 fails while expected to recovery in 5s.
Start Acceptor1
Server1 expected to fail in 115s.
Server3 fails while expected to recovery in 1s.
Start Acceptor3
Server3 expected to fail in 78s.
Server1 fails while expected to recovery in 4s.
Start Learner1
Server1 expected to fail in 64s.
Database server receives get(alex) from 127.0.0.1
Add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server2
Add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server3
Add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server2
Add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server3
Add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Write, clientRequest=put(cindy,1), ProposerID=0, ProposalID=0} to Server1
Database server receives get(cindy) from 127.0.0.1
Server2 fails while expected to recovery in 5s.
Start Learner2
Server2 expected to fail in 102s.
Server3 fails while expected to recovery in 0s.
Start Learner3
Server3 expected to fail in 97s.
Add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server2
Add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server3
Add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server2
Add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server3
Add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Write, clientRequest=put(dendy,5), ProposerID=0, ProposalID=0} to Server1
Database server receives get(dendy,5) from 127.0.0.1
Server1 fails while expected to recovery in 3s.
Start DatabaseServer1
Server1 expected to fail in 78s.
Database server receives get(dendy) from 127.0.0.1
Server1 fails while expected to recovery in 9s.
Add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server1
Add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server2
Add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server3
Add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server1
Add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server2
Add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server3
Add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server2
Add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server2
Add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server2
Add task: Task{Type=Write, clientRequest=Delete(alex,), ProposerID=0, ProposalID=0} to Server1
Start Learner1
Server1 expected to fail in 87s.
Server3 fails while expected to recovery in 9s.
Database server receives get(alex) from 127.0.0.1
Server2 fails while expected to recovery in 9s.
Start Acceptor2
Server2 expected to fail in 95s.
Add task: Task{Type=Prepare, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Add task: Task{Type=Prepare, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server2
Add task: Task{Type=Accept, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Add task: Task{Type=Accept, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server2
Add task: Task{Type=Announce, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Add task: Task{Type=Announce, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Add task: Task{Type=Write, clientRequest=Delete(bill,), ProposerID=0, ProposalID=0} to Server1
Database server receives get(bill) from 127.0.0.1
Start Acceptor3
Server3 expected to fail in 107s.
Add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server2
Add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server3
Add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server2
Add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server3
Add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Add task: Task{Type=Write, clientRequest=Delete(cindy,), ProposerID=0, ProposalID=0} to Server1
Database server receives get(cindy) from 127.0.0.1
```    
Server of proposer:
```
MacBook-Pro-5:src siyangzhang$ java ServerDriver localhost Proposer Proposer Proposer
Start Proposer1
Start Proposer2
Start Proposer3
Add task: Task{Type=Request, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposal with key: alex not found in promise map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposal with key: bill not found in promise map.
Could because it's first time receive this key
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Server3 fails while expected to recovery in 0s.
Start Proposer3
Server3 expected to fail in 81s.
Server1 fails while expected to recovery in 0s.
Start Proposer1
Server1 expected to fail in 60s.
Server2 fails while expected to recovery in 6s.
Start Proposer2
Server2 expected to fail in 109s.
Server1 fails while expected to recovery in 0s.
Start Proposer1
Server1 expected to fail in 97s.
Server3 fails while expected to recovery in 2s.
Start Proposer3
Server3 expected to fail in 81s.
Server2 fails while expected to recovery in 9s.
Start Proposer2
Server2 expected to fail in 90s.
Server1 fails while expected to recovery in 6s.
Server3 fails while expected to recovery in 7s.
Start Proposer1
Server1 expected to fail in 67s.
Start Proposer3
Server3 expected to fail in 61s.
Add task: Task{Type=Request, clientRequest=put(alex,2), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Proposal with key: alex not found in promise map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=put(alex,3), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Proposal with key: alex not found in promise map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Server2 fails while expected to recovery in 5s.
Server3 fails while expected to recovery in 9s.
Start Proposer2
Server2 expected to fail in 101s.
Server1 fails while expected to recovery in 1s.
Add task: Task{Type=Request, clientRequest=put(alex,4), ProposerID=0, ProposalID=0} to Server2
Fails to get server with role: Proposer and id: 1
Start Proposer1
Server1 expected to fail in 103s.
Start Proposer3
Server3 expected to fail in 98s.
Add task: Task{Type=Request, clientRequest=put(cindy,1), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Proposal with key: cindy not found in promise map.
Could because it's first time receive this key
Proposal key cindy not found in accepted map.
Could because it's first time receive this key
Proposal key cindy not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Proposal key cindy not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=put(dendy,5), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Proposal with key: dendy not found in promise map.
Could because it's first time receive this key
Proposal key dendy not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} to Server1
Proposal key dendy not found in accepted map.
Could because it's first time receive this key
Proposal key dendy not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=Delete(alex,), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} to Server1
Add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server1
Proposal with key: alex not found in promise map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server1
Add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} to Server1
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Proposal key alex not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=Delete(bill,), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} to Server1
Proposer receive majority promises
Add task: Task{Type=Accepted, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} to Server1
Proposal key bill not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Request, clientRequest=Delete(cindy,), ProposerID=0, ProposalID=0} to Server1
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Proposer receive majority promises
Add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} to Server1
Add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Proposal with key: cindy not found in promise map.
Could because it's first time receive this key
Proposal key cindy not found in accepted map.
Could because it's first time receive this key
Add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Proposal key cindy not found in accepted map.
Add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} to Server1
Could because it's first time receive this key
Proposal key cindy not found in accepted map.
Could because it's first time receive this key
Server2 fails while expected to recovery in 9s.
Server3 fails while expected to recovery in 7s.
Server1 fails while expected to recovery in 7s.
```    
ServerLogs:

Server1 log:
```
Server1 add task: Task{Type=Request, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} at 2022-07-31 12:41:23.270
Server1 add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.358
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.380
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.381
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.382
Server1 add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.392
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.397
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.399
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.402
Server1 add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.409
Server1 add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.411
Server1 add task: Task{Type=Announce, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.411
Server1 add task: Task{Type=Write, clientRequest=put(alex,1), ProposerID=0, ProposalID=0} at 2022-07-31 12:41:23.422
Server1 add task: Task{Type=Request, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} at 2022-07-31 12:41:28.599
Server1 add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.611
Server1 add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.617
Server1 add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.619
Server1 add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.620
Server1 add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.624
Server1 add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.626
Server1 add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.629
Server1 add task: Task{Type=Accepted, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.634
Server1 add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.637
Server1 add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.639
Server1 add task: Task{Type=Announce, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.640
Server1 add task: Task{Type=Write, clientRequest=put(bill,2), ProposerID=0, ProposalID=0} at 2022-07-31 12:41:28.643
Server1 fails while expected to recovery in 4s. at 2022-07-31 12:42:26.837
Server1 expected to fail in 63s. at 2022-07-31 12:42:30.845
Server1 fails while expected to recovery in 1s. at 2022-07-31 12:42:37.258
Server1 expected to fail in 65s. at 2022-07-31 12:42:38.266
Server1 fails while expected to recovery in 0s. at 2022-07-31 12:42:44.946
Server1 expected to fail in 60s. at 2022-07-31 12:42:44.947
Server1 fails while expected to recovery in 6s. at 2022-07-31 12:43:02.619
Server1 expected to fail in 69s. at 2022-07-31 12:43:08.339
Server1 fails while expected to recovery in 9s. at 2022-07-31 12:43:33.848
Server1 expected to fail in 113s. at 2022-07-31 12:43:42.851
Server1 fails while expected to recovery in 4s. at 2022-07-31 12:43:43.270
Server1 fails while expected to recovery in 0s. at 2022-07-31 12:43:44.952
Server1 expected to fail in 97s. at 2022-07-31 12:43:44.965
Server1 expected to fail in 61s. at 2022-07-31 12:43:47.277
Server1 fails while expected to recovery in 3s. at 2022-07-31 12:44:17.344
Server1 expected to fail in 64s. at 2022-07-31 12:44:20.348
Server1 fails while expected to recovery in 6s. at 2022-07-31 12:44:48.282
Server1 expected to fail in 91s. at 2022-07-31 12:44:52.232
Server1 fails while expected to recovery in 6s. at 2022-07-31 12:45:21.971
Server1 fails while expected to recovery in 7s. at 2022-07-31 12:45:24.350
Server1 expected to fail in 67s. at 2022-07-31 12:45:27.977
Server1 expected to fail in 60s. at 2022-07-31 12:45:31.360
Server1 fails while expected to recovery in 1s. at 2022-07-31 12:45:35.854
Server1 expected to fail in 114s. at 2022-07-31 12:45:36.860
Server1 add task: Task{Type=Request, clientRequest=put(alex,2), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:06.064
Server1 add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.076
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:46:06.080
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:46:06.080
Server1 add task: Task{Type=Promise, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:46:06.081
Server1 add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.084
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.086
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.088
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.089
Server1 add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.094
Server1 add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.095
Server1 add task: Task{Type=Announce, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.096
Server1 add task: Task{Type=Write, clientRequest=put(alex,2), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:06.100
Server1 add task: Task{Type=Request, clientRequest=put(alex,3), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:20.605
Server1 add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.614
Server1 add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:20.617
Server1 add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:20.617
Server1 add task: Task{Type=Promise, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:20.618
Server1 add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.621
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.623
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.624
Server1 add task: Task{Type=Accepted, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.626
Server1 add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.631
Server1 add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.631
Server1 add task: Task{Type=Announce, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.632
Server1 add task: Task{Type=Write, clientRequest=put(alex,3), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:20.636
Server1 fails while expected to recovery in 5s. at 2022-07-31 12:46:23.235
Server1 expected to fail in 115s. at 2022-07-31 12:46:28.240
Server1 fails while expected to recovery in 4s. at 2022-07-31 12:46:31.364
Server1 fails while expected to recovery in 1s. at 2022-07-31 12:46:34.984
Server1 expected to fail in 64s. at 2022-07-31 12:46:35.370
Server1 expected to fail in 103s. at 2022-07-31 12:46:35.988
Database Server1 receives get(alex) from 127.0.0.1 at 2022-07-31 12:46:46.827
Server1 add task: Task{Type=Request, clientRequest=put(cindy,1), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:53.622
Server1 add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.630
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.632
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.633
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.633
Server1 add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.636
Server1 add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.637
Server1 add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.638
Server1 add task: Task{Type=Accepted, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.639
Server1 add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.642
Server1 add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.643
Server1 add task: Task{Type=Announce, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.643
Server1 add task: Task{Type=Write, clientRequest=put(cindy,1), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:53.645
Database Server1 receives get(cindy) from 127.0.0.1 at 2022-07-31 12:46:58.711
Server1 add task: Task{Type=Request, clientRequest=put(dendy,5), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:14.031
Server1 add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.038
Server1 add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.040
Server1 add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.041
Server1 add task: Task{Type=Promise, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.042
Server1 add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.044
Server1 add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.046
Server1 add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.048
Server1 add task: Task{Type=Accepted, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.049
Server1 add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.052
Server1 add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.053
Server1 add task: Task{Type=Announce, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.053
Server1 add task: Task{Type=Write, clientRequest=put(dendy,5), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:14.056
Database Server1 receives get(dendy,5) from 127.0.0.1 at 2022-07-31 12:47:21.896
Server1 fails while expected to recovery in 3s. at 2022-07-31 12:47:30.866
Server1 expected to fail in 78s. at 2022-07-31 12:47:33.868
Database Server1 receives get(dendy) from 127.0.0.1 at 2022-07-31 12:47:38.766
Server1 fails while expected to recovery in 9s. at 2022-07-31 12:47:39.375
Server1 add task: Task{Type=Request, clientRequest=Delete(alex,), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:46.392
Server1 add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.401
Server1 add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:47:46.403
Server1 add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:47:46.403
Server1 add task: Task{Type=Promise, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:47:46.404
Server1 add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.406
Server1 add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.408
Server1 add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.411
Server1 add task: Task{Type=Accepted, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.411
Server1 add task: Task{Type=Write, clientRequest=Delete(alex,), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:46.418
Server1 expected to fail in 87s. at 2022-07-31 12:47:48.380
Database Server1 receives get(alex) from 127.0.0.1 at 2022-07-31 12:47:51.166
Server1 add task: Task{Type=Request, clientRequest=Delete(bill,), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:58.566
Server1 add task: Task{Type=Prepare, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.572
Server1 add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:47:58.575
Server1 add task: Task{Type=Promise, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:47:58.575
Server1 add task: Task{Type=Accept, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.579
Server1 add task: Task{Type=Accepted, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.582
Server1 add task: Task{Type=Accepted, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.583
Server1 add task: Task{Type=Announce, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.586
Server1 add task: Task{Type=Announce, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.587
Server1 add task: Task{Type=Write, clientRequest=Delete(bill,), ProposerID=0, ProposalID=0} at 2022-07-31 12:47:58.590
Database Server1 receives get(bill) from 127.0.0.1 at 2022-07-31 12:48:02.359
Server1 add task: Task{Type=Request, clientRequest=Delete(cindy,), ProposerID=0, ProposalID=0} at 2022-07-31 12:48:08.097
Server1 add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.106
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:48:08.111
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:48:08.111
Server1 add task: Task{Type=Promise, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:48:08.112
Server1 add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.116
Server1 add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.118
Server1 add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.121
Server1 add task: Task{Type=Accepted, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.121
Server1 add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.125
Server1 add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.126
Server1 add task: Task{Type=Announce, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.126
Server1 add task: Task{Type=Write, clientRequest=Delete(cindy,), ProposerID=0, ProposalID=0} at 2022-07-31 12:48:08.131
Database Server1 receives get(cindy) from 127.0.0.1 at 2022-07-31 12:48:12.847
Server1 fails while expected to recovery in 7s. at 2022-07-31 12:48:18.995
Server1 expected to fail in 75s. at 2022-07-31 12:48:22.614
Server1 fails while expected to recovery in 8s. at 2022-07-31 12:48:23.247
Database Server1 receives get(cindy) from 127.0.0.1 at 2022-07-31 12:48:26.441
Server1 expected to fail in 91s. at 2022-07-31 12:48:31.250
```  

Server2 log:
```
Server2 add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.371
Server2 add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.397
Server2 add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.613
Server2 add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.626
Server2 fails while expected to recovery in 0s. at 2022-07-31 12:42:26.419
Server2 expected to fail in 96s. at 2022-07-31 12:42:26.432
Server2 fails while expected to recovery in 6s. at 2022-07-31 12:42:53.568
Server2 expected to fail in 109s. at 2022-07-31 12:42:59.577
Server2 fails while expected to recovery in 5s. at 2022-07-31 12:43:03.331
Server2 expected to fail in 74s. at 2022-07-31 12:43:08.629
Server2 fails while expected to recovery in 8s. at 2022-07-31 12:44:02.438
Server2 expected to fail in 106s. at 2022-07-31 12:44:10.451
Server2 fails while expected to recovery in 7s. at 2022-07-31 12:44:22.637
Server2 expected to fail in 89s. at 2022-07-31 12:44:29.644
Server2 fails while expected to recovery in 9s. at 2022-07-31 12:44:48.584
Server2 expected to fail in 90s. at 2022-07-31 12:44:57.594
Server2 fails while expected to recovery in 2s. at 2022-07-31 12:45:56.464
Server2 expected to fail in 117s. at 2022-07-31 12:45:58.477
Server2 fails while expected to recovery in 1s. at 2022-07-31 12:45:58.651
Server2 expected to fail in 60s. at 2022-07-31 12:45:59.661
Server2 add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.077
Server2 add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.086
Server2 add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.615
Server2 add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.623
Server2 fails while expected to recovery in 5s. at 2022-07-31 12:46:27.600
Server2 expected to fail in 101s. at 2022-07-31 12:46:32.604
Server2 add task: Task{Type=Request, clientRequest=put(alex,4), ProposerID=0, ProposalID=0} at 2022-07-31 12:46:35.699
Server2 add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.631
Server2 add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.636
Server2 fails while expected to recovery in 5s. at 2022-07-31 12:46:59.663
Server2 expected to fail in 102s. at 2022-07-31 12:47:04.667
Server2 add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.039
Server2 add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.046
Server2 add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.402
Server2 add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.407
Server2 add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.414
Server2 add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.415
Server2 add task: Task{Type=Announce, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.415
Server2 fails while expected to recovery in 9s. at 2022-07-31 12:47:55.480
Server2 expected to fail in 95s. at 2022-07-31 12:47:58.313
Server2 add task: Task{Type=Prepare, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.573
Server2 add task: Task{Type=Accept, clientRequest=Delete(bill,), ProposerID=1, ProposalID=8} at 2022-07-31 12:47:58.582
Server2 add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.108
Server2 add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.118
Server2 fails while expected to recovery in 9s. at 2022-07-31 12:48:13.608
Server2 expected to fail in 105s. at 2022-07-31 12:48:24.237
``` 

Server3 log:
```
Server3 add task: Task{Type=Prepare, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.378
Server3 add task: Task{Type=Accept, clientRequest=put(alex,1), ProposerID=1, ProposalID=1} at 2022-07-31 12:41:23.399
Server3 add task: Task{Type=Prepare, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.617
Server3 add task: Task{Type=Accept, clientRequest=put(bill,2), ProposerID=1, ProposalID=2} at 2022-07-31 12:41:28.630
Server3 fails while expected to recovery in 0s. at 2022-07-31 12:42:38.163
Server3 expected to fail in 81s. at 2022-07-31 12:42:38.175
Server3 fails while expected to recovery in 3s. at 2022-07-31 12:42:42.209
Server3 expected to fail in 86s. at 2022-07-31 12:42:45.218
Server3 fails while expected to recovery in 3s. at 2022-07-31 12:43:00.205
Server3 expected to fail in 103s. at 2022-07-31 12:43:03.220
Server3 fails while expected to recovery in 2s. at 2022-07-31 12:43:59.181
Server3 expected to fail in 81s. at 2022-07-31 12:44:01.188
Server3 fails while expected to recovery in 6s. at 2022-07-31 12:44:11.220
Server3 expected to fail in 110s. at 2022-07-31 12:44:17.230
Server3 fails while expected to recovery in 6s. at 2022-07-31 12:44:46.225
Server3 expected to fail in 96s. at 2022-07-31 12:44:54.289
Server3 fails while expected to recovery in 7s. at 2022-07-31 12:45:22.195
Server3 expected to fail in 61s. at 2022-07-31 12:45:29.207
Server3 add task: Task{Type=Prepare, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.079
Server3 add task: Task{Type=Accept, clientRequest=put(alex,2), ProposerID=1, ProposalID=3} at 2022-07-31 12:46:06.087
Server3 fails while expected to recovery in 2s. at 2022-07-31 12:46:07.235
Server3 expected to fail in 61s. at 2022-07-31 12:46:09.245
Server3 add task: Task{Type=Prepare, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.616
Server3 add task: Task{Type=Accept, clientRequest=put(alex,3), ProposerID=1, ProposalID=4} at 2022-07-31 12:46:20.625
Server3 fails while expected to recovery in 9s. at 2022-07-31 12:46:30.213
Server3 fails while expected to recovery in 1s. at 2022-07-31 12:46:30.296
Server3 expected to fail in 78s. at 2022-07-31 12:46:31.305
Server3 expected to fail in 98s. at 2022-07-31 12:46:39.223
Server3 add task: Task{Type=Prepare, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.632
Server3 add task: Task{Type=Accept, clientRequest=put(cindy,1), ProposerID=1, ProposalID=5} at 2022-07-31 12:46:53.637
Server3 fails while expected to recovery in 0s. at 2022-07-31 12:47:10.249
Server3 expected to fail in 97s. at 2022-07-31 12:47:10.254
Server3 add task: Task{Type=Prepare, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.040
Server3 add task: Task{Type=Accept, clientRequest=put(dendy,5), ProposerID=1, ProposalID=6} at 2022-07-31 12:47:14.047
Server3 add task: Task{Type=Prepare, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.403
Server3 add task: Task{Type=Accept, clientRequest=Delete(alex,), ProposerID=1, ProposalID=7} at 2022-07-31 12:47:46.409
Server3 fails while expected to recovery in 9s. at 2022-07-31 12:47:49.308
Server3 expected to fail in 107s. at 2022-07-31 12:48:04.489
Server3 add task: Task{Type=Prepare, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.109
Server3 add task: Task{Type=Accept, clientRequest=Delete(cindy,), ProposerID=1, ProposalID=9} at 2022-07-31 12:48:08.120
Server3 fails while expected to recovery in 7s. at 2022-07-31 12:48:17.228
Server3 expected to fail in 101s. at 2022-07-31 12:48:26.004
``` 
ClientLogs:

Client1 log:
```
Client1 send: put(alex,1) at 2022-07-31 12:41:23.309
Client1 receive put(alex,1) at 2022-07-31 12:41:23.437
Client1 send: put(alex,2) at 2022-07-31 12:46:06.065
Client1 receive put(alex,2) at 2022-07-31 12:46:06.104
Client1 send: put(alex,3) at 2022-07-31 12:46:20.606
Client1 receive put(alex,3) at 2022-07-31 12:46:20.640
Client1 receive Client receive database server response(alex,3) of get request. at 2022-07-31 12:46:46.829
Client1 send: get(alex) at 2022-07-31 12:46:46.830
Client1 send: put(cindy,1) at 2022-07-31 12:46:53.623
Client1 receive put(cindy,1) at 2022-07-31 12:46:53.648
Client1 receive Client receive database server response(dendy,5,null) of get request. at 2022-07-31 12:47:21.897
Client1 send: get(dendy,5) at 2022-07-31 12:47:21.897
Client1 receive Client receive database server response(alex,null) of get request. at 2022-07-31 12:47:51.167
Client1 send: get(alex) at 2022-07-31 12:47:51.167
Client1 receive Client receive database server response(bill,null) of get request. at 2022-07-31 12:48:02.359
Client1 send: get(bill) at 2022-07-31 12:48:02.360
Client1 send: delete(cindy) at 2022-07-31 12:48:08.098
Client1 receive Delete(cindy,) at 2022-07-31 12:48:08.136
```   

Client2 log:
```
Client2 send: put(bill,2) at 2022-07-31 12:41:28.642
Client2 receive put(bill,2) at 2022-07-31 12:41:28.647
Client2 send: put(alex,4) at 2022-07-31 12:46:35.699
Client2 receive Client receive database server response(cindy,1) of get request. at 2022-07-31 12:46:58.715
Client2 send: get(cindy) at 2022-07-31 12:46:58.716
Client2 send: put(dendy,5) at 2022-07-31 12:47:14.032
Client2 receive put(dendy,5) at 2022-07-31 12:47:14.059
Client2 receive Client receive database server response(dendy,5) of get request. at 2022-07-31 12:47:38.767
Client2 send: get(dendy) at 2022-07-31 12:47:38.767
Client2 send: delete(alex) at 2022-07-31 12:47:46.393
Client2 receive Delete(alex,) at 2022-07-31 12:47:46.421
Client2 send: delete(bill) at 2022-07-31 12:47:58.567
Client2 receive Delete(bill,) at 2022-07-31 12:47:58.593
Client2 receive Client receive database server response(cindy,null) of get request. at 2022-07-31 12:48:12.848
Client2 send: get(cindy) at 2022-07-31 12:48:12.848
Client2 get: No working proposer. at 2022-07-31 12:48:21.432 client time
Client2 send: delete(cindy) at 2022-07-31 12:48:21.432
Client2 receive Client receive database server response(cindy,null) of get request. at 2022-07-31 12:48:26.442
Client2 send: get(cindy) at 2022-07-31 12:48:26.442
```

<h4>Notes About Acceptor and Proposer Failure Recovery:<h4/>

Add random server down time to both acceptor and proposer server, it's hard to control but can cover the levels case for both acceptor and proposer. 
Could be verified by server's log files.

<h4>Notes About Logging:<h4/>

`Client` use `ClientLogger` to write logs into separate `Client<Client number>Log.txt` 

`Server` use `ServerLogger` to write logs into separate `Server<Server number>Log.txt`    

<h3>Assumption:  <h3/>

Assume the database server as permanent storage. Client get request retrieve value from database server directly instead of the whole 2PC procedure.

After updating the database server, the task to announce the update will be assigned to any learner.

Some times cannot perform update since the database server fails, need to wait and retry again,
<h3>Limitation:  <h3/>  

Could add fail recovery case when all servers down and recovery from local log files. Could achieve this by associate logs with states(phase and task type) and client request.

<h3>Citation:   <h3/>
PDF from Canvas module 

