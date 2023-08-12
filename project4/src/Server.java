import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/** The abstract Server. */
public abstract class Server extends UnicastRemoteObject implements ServerInterface {
  private final String address;
  // server ID
  protected int serverNum = 0;
  // store tasks to be processed by different roles
  private final ConcurrentLinkedQueue<Task> taskQueue = new ConcurrentLinkedQueue<>();
  // create down and recovery time
  private final Timer timer = new Timer();
  private boolean exit = false;
  // create thread with process tasks and wait while queue is empty or temporary down
  private final Thread worker =
      new Thread() {
        public void run() {
          while (!exit) {
            while (!taskQueue.isEmpty() && !exit) {
              try {
                process(taskQueue.poll());
              } catch (Exception e) {
                System.out.println(
                    "Fails to process task with exception:" + e + "," + e.getMessage());
                ServerLogger.serverExceptionLogging(
                    "Fails to process task with exception:" + e + "," + e.getMessage(), serverNum);
              }
            }
            try {
              wait();
            } catch (Exception e) {
              // most time will just wait therefore leave here empty
              // System.out.println("Thread fails when waiting for tasks " + e.getMessage());
            }
          }
        }
      };

  /**
   * Instantiates a new Server.
   *
   * @param address the address
   * @throws RemoteException the remote exception
   */
  public Server(String address) throws RemoteException {
    this.address = address;
    // start worker thread with shut down hook
    worker.start();
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    synchronized (worker) {
                      // notify other thread and stop worker thread
                      exit = true;
                      worker.notify();
                      worker.join();
                    }
                    logoff();
                  } catch (Exception e) {
                    System.out.println(
                        "Fails to stop worker thread on exit. Exception: " + e.getMessage());
                    ServerLogger.serverExceptionLogging(
                        "Fails to stop worker thread on exit. " + "Exception: " + e.getMessage(),
                        serverNum);
                  }
                }));
    try {
      login();
    } catch (Exception e) {
      System.out.println("Server" + serverNum + " fails to login. " + e.getMessage());
      ServerLogger.serverExceptionLogging(
          "Server" + serverNum + " fails to login. " + e.getMessage(), serverNum);
    }
    // add random fail case
    timer.schedule(new FailStop(), (long) ((Math.random() * 60 + 60) * 1000));
  }

  // set server recovery after failure and fails after some time
  private class TurnUp extends TimerTask {
    @Override
    public void run() {
      if (exit) return;
      try {
        login();
      } catch (Exception exception) {
        System.out.println(
            "Server" + serverNum + " fails to login when recovery. " + exception.getMessage());
        ServerLogger.serverExceptionLogging(
            "Server" + serverNum + " fails to login when recovery. " + exception.getMessage(),
            serverNum);
      }
      int runTime = (int) (Math.random() * 60 + 60);
      System.out.println("Server" + serverNum + " expected to fail in " + runTime + "s.");
      ServerLogger.serverOperationLogging(" expected to fail in " + runTime + "s.", serverNum);
      // add next fails round
      timer.schedule(new FailStop(), runTime * 1000L);
    }
  }

  // set server random fails and recovery with timer task
  private class FailStop extends TimerTask {
    @Override
    public void run() {
      if (exit) return;
      int shutdownTime = (int) (Math.random() * 10.0);
      System.out.println(
          "Server" + serverNum + " fails while expected to recovery in " + shutdownTime + "s.");
      ServerLogger.serverOperationLogging(
          " fails while expected to recovery in " + shutdownTime + "s.", serverNum);

      logoff();
      timer.schedule(new TurnUp(), shutdownTime * 1000L);
    }
  }

  /**
   * Process task.
   *
   * @param task the task
   * @throws Exception the exception
   */
  abstract void process(Task task) throws Exception;

  public void addTask(Task task) {
    System.out.println("Add task: " + task.toString() + " to Server" + serverNum);
    ServerLogger.serverOperationLogging(" add task: " + task, serverNum);
    taskQueue.add(task);
    synchronized (worker) {
      worker.notify();
    }
  }

  // get server's role
  private String getServerRole() {
    return getClass().getName();
  }

  // get server name
  private String getServerName() {
    return getServerRole() + serverNum;
  }

  /**
   * Gets server.
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
      System.out.println("Fails to get server with role: " + role + " and id: " + id);
      return null;
    }
  }

  /**
   * Gets client.
   *
   * @param clientNumber the client number
   * @return the client
   */
  protected ClientInterface getClient(int clientNumber) {
    try {
      Registry registry = LocateRegistry.getRegistry(address);
      return (ClientInterface) registry.lookup("Client" + clientNumber);
    } catch (RemoteException | NotBoundException exception) {
      System.out.println(
          "Fails to get client" + clientNumber + ". Exception: " + exception.getMessage());
      return null;
    }
  }

  /**
   * Get servers name array with role.
   *
   * @param role the role
   * @return the servers name array with role
   */
  ArrayList<Integer> getServerWithRole(String role) {
    ArrayList<Integer> serverNumArray = new ArrayList<>();
    Registry registry;
    try {
      registry = LocateRegistry.getRegistry(address);
    } catch (RemoteException remoteException) {
      //      System.out.println("Fails to get server with given role");
      // return empty array if fails
      return new ArrayList<>();
    }
    for (int i = 1; i <= 5; i++) {
      try {
        // get all roles and add server number to return
        registry.lookup(role + i);
        serverNumArray.add(i);
      } catch (RemoteException | NotBoundException exception) {
        //        System.out.println("Fails to get server with given role");
      }
    }
    return serverNumArray;
  }

  // server login
  private void login() throws Exception {
    Registry registry = LocateRegistry.getRegistry(address);
    for (int i = 1; i <= 5; i++) {
      try {
        // if already registered, skip
        registry.lookup(getServerRole() + i);
      } catch (NotBoundException | RemoteException e) {
        // if not register, bind to registry with name
        serverNum = i;
        registry.rebind(getServerName(), this);
        System.out.println("Start " + getServerName());
        return;
      }
    }
    throw new Exception("Fails to register server" + getServerRole());
  }

  // server logoff
  private void logoff() {
    try {
      Registry registry = LocateRegistry.getRegistry(address);
      registry.unbind(getServerName());
    } catch (RemoteException | NotBoundException exception) {
      System.out.println(
          "Unable to unbind " + getServerName() + ". Exception:" + exception.getMessage());
    }
  }
}
