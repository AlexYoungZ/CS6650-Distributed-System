import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The Server logger for all roles. */
public class ServerLogger {

  /**
   * Server client request logging.
   *
   * @param operation the operation
   * @param serverName the name of server
   */
  public static synchronized void databaseServerLogging(String operation, int serverName) {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Database Server" + serverName + operation + " at " + time;

    try {
      FileWriter fw = new FileWriter("../logs/Server" + serverName + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException ioException) {
      System.out.println(ioException.getMessage());
    }
  }

  /**
   * Server exception logging.
   *
   * @param exceptionMessage the exception message
   * @param serverName the number of server
   */
  public static synchronized void serverExceptionLogging(String exceptionMessage, int serverName) {

    String log;

    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Server" + serverName + exceptionMessage + " at " + time;

    try {
      FileWriter fw = new FileWriter("../logs/Server" + serverName + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException ioException) {
      System.out.println(ioException.getMessage());
    }
  }

  /**
   * Server operation logging.
   *
   * @param operationMessage the exception message
   * @param serverName the number of server
   */
  public static synchronized void serverOperationLogging(String operationMessage, int serverName) {

    String log;

    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Server" + serverName + operationMessage + " at " + time;

    try {
      FileWriter fw = new FileWriter("../logs/Server" + serverName + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException ioException) {
      System.out.println(ioException.getMessage());
    }
  }
}
