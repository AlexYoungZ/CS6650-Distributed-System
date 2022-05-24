import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientLogger {


  public static void clientLoggingRequest(
      String request, String operation, Integer port, String clientAddress) throws IOException {

    String log;
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
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

  public static void clientLoggingResponse(String response, String operation, String serverAddress)
      throws IOException {

    String log;
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
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

  public static void clientLoggingException(String exceptionMessage) throws IOException {

    String log;
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .format(new Date(System.currentTimeMillis()));

    log = "Client get: " + exceptionMessage + " at " + time + " client time";

    FileWriter fw = new FileWriter("clientTCPLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }
}
