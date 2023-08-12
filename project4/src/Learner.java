import java.rmi.RemoteException;
import java.util.ArrayList;

/** The Learner. */
public class Learner extends Server {
  // store learned proposals by proposal ID
  private ArrayList<Integer> learnedProposals = new ArrayList<>();

  /**
   * Instantiates a new Learner.
   *
   * @param address the address
   * @throws RemoteException the remote exception
   */
  public Learner(String address) throws RemoteException {
    super(address);
  }

  @Override
  void process(Task task) throws RemoteException {
    if (task.Type == TaskType.Announce) {
      announce(task);
    } else {
      System.out.println("Learner fails to process the task.");
      ServerLogger.serverExceptionLogging("Learner fails to process the task.", serverNum);
    }
  }

  // update database server and announce the response to client
  private void announce(Task task) throws RemoteException {
    if (learnedProposals.contains(task.ProposalID)) {
      // avoid duplicate announce
      return;
    }
    learnedProposals.add(task.ProposalID);

    // create update task in server hosting database
    Task updateTask = new Task();
    updateTask.Type = TaskType.Write;
    updateTask.clientRequest = task.clientRequest;
    for (int serverNumber : getServerWithRole("DatabaseServer")) {
      ServerInterface server = getServer("DatabaseServer", serverNumber);
      if (server != null) {
        try {
          server.addTask(updateTask);
        } catch (RemoteException e) {
          ServerLogger.databaseServerLogging(
              "Fails to add update task. Exception: " + e.getMessage(), serverNumber);
        }
      }
    }

    // announce updated task to client
    ClientInterface client = getClient(task.clientNumber);
    if (client != null) {
      client.receiveAnnounce(task);
    }
  }
}
