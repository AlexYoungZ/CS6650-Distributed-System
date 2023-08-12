import java.rmi.RemoteException;
import java.util.HashMap;

/** The Acceptor. */
public class Acceptor extends Server {
  // store accepted proposal
  // key: key from client request, value: proposal received with higher ID
  private HashMap<String, Proposal> acceptedMap = new HashMap<>();
  // store prepare request from proposers
  // key: key from client request,
  // value: highest promised proposal ID received for each client request
  private HashMap<String, Integer> preparedMap = new HashMap<>();
  /** The Client request. */
  ClientRequest clientRequest = new ClientRequest();

  /**
   * Instantiates a new Acceptor.
   *
   * @param address the address
   * @throws RemoteException the remote exception
   */
  public Acceptor(String address) throws RemoteException {
    super(address);
  }

  @Override
  void process(Task task) {
    switch (task.Type) {
      case Prepare:
        receivePrepare(task);
        break;
      case Accept:
        accept(task);
        break;
      default:
        System.out.println("Acceptor fails to process the task.");
        break;
    }
  }

  // receive prepare message from proposer
  private void receivePrepare(Task task) {
    if (preparedMap.containsKey(task.clientRequest.key)
        && preparedMap.get(task.clientRequest.key) > task.ProposalID) {
      System.out.println("The received prepare ID is not bigger, just ignore.");
      return;
    }

    // check if already accept
    Proposal proposal = acceptedMap.get(task.clientRequest.key);
    if (proposal != null) {
      // update the proposal ID and request to new promise task
      task.ProposalID = proposal.ProposalID;
      task.clientRequest = proposal.clientRequest;
    }

    // add promise task to proposer's task queue
    task.Type = TaskType.Promise;
    ServerInterface proposer = getServer("Proposer", task.ProposerID);
    if (proposer != null) {
      try {
        proposer.addTask(task);
      } catch (RemoteException e) {
        ServerLogger.serverExceptionLogging(
            "Fails to add promise task to proposer. Exception: " + e.getMessage(), serverNum);
        System.out.println("Fails to add promise task to proposer. Exception: " + e.getMessage());
      }
    }
  }

  // acceptor accept the proposal
  private void accept(Task task) {
    if (preparedMap.containsKey(task.clientRequest.key)
        && preparedMap.get(task.clientRequest.key) > task.ProposalID) {
      System.out.println("The received proposal ID is not bigger, just ignore.");
      return;
    }

    // System.out.println("Acceptor's accept check valid task client request: " +
    // task.clientRequest);

    Proposal proposal = acceptedMap.get(task.clientRequest.key);
    if (proposal == null || proposal.ProposalID <= task.ProposalID) {
      // if receive new proposal or received proposal with higher ID
      proposal = new Proposal();
      proposal.ProposalID = task.ProposalID;
      proposal.clientNumber = task.clientNumber;
      proposal.clientRequest = task.clientRequest;
      // System.out.println(proposal);
      // save in accepted map
      acceptedMap.put(task.clientRequest.key, proposal);
    }

    // create and send accept task to proposers
    task.Type = TaskType.Accepted;
    ServerInterface proposer = getServer("Proposer", task.ProposerID);
    if (proposer != null) {
      try {
        proposer.addTask(task);
      } catch (RemoteException e) {
        System.out.println("Fails to add accepted task to proposer. Exception: " + e.getMessage());
        ServerLogger.serverExceptionLogging(
            "Fails to add accepted task to proposer. Exception: " + e.getMessage(), serverNum);
      }
    }

    // create and send announce task to learners
    for (int learnerNumber : getServerWithRole("Learner")) {
      task.Type = TaskType.Announce;
      ServerInterface learner = getServer("Learner", learnerNumber);
      if (learner != null) {
        try {
          learner.addTask(task);
        } catch (RemoteException e) {
          System.out.println("Fails to add announce task to learner. Exception: " + e.getMessage());
          ServerLogger.serverExceptionLogging(
              "Fails to add announce task to learner. Exception: " + e.getMessage(), serverNum);
        }
        break;
      }
    }
  }
}
