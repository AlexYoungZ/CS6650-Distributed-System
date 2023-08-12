import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/** The Proposer. */
public class Proposer extends Server implements ProposerInterface {
  // the number of alive acceptors
  private int acceptorCount = 0;
  // ID of leader proposer
  private int leaderID;
  private int proposalID = 0;
  // proposal map with client update request key(get from task) as Key, proposal as value
  private HashMap<String, Proposal> proposals = new HashMap<>();
  private ClientRequest clientRequest;

  /**
   * Instantiates a new Proposer.
   *
   * @param address the address
   * @throws RemoteException the remote exception
   */
  public Proposer(String address) throws RemoteException {
    super(address);
    // get leader ID
    leaderID = Collections.min(getServerWithRole("Proposer"));
  }

  // create proposal ID
  public synchronized int createProposalID() {
    return ++proposalID;
  }

  @Override
  void process(Task task) {
    switch (task.Type) {
      case Request:
        receiveRequest(task);
        break;
      case Promise:
        receivePromise(task);
        break;
      case Accepted:
        receiveAccepted(task);
        break;
      default:
        System.out.println("Proposer cannot process invalid task.");
        break;
    }
  }

  // receive update request from client
  private void receiveRequest(Task task) {
    // get leader among proposers
    ProposerInterface leader = (ProposerInterface) getServer("Proposer", leaderID);
    if (leader == null) {
      return;
    }

    // create proposal ID
    try {
      proposalID = leader.createProposalID();
    } catch (RemoteException e) {
      System.out.println("Fails to create new proposal ID. Exception: " + e.getMessage());
      ServerLogger.serverExceptionLogging(
          "Fails to create new proposal ID. Exception: " + e.getMessage(), serverNum);
    }

    // initialize and save the proposal in proposal map
    Proposal proposal = new Proposal();
    proposal.ProposalID = proposalID;
    proposal.clientNumber = task.clientNumber;
    proposal.clientRequest = task.clientRequest;
    proposals.put(task.clientRequest.key, proposal);

    // initiate prepare task
    Task prepare = new Task();
    prepare.Type = TaskType.Prepare;
    prepare.ProposalID = proposalID;
    prepare.ProposerID = serverNum;
    prepare.clientNumber = task.clientNumber;
    prepare.clientRequest = task.clientRequest;

    // get acceptors
    ArrayList<Integer> acceptors = getServerWithRole("Acceptor");
    acceptorCount = acceptors.size();
    // send prepare task to acceptors
    for (int acceptorNum : acceptors) {
      ServerInterface acceptor = getServer("Acceptor", acceptorNum);
      if (acceptor != null) {
        try {
          acceptor.addTask(prepare);
        } catch (RemoteException e) {
          System.out.println(
              "Fails to add prepare task to acceptors. Exception: " + e.getMessage());
          ServerLogger.serverExceptionLogging(
              "Fails to add prepare task to acceptors. Exception: " + e.getMessage(), serverNum);
        }
      }
    }
  }

  // handle accepted response from acceptor
  private void receiveAccepted(Task task) {
    Proposal proposal = proposals.get(task.clientRequest.key);
    if (proposal == null) {
      System.out.println("Proposal key " + task.clientRequest.key + " not found in accepted map.");
      System.out.println("Could because it's first time receive this key");
      return;
    }
    // remove the accepted record, prepare for next round
    proposals.remove(task.clientRequest.key);
  }

  // handle promise response from acceptor
  private void receivePromise(Task task) {
    Proposal proposal = proposals.get(task.clientRequest.key);
    if (proposal == null) {
      System.out.println(
          "Proposal with key: " + task.clientRequest.key + " not found in promise map.");
      System.out.println("Could because it's first time receive this key");
      return;
    }

    // if receive newer proposal, accept the new one
    if (task.ProposalID > proposal.ProposalID) {
      System.out.println(
          "Replace old proposal ID: " + proposal.ProposalID + "with new ID: " + task.ProposalID);
      proposal.ProposalID = task.ProposalID;
      proposal.clientNumber = task.clientNumber;
      proposal.clientRequest = task.clientRequest;
    }

    // if proposer receive promise responses from a majority of acceptors
    if (++proposal.promiseNumReceived > acceptorCount / 2) {
      System.out.println("Proposer receive majority promises");
      // create accept task and add it to acceptor's task queue
      Task accept = new Task();
      accept.Type = TaskType.Accept;
      accept.ProposalID = proposal.ProposalID;
      accept.ProposerID = serverNum;
      accept.clientNumber = proposal.clientNumber;
      accept.clientRequest = proposal.clientRequest;
      // send accept task to acceptors
      for (int acceptorNum : getServerWithRole("Acceptor")) {
        ServerInterface acceptor = getServer("Acceptor", acceptorNum);
        if (acceptor != null) {
          try {
            acceptor.addTask(accept);
          } catch (RemoteException e) {
            System.out.println(
                "Fails to send accept task to acceptors. Exception: " + e.getMessage());
            ServerLogger.serverExceptionLogging(
                "Fails to send accept task to acceptors. " + "Exception: " + e.getMessage(),
                serverNum);
          }
        }
      }
      // avoid duplicate accept
      proposals.remove(task.clientRequest.key);
    }
  }
}
