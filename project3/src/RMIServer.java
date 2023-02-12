import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/** The remote concurrent hashmap interface. */
public interface RMIServer extends Remote {
  /**
   * Execute client request.
   *
   * @param request the request
   * @return the response from server
   * @throws IOException the io exception
   */
  // add ID to execute
  String execute(String request) throws IOException;

  // helper method to set current server port and other servers' ports
  void setServerPorts(int[] otherPorts, int curPort) throws RemoteException;

  void ack(UUID transactionID, int serverNum, String ack) throws RemoteException;

  void go(UUID transactionID, int serverNum, String ack) throws RemoteException;

  void prepareRequest(UUID transactionID, String request, int serverNum) throws RemoteException;
}
