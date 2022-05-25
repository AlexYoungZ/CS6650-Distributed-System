import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The type Client logger. */
public class ClientLogger {

  /**
   * Client logging request.
   *
   * @param request the request
   * @param operation the operation
   * @param port the port
   * @param clientAddress the client address
   * @throws IOException the io exception
   */
  public static void clientLoggingRequest(
      String request, String operation, Integer port, String clientAddress) throws IOException {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log =
        "Client send: "
            + request
            + " from "
            + clientAddress
            + " at "
            + port
            + " to perform "
            + operation
            + " operation at "
            + time
            + " client time";

    FileWriter fw = new FileWriter("clientTCPLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }

  /**
   * Client logging response.
   *
   * @param response the response
   * @param operation the operation
   * @param serverAddress the server address
   * @throws IOException the io exception
   */
  public static void clientLoggingResponse(String response, String operation, String serverAddress)
      throws IOException {

    String log;
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log =
        "Client receive: "
            + response
            + " from "
            + serverAddress
            + " with "
            + operation
            + " operation at "
            + time
            + " client time";

    FileWriter fw = new FileWriter("clientTCPLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }

  /**
   * Client logging exception.
   *
   * @param exceptionMessage the exception message
   * @throws IOException the io exception
   */
  public static void clientLoggingException(String exceptionMessage) throws IOException {

    String log;
    String exception;
    String netExceptionPrefix = "java.net.";
    String ioExceptionPrefix = "java.io.";
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    // only take succinct error message to client log
    if (exceptionMessage.contains(netExceptionPrefix)) {
      exception = exceptionMessage.substring(netExceptionPrefix.length());
    } else if (exceptionMessage.contains(ioExceptionPrefix)) {
      exception = exceptionMessage.substring(ioExceptionPrefix.length());
    } else {
      exception = exceptionMessage;
    }

    log = "Client get: " + exception + " at " + time + " client time";

    FileWriter fw = new FileWriter("clientTCPLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }
}
