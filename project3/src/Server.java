import static java.rmi.server.RemoteServer.getClientHost;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** The Server to provide concurrent hashmap service. */
public class Server extends Thread implements RMIServer {
  // use concurrent hashmap to provide thread safe and mutual exclusion
  static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
  private int port;
  private int[] otherPorts = new int[4];

  static class ClientRequest {
    String operation;
    String key;
    String value;
  }

  /**
   * Instantiates the Server.
   *
   * @throws RemoteException the remote exception
   */
  public Server() throws RemoteException {
    super();
  }

  public String execute(String request) throws IOException {
    ClientRequest clientRequest = new ClientRequest();

    // display request received at server side:
    System.out.println("Server received request: " + request);

    // get client address for server logging
    String clientAddress = getClientAddress();

    // get Operation type after formatting
    String operation = RequestHandler.getOperationType(request);

    // call request handler to handle client request and return response
    String response = RequestHandler.handleRequest(request, map, clientAddress);

    // set operation, key and value to client request class for easy retrieval
    clientRequest.operation = operation;
    clientRequest.key = RequestHandler.key;
    clientRequest.value = RequestHandler.value;

    // display response at server side
    System.out.println("Server response: " + response);

    //  write to server log file use Server logger
    ServerLogger.serverLogging(request, response, operation, clientAddress);

    return response;
  }

  @Override
  public void setServerPorts(int[] otherPorts, int curPort) throws RemoteException {
    this.otherPorts = otherPorts;
    this.port = curPort;
  }

  // helper method to retrieve client address for logging
  private String getClientAddress() {
    try {
      return getClientHost();
    } catch (ServerNotActiveException serverNotActiveException) {
      System.err.println("ServerNotActiveException: " + serverNotActiveException.getMessage());
      return "No valid client host";
    }
  }

  @Override
  public void ack(UUID transactionID, int serverNum, String ack) throws RemoteException {}

  @Override
  public void go(UUID transactionID, int serverNum, String ack) throws RemoteException {}

  @Override
  public void prepareRequest(UUID transactionID, String request, int serverNum)
      throws RemoteException {}
}
