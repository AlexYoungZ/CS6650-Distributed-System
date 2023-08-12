import java.io.Console;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/** The Client sending request to proposer servers and read from database server. */
public class Client extends UnicastRemoteObject implements Serializable, ClientInterface {
  private ArrayList<Integer> databaseServers;
  private ArrayList<Integer> proposers;
  private static String address = "localhost";
  private static int clientNum = 0;
  /** The Client request. */
  ClientRequest clientRequest = new ClientRequest();

  /**
   * Instantiates Client.
   *
   * @param address the host address
   * @throws RemoteException the remote exception
   */
  public Client(String address) throws RemoteException {

    // create console to interact
    Console console = System.console();

    Runtime.getRuntime().addShutdownHook(new Thread(this::clientLogoff));
    clientLogin();
    databaseServers = getServerWithRole("DatabaseServer");
    proposers = getServerWithRole("Proposer");

    // initialize request, response and operation variables
    String request = null;
    String message;
    String pair;
    String key;
    String value;

    do {
      try {
        // read user input
        request = console.readLine("Enter text: ");
        // request after formatting
        message = request.trim().toLowerCase();

        if (message.contains("put")) {
          pair = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")")).trim();

          key = pair.split(",")[0].trim();
          value = pair.split(",")[1].trim();

          createPutTask(key, value);
        } else if (message.contains("get")) {

          key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
          System.out.println("key: " + key);

          get(key);
        } else if (message.contains("delete")) {

          key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
          System.out.println("key: " + key);
          createDeleteTask(key);
        } else {
          System.out.println("Invalid request, please try again.");
        }

        // write request into client log
        ClientLogger.clientLoggingRequest(request, clientNum);

        // give some time for Paxos to run
        Thread.sleep(300);

        // exception handle and log
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        System.out.println(
            "ArrayIndexOutOfBoundsException: " + arrayIndexOutOfBoundsException.getMessage());
        ClientLogger.clientLoggingException(arrayIndexOutOfBoundsException.toString(), clientNum);
      } catch (StringIndexOutOfBoundsException | InterruptedException exception) {
        System.out.println("StringIndexOutOfBoundsException: " + exception.getMessage());
        ClientLogger.clientLoggingException(exception.toString(), clientNum);
      }
    } while (!Objects.equals(request, "quit"));
    // when user input quit, close up
    System.out.println("Closing connection");
    ClientLogger.clientLoggingExit(clientNum);
  }

  /**
   * The entry point of Client application.
   *
   * @param args the input arguments, server name, server host and registry port
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Example: java Client <host address>");
      System.exit(1);
    }
    // get server host and registry port from client input
    address = args[0];
    try {
      new Client(address);
    } catch (RemoteException exception) {
      ClientLogger.clientLoggingException(
          "Fails to create new client. Exception: " + exception.getMessage(), clientNum);
      System.out.println("Fails to create new client");
    }
  }

  /**
   * Get server with given role and ID.
   *
   * @param role the role
   * @param id the id
   * @return the server
   */
  ServerInterface getServer(String role, int id) {
    try {
      Registry registry = LocateRegistry.getRegistry(address);
      return (ServerInterface) registry.lookup(role + id);
    } catch (RemoteException | NotBoundException exception) {
      // System.out.println("Fails to get server with given role and id");
      return null;
    }
  }

  /**
   * Get server number array with role.
   *
   * @param role the role
   * @return the server with role
   */
  ArrayList<Integer> getServerWithRole(String role) {
    ArrayList<Integer> servers = new ArrayList<>();
    Registry registry;
    try {
      registry = LocateRegistry.getRegistry(address);
    } catch (RemoteException remoteException) {
      // System.out.println("Fails to get server with given role");
      // return empty array if no matching
      return new ArrayList<>();
    }
    // add matched server number to array and return
    for (int i = 1; i <= 5; i++) {
      try {
        registry.lookup(role + i);
        servers.add(i);
      } catch (RemoteException | NotBoundException exception) {
        // System.out.println("Fails to get server with given role");
      }
    }
    return servers;
  }

  // get first alive proposer
  private ServerInterface getProposer() {
    for (int id : proposers) {
      ServerInterface proposer = getServer("Proposer", id);
      if (proposer != null) {
        return proposer;
      }
    }
    return null;
  }

  private void clientLogin() {
    try {
      Registry registry = LocateRegistry.getRegistry(address);
      for (int i = 1; i <= 5; i++) {
        try {
          // try the next number if current already used
          registry.lookup("Client" + i);
        } catch (NotBoundException e) {
          clientNum = i;
          registry.rebind("Client" + clientNum, this);
          System.out.println("Start client" + clientNum);
          return;
        }
      }
    } catch (RemoteException exception) {
      ClientLogger.clientLoggingException(exception.getMessage(), clientNum);
      System.out.println(
          "Fails to start client" + clientNum + ". Exception: " + exception.getMessage());
    }
  }

  private void clientLogoff() {
    try {
      Registry registry = LocateRegistry.getRegistry(address);
      registry.unbind("Client" + clientNum);
    } catch (Exception exception) {
      ClientLogger.clientLoggingException(exception.getMessage(), clientNum);
      System.out.println(
          "Unable to unbind client" + clientNum + ". Exception:" + exception.getMessage());
    }
  }

  // check if majority alive database server agree on a value
  private void get(String key) {
    // key: value of client request,
    // value: the number of database server holding same value
    HashMap<String, Integer> valueMap = new HashMap<>();
    // get database server
    for (int databaseServerNumber : databaseServers) {
      DatabaseServerInterface databaseServer =
          (DatabaseServerInterface) getServer("DatabaseServer", databaseServerNumber);
      if (databaseServer != null) {
        String value;
        // check if value exist
        try {
          value = databaseServer.getQuery(key);
        } catch (Exception e) {
          // System.out.println("Client fails to get value with key: "+key +" from database
          // server.");
          continue;
        }
        // the number of database server agree on the same value
        int num = 0;
        if (valueMap.containsKey(value)) {
          num = valueMap.get(value);
        }
        // if the majority of database server agree on the value
        if (++num > databaseServers.size() / 2) {
          System.out.println(
              "Client receive database server response"
                  + "("
                  + key
                  + ","
                  + value
                  + ") of "
                  + "get "
                  + "request"
                  + ".");
          ClientLogger.clientLoggingResponse(
              "Client receive database server response"
                  + "("
                  + key
                  + ","
                  + value
                  + ") of "
                  + "get "
                  + "request"
                  + ".",
              clientNum);
          return;
        }
        valueMap.put(value, num);
      }
    }
    ClientLogger.clientLoggingException("Fails to get value with key: " + key, clientNum);
  }

  // create delete task and add to proposer's task queue
  private void createDeleteTask(String key) {
    // get proposer
    ServerInterface proposer = getProposer();
    if (proposer == null) {
      ClientLogger.clientLoggingException("No working proposer.", clientNum);
      return;
    }
    // create delete task
    Task request = new Task();
    request.Type = TaskType.Request;
    request.clientNumber = clientNum;
    clientRequest.operation = "Delete";
    clientRequest.key = key;
    clientRequest.value = "";
    request.clientRequest = clientRequest;
    System.out.println("Create delete task: " + clientRequest);
    // add onto proposer's task queue
    try {
      proposer.addTask(request);
    } catch (Exception e) {
      ClientLogger.clientLoggingException("Failed to assign delete task to proposer", clientNum);
    }
  }

  // create put task and add to proposer's task queue
  private void createPutTask(String key, String value) {
    // get proposer
    ServerInterface proposer = getProposer();
    if (proposer == null) {
      ClientLogger.clientLoggingException("No working proposer.", clientNum);
      return;
    }
    // create put task
    Task request = new Task();
    request.Type = TaskType.Request;
    request.clientNumber = clientNum;
    clientRequest.operation = "put";
    clientRequest.key = key;
    clientRequest.value = value;
    request.clientRequest = clientRequest;
    // add onto proposer's task queue
    System.out.println("Create put task: " + clientRequest);
    try {
      proposer.addTask(request);
    } catch (Exception e) {
      ClientLogger.clientLoggingException("Failed to assign put task to proposer", clientNum);
    }
  }

  // receive announce response from learners, write log
  public void receiveAnnounce(Task task) {
    if (task.clientRequest.operation.equalsIgnoreCase("put")) {
      ClientLogger.clientLoggingResponse(task.clientRequest.toString(), clientNum);
    } else if (task.clientRequest.operation.equalsIgnoreCase("delete")) {
      ClientLogger.clientLoggingResponse(task.clientRequest.toString(), clientNum);
    }
  }
}
