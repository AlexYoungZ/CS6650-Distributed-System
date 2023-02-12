import java.rmi.Naming;

/** The Sorter server. */
public class SorterServer {

  /**
   * The entry point of Sorter Server.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      // create a sorter object
      Sorter sorter = new SorterImpl();
      // bind the sorter by name of SorterService to localhost:3000
      Naming.rebind("rmi://localhost:3000/SorterService", sorter);
      System.out.println("Start RMI sorter server");
    } catch (Exception exception) {
      System.out.println("Exception: " + exception.getMessage());
    }
  }
}
