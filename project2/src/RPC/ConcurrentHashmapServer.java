import java.io.IOException;
import java.rmi.Remote;

public interface ConcurrentHashmapServer extends Remote {
  // execute operation and return acknowledge information
  String execute(String request) throws IOException;
}
