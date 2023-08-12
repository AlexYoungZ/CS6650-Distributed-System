import java.rmi.Remote;
import java.rmi.RemoteException;

/** The Server interface. */
public interface ServerInterface extends Remote {
  /**
   * Add task to server's task queue to process by different roles.
   *
   * @param task the task
   * @throws RemoteException the remote exception
   */
  void addTask(Task task) throws RemoteException;
}
