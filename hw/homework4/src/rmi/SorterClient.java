
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Arrays;

/** The Sorter client. */
public class SorterClient {
  /**
   * The entry point of Sorter client.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    try {
      Sorter sorter = (Sorter) Naming.lookup("rmi://localhost/SorterService");
      Integer[] intArray = {1, 5, 4, 3, 12, 22, 1};
      System.out.println(Arrays.toString(sorter.sortArray(intArray)));

    } catch (MalformedURLException | RemoteException | NotBoundException exception) {
      System.out.println("Exception: " + exception.getMessage());
    }
  }
}
