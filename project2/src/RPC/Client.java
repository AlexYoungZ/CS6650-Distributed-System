import java.io.Console;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Client {
  public static void main(String[] args) throws RemoteException {
    if (args.length != 2) {
      System.err.println("Example: java Client <server host> <registry port>");
      System.exit(1);
    }
    System.setSecurityManager(new SecurityManager());
    String hostName = args[0];
    int registryPort = Integer.parseInt(args[1]);
    new Client(hostName, registryPort);
  }

  // client make operations, read cmd line input string,
  // operation=server.preprocess(input) check if match get, put, delete first
  // then server.<operation>
  public Client(String host, int registryPort) throws RemoteException {
    try {
      // create console to interact
      Console console = System.console();

      // get the rmi registry with given hostname and port number
      Registry registry = LocateRegistry.getRegistry(host, registryPort);

      // get the server hosting concurrent hashmap service by look up from RMI registry
      String serverName = "RPCServer";
      Server server = (Server) registry.lookup(serverName);

      server.execute("Operation");
      //			System.out.println(server.execute(new MyCalculation(2)));

      // initialize request, response and operation variables
      String request;
      //			String response;
      String operation;
      String message; // request after formatting
	    // get client ip and host for logging
      InetAddress ip;
      String localhost;
      String localIP;
      ip = InetAddress.getLocalHost();
      localIP = ip.getHostAddress();
      localhost = ip.getHostName();
      System.out.println("Your current IP address : " + localIP);
      System.out.println("Your current Hostname : " + localhost);
      do {
        // read user input, write it into output stream then flush
        request = console.readLine("Enter text: ");

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
        // todo: receive response
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

    } catch (RemoteException remoteException) {
      // I/O Error or bad URL
      System.out.println("Client: remote call failed");
      System.out.println(remoteException.getMessage());
    } catch (NotBoundException | UnknownHostException exception) {
      // RPCServer isn't registered
      System.out.println(exception.getMessage());
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } // todo: log exception
  }
}
