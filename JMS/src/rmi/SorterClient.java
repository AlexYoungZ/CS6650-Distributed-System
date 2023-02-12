
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Arrays;

/**
 * The Sorter client.
 */
public class SorterClient {

	/**
	 * The entry point of Sorter client.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		try {
			// get the remote sorter object by lookup in remove object registry with name of
			// SorterService at localhost:3000
			Sorter sorter = (Sorter) Naming.lookup("rmi://localhost:3000/SorterService");

			Integer[] intArray = {1, 5, 4, 3, 12, 22, 1, 2, 6, 222};
			System.out.println("Input array: " + Arrays.toString(intArray));


			// print the sorted int array at client console
			System.out.println("Sorted Array: ");
			System.out.println(Arrays.toString(sorter.sortArray(intArray)));

		} catch (MalformedURLException | RemoteException | NotBoundException exception) {
			System.out.println("Exception: " + exception.getMessage());
		}
	}
}
