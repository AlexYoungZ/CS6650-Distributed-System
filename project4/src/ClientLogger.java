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
   * @param clientNumber the client number
   */
  public static void clientLoggingRequest(String request, int clientNumber) {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client" + clientNumber + " send: " + request + " at " + time;

    try {
      FileWriter fw = new FileWriter("../logs/client" + clientNumber + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException e) {
      System.out.println("Fails to log client request. Exception: " + e.getMessage());
    }
  }

  /**
   * Client logging response.
   *
   * @param response the response
   * @param clientNumber the client number
   */
  public static void clientLoggingResponse(String response, int clientNumber) {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client" + clientNumber + " receive " + response + " at " + time;
    try {

      FileWriter fw = new FileWriter("../logs/client" + clientNumber + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException e) {
      System.out.println("Fails to log response from learner. Exception: " + e.getMessage());
    }
  }

  /**
   * Client logging exception.
   *
   * @param exceptionMessage the exception message
   * @param clientNumber the client number
   */
  public static void clientLoggingException(String exceptionMessage, int clientNumber) {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client" + clientNumber + " get: " + exceptionMessage + " at " + time + " client time";

    try {
      FileWriter fw = new FileWriter("../logs/client" + clientNumber + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException e) {
      System.out.println("Fails to log exception: " + e.getMessage());
    }
  }

  /**
   * Client logging exit.
   *
   * @param clientNumber the client number
   */
  public static void clientLoggingExit(int clientNumber) {

    String log;
    String time =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            .format(new Date(System.currentTimeMillis()));

    log = "Client " + clientNumber + " quit at " + time + " client time";
    try {

      FileWriter fw = new FileWriter("../logs/client" + clientNumber + "Log.txt", true);
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(log);
      bw.newLine();
      bw.close();
      fw.close();
    } catch (IOException e) {
      System.out.println("Fails to log exit. Exception: " + e.getMessage());
    }
  }
}
