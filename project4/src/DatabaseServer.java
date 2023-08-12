import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.concurrent.ConcurrentHashMap;

/** The Database server. */
public class DatabaseServer extends Server implements DatabaseServerInterface {
  /** The concurrent hashmap to provide thread safe and mutual exclusion. */
  protected final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

  /**
   * Instantiates a new Database server.
   *
   * @param address the address
   * @throws RemoteException the remote exception
   */
  public DatabaseServer(String address) throws RemoteException {
    super(address);
  }

  @Override
  protected synchronized void process(Task task) {
    if (task.Type == TaskType.Write) {
      if (task.clientRequest.operation.equalsIgnoreCase("put")) {
        map.put(task.clientRequest.key, task.clientRequest.value);
      } else if (task.clientRequest.operation.equalsIgnoreCase("delete")) {
        map.remove(task.clientRequest.key);
      }
    } else {
      System.out.println("Fail to update task: " + task + "in database server");
      ServerLogger.databaseServerLogging(task.toString(), serverNum);
    }
  }

  public String getQuery(String key) {
    synchronized (map) {
      try {
        System.out.println("Database server receives get(" + key + ") from " + getClientHost());
        ServerLogger.databaseServerLogging(
            " receives get(" + key + ") from " + getClientHost(), serverNum);
      } catch (ServerNotActiveException exception) {
        System.out.println("Database server fails to perform get query. " + exception.getMessage());
        ServerLogger.serverExceptionLogging(
            "Database server fails to perform get query. " + exception.getMessage(), serverNum);
      }
      String result = map.get(key);
      return result;
    }
  }
}
