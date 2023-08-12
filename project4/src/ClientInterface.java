import java.rmi.Remote;
import java.rmi.RemoteException;

/** The interface Client interface. */
public interface ClientInterface extends Remote {
  /**
   * Receive announce from learners.
   *
   * @param task the task
   * @throws RemoteException the remote exception
   */
  void receiveAnnounce(Task task) throws RemoteException;
}
