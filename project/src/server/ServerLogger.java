import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ServerLogger {

  public static void serverLogging(
      String request, String response, String operation, Integer port, String clientAddress)
      throws IOException {

    String log;
    //  "yyyy-MM-dd HH:mm:ss.SSSZ"
    String time =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .format(new Date(System.currentTimeMillis()));

    log =
        "Server receive: "
            + request
            + " from "
            + clientAddress
            + " at "
            + port
            + " and perform "
            + operation
            + " operation at "
            + time
            + " server time with server response: "
            + response;

    FileWriter fw = new FileWriter("serverTCPLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    bw.write(log);
    bw.newLine();
    bw.close();
  }
}
