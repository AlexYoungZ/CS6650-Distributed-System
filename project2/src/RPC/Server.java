
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends UnicastRemoteObject implements ConcurrentHashmapServer {
  static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

  public Server() throws RemoteException {}

  @Override
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

    // return response
    return response;
  }

  private String getClientAddress() {
    try {
      return getClientHost();
    } catch (ServerNotActiveException serverNotActiveException) {
      System.err.println(serverNotActiveException.getMessage());
      return "No valid client host";
    }
  }

  public static void main(String[] args) {

    // basic args check
    if (args.length != 1) {
      System.err.println("Example: java Server <registry port>");
      System.exit(1);
    }

    int registryPort = Integer.parseInt(args[0]);
    System.setProperty("java.security.policy", "security.policy");

    try {

      System.setSecurityManager(new SecurityManager());
      Server server = new Server();

      Registry registry = LocateRegistry.getRegistry(registryPort);

      String serverName = "RPCServer";

      registry.rebind(serverName, server);
      System.out.println(serverName + " bound");

    } catch (RemoteException remoteException) {
      System.err.println("RemoteException: "+remoteException.getMessage());
      // todo: server log
    }
  }
}
