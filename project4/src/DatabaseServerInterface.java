import java.rmi.Remote;
import java.rmi.RemoteException;

/** The Database server interface. */
public interface DatabaseServerInterface extends Remote {
  /**
   * process get request and return to client without 2PC process
   *
   * @param key the key
   * @return the string
   * @throws RemoteException the remote exception
   */
  String getQuery(String key) throws RemoteException;
}
