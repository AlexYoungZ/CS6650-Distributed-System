
import java.rmi.Remote;
import java.rmi.RemoteException;

/** The remote Sorter interface. */
public interface Sorter extends Remote {
  /**
   * Sort array of integers.
   *
   * @param array the array of integers
   * @return the integer array
   * @throws RemoteException the remote exception
   */
  Integer[] sortArray(Integer[] array) throws RemoteException;
}
