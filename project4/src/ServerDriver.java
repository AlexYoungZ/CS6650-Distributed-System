import java.util.ArrayList;

/** The Server driver. */
public class ServerDriver {
  // array of servers with different roles
  private static ArrayList<Server> servers = new ArrayList<>();

  /**
   * The entry point of server.
   *
   * @param argv the input arguments
   */
  public static void main(String[] argv) {
    if (argv.length < 1) {
      System.err.println("Example: java ServerDriver localhost <role>\n");
      System.exit(1);
    }

    boolean foundRole = false;
    String address = "localhost";
    for (String arg : argv) {
      if (arg.equalsIgnoreCase("databaseServer")
          || arg.equalsIgnoreCase("proposer")
          || arg.equalsIgnoreCase("learner")
          || arg.equalsIgnoreCase("acceptor")) {
        foundRole = true;
      } else {
        address = arg;
      }
    }
    if (!foundRole) {
      System.out.println("Didn't find valid server role to initiate.");
      return;
    }

    // add different roles to server list
    for (String arg : argv) {
      try {
        if (arg.equalsIgnoreCase("databaseServer")) {
          servers.add(new DatabaseServer(address));
        } else if (arg.equalsIgnoreCase("proposer")) {
          servers.add(new Proposer(address));
        } else if (arg.equalsIgnoreCase("learner")) {
          servers.add(new Learner(address));
        } else if (arg.equalsIgnoreCase("acceptor")) {
          servers.add(new Acceptor(address));
        }
      } catch (Exception e) {
        System.out.println("Failed to create " + arg + ". Exception: " + e.getMessage());
        System.exit(0);
      }
    }
  }
}
