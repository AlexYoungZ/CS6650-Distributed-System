import java.io.Serializable;

/**
 * The Client request allowing serialized to transmit object.
 */
public class ClientRequest implements Serializable {

	/**
	 * The request operation.
	 */
	String operation;
	/**
	 * The request key.
	 */
	String key;
	/**
	 * The request value.
	 */
	String value;

	@Override
	public String toString() {
		return operation + "(" + key + "," + value + ")";
	}
}
