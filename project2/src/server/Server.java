import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

/** The Server to provide concurrent hashmap service. */
public class Server extends UnicastRemoteObject implements RMIServer {
  // use concurrent hashmap to provide thread safe and mutual exclusion
  static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

  /**
   * Instantiates the Server.
   *
   * @throws RemoteException the remote exception
   */
  public Server() throws RemoteException {
    super();
  }

  public String execute(String request) throws IOException {
    // display request received at server side:
    System.out.println("Server received request: " + request);

    // get client address for server logging
    String clientAddress = getClientAddress();

    // get Operation type after formatting
    String operation = RequestHandler.getOperationType(request);

    // call request handler to handle client request and return response
    String response = RequestHandler.handleRequest(request, map, clientAddress);

    // display response at server side
    System.out.println("Server response: " + response);

    //  write to server log file use Server logger
    ServerLogger.serverLogging(request, response, operation, clientAddress);

    return response;
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

  /**
   * The entry point of Server application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {

    // basic args check
    if (args.length != 1) {
      System.err.println("Example: java Server <registry port>");
      System.exit(1);
    }

    // get registry port number
    int registryPort = Integer.parseInt(args[0]);

    try {
      Server server = new Server();

      // get the rmi registry with given port number
      Registry registry = LocateRegistry.getRegistry(registryPort);

      String serverName = "RPCServer";
      // bind the concurrent hashmap server object to RPCServer
      registry.rebind(serverName, server);
      System.out.println(serverName + " ready");

      // exception handling and server logging
    } catch (RemoteException remoteException) {
      System.err.println("RemoteException: " + remoteException.getMessage());
      ServerLogger.serverExceptionLogging(remoteException.toString());
    }
  }
}
