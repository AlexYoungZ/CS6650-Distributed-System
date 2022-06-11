import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/** The Sorter implementation of the Sorter remote interface. */
public class SorterImpl extends UnicastRemoteObject implements Sorter {

  /**
   * Constructor to instantiate a Sorter object.
   *
   * @throws RemoteException the remote exception
   */
  public SorterImpl() throws RemoteException {
    super();
  }

  // the remote method to sort given integer array
  public Integer[] sortArray(Integer[] array) {
    Arrays.sort(array);
    return array;
  }
}
