
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

/** The Sorter. */
public class SorterImpl extends UnicastRemoteObject implements Sorter {

  /**
   * Instantiates a new Sorter.
   *
   * @throws RemoteException the remote exception
   */
  public SorterImpl() throws RemoteException {
    super();
  }

  public Integer[] sortArray(Integer[] array) throws RemoteException {
    Arrays.sort(array);
    return array;
  }
}
