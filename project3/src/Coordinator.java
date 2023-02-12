import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class Coordinator extends Thread {
  static Server[] servers = new Server[5];

  public static void main(String[] args) throws Exception {
    // basic args check
    if (args.length != 5) {
      System.err.println("Input five server ports, separate with empty space.");
      System.exit(1);
    }

    // convert string array to int array
    int[] serverPorts = Arrays.stream(args).mapToInt(Integer::parseInt).toArray();

    for (int i = 0; i < serverPorts.length; i++) {
      try {
        servers[i] = new Server();
        RMIServer server = (RMIServer) UnicastRemoteObject.exportObject(servers[i], 0);
        // get the rmi registry with given port number
        Registry registry = LocateRegistry.createRegistry(serverPorts[i]);
        String serverName = "RPCServer";
        // bind the concurrent hashmap server object to RPCServer
        registry.bind(serverName, server);
        getServerPorts(serverPorts, serverPorts[i]);

        System.out.printf("Server %s is running at port %s", i, servers[i]);

        // exception handling and server logging
      } catch (RemoteException remoteException) {
        System.err.println("RemoteException: " + remoteException.getMessage());
        ServerLogger.serverExceptionLogging(remoteException.toString());
      }
      Thread serverThread = new Thread();
      serverThread.start();
    }
  }

  // helper method to get current server port and other servers' ports
  private static void getServerPorts(int[] serverPorts, int curPort) throws IOException {
    try {
      Registry registry = LocateRegistry.getRegistry(curPort);
      RMIServer curServer = (RMIServer) registry.lookup("RPCServer");

      int[] otherPorts = new int[serverPorts.length - 1];
      int j = 0;

      for (int serverPort : serverPorts) {
        if (serverPort != curPort) {
          otherPorts[j] = serverPort;
          j++;
        }
      }
      curServer.setServerPorts(otherPorts, curPort);

    } catch (NotBoundException | RemoteException exception) {
      System.err.println("Fails to get server ports: " + exception.getMessage());
      ServerLogger.serverExceptionLogging(exception.toString());
    }
  }
}
