import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/** The Client logger. */
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
      String request, String operation, String port, String clientAddress) throws IOException {

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

    FileWriter fw = new FileWriter("clientLog.txt", true);
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
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log =
        "Client receive server response: "
            + response
            + " from "
            + serverAddress
            + " with "
            + operation
            + " operation at "
            + time
            + " client time";

    FileWriter fw = new FileWriter("clientLog.txt", true);
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
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client get: " + exceptionMessage + " at " + time + " client time";

    FileWriter fw = new FileWriter("clientLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }

  /**
   * Client logging exit.
   *
   * @throws IOException the io exception
   */
  public static void clientLoggingExit() throws IOException {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client quit at " + time + " client time";

    FileWriter fw = new FileWriter("clientLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }
}
