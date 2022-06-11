import java.io.IOException;
import java.rmi.Remote;

/** The remote concurrent hashmap interface. */
public interface RMIServer extends Remote {
  /**
   * Execute client request.
   *
   * @param request the request
   * @return the response from server
   * @throws IOException the io exception
   */
  String execute(String request) throws IOException;
}
