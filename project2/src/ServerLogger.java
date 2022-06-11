import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The Server logger. */
public class ServerLogger {

  /**
   * Server logging.
   *
   * @param request the request
   * @param response the response
   * @param operation the operation
   * @param clientAddress the client address
   * @throws IOException the io exception
   */
  public static void serverLogging(
      String request, String response, String operation, String clientAddress)
      throws IOException {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log =
        "Server receive: "
            + request
            + " from "
            + clientAddress
            + " and perform "
            + operation
            + " operation at "
            + time
            + " server time with server response: "
            + response;

    FileWriter fw = new FileWriter("serverLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }

  /**
   * Server exception logging.
   *
   * @param exceptionMessage the exception message
   * @throws IOException the io exception
   */
  public static void serverExceptionLogging(String exceptionMessage) throws IOException {

    String log;

    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Server get: " + exceptionMessage + " at " + time + " server time";

    FileWriter fw = new FileWriter("serverLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }
}
