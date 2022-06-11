import java.io.Console;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;

/** The Client to send request. */
public class Client {
  /**
   * The entry point of Client application.
   *
   * @param args the input arguments, server host and registry port
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Example: java Client <server host> <registry port>");
      System.exit(1);
    }

    // get server host and registry port from client input
    String hostName = args[0];
    int registryPort = Integer.parseInt(args[1]);
    new Client(hostName, registryPort);
  }

  /**
   * Instantiates Client.
   *
   * @param host the host
   * @param registryPort the registry port
   * @throws IOException the io exception
   */
  public Client(String host, int registryPort) throws IOException {
    try {
      // create console to interact
      Console console = System.console();

      // get the rmi registry with given hostname and port number
      Registry registry = LocateRegistry.getRegistry(host, registryPort);

      // get the server hosting concurrent hashmap service by look up from RMI registry
      String serverName = "RPCServer";
      RMIServer server = (RMIServer) registry.lookup(serverName);

      // initialize request, response and operation variables
      String request;
      String operation;
      String message;
      // get client ip and host for logging
      InetAddress ip;
      String localhost;
      String localIP;
      ip = InetAddress.getLocalHost();
      localIP = ip.getHostAddress();
      localhost = ip.getHostName();

      do {
        // read user input
        request = console.readLine("Enter text: ");
        // request after formatting
        message = request.trim().toLowerCase();

        // if else check to get the type of operation
        if (message.contains("put")) {
          operation = "PUT";
        } else if (message.contains("get")) {
          operation = "GET";
        } else if (message.contains("delete")) {
          operation = "DELETE";
        } else {
          operation = "No valid operation";
        }

        // call remote method execute on server to perform request
        String response = server.execute(request);

        // write request into client log
        ClientLogger.clientLoggingRequest(request, operation, localIP, localhost);

        if (response == null) {
          response = "Invalid response";

          // write server response into client log
          ClientLogger.clientLoggingResponse(response, operation, host);

          // display server response at client side
          System.out.println(response);
          break;
        }
        // write server response into client log
        ClientLogger.clientLoggingResponse(response, operation, host);

        // display server response at client side
        System.out.println(response);

      } while (!request.equals("quit"));
      // when user input quit, close up
      System.out.println("Closing connection");
      ClientLogger.clientLoggingExit();

      // exception handle and log
    } catch (RemoteException remoteException) {
      System.out.println("RemoteException: " + remoteException.getMessage());
      ClientLogger.clientLoggingException(remoteException.toString());
    } catch (NotBoundException | IOException exception) {
      System.out.println(exception + exception.getMessage());
      ClientLogger.clientLoggingException(exception.toString());
    }
  }
}
