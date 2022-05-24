import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ServerApp {

  public static void main(String[] args) {

    // basic args check
    if (args.length < 1) {
      System.out.println("Example: java Server 3200");
      System.exit(1);
    }

    // initialize hashmap
    HashMap<String, String> map = new HashMap<String, String>();

    // initialize ServerLogger
    ServerLogger serverLogger;

    int port = Integer.parseInt(args[0]);

    try (ServerSocket serverSocket = new ServerSocket(port) // create server socket on given port
    ) {
      System.out.println("Server is listening on port: " + port);

      while (true) {
        Socket socket = serverSocket.accept(); // wait and accept
        System.out.println("Client's connection success!");

        // get client address
        InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        String clientIpAddress = socketAddress.getAddress().getHostAddress();

        // creat I/O stream, reader and writer
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        OutputStream outputStream = socket.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        // get request sent from client, only accept PUT/GET/DELETE with a valid KEY
        String request;
        String message;

        do {
          request = reader.readLine();
          // display request received at server side:
          System.out.println("Received request: " + request);

          Integer len = request.length();
          message = request.trim().toLowerCase();

          String key;
          String value;
          String operation;
          String pair;
          String response;

          if (message.contains("put") || message.contains("PUT")) {
            operation = "PUT";
            pair = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
            key = pair.split(",")[0].trim();
            value = pair.split(",")[1].trim();

            System.out.println("key: " + key);
            System.out.println("value: " + value);
            map.put(key, value);
            response = "Put key: " + key + ", value: " + value + " pair in map";
          } else if (message.contains("get") || message.contains("GET")) {
            operation = "GET";

            key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
            System.out.println("key: " + key);
            if (map.containsKey(key)) {
              value = map.get(key);
              response = "Get value: " + value + " with given key: " + key;
            } else {
              response = "Didn't find matching value with given key: " + key;
            }
          } else if (message.contains("delete") || message.contains("DELETE")) {
            operation = "DELETE";

            key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
            System.out.println("key: " + key);
            if (map.containsKey(key)) {
              value = map.remove(key);
              response = "Delete value: " + value + " with given key: " + key;
            } else {
              response = "Didn't find matching value with given key: " + key;
            }
          } else {
            response =
                String.format(
                    " Received malformed request of length %d from %s : %d",
                    len, clientIpAddress, port);
            operation = "No operation";
          }

          // display response at server side
          System.out.println("Server response: " + response);

          //  write to server log file use Server logger
          ServerLogger.serverLogging(request, response, operation, port, clientIpAddress);

          //  write response and flush
          writer.write("Server response: " + response);
          writer.newLine();
          writer.flush();
        } while (!message.equals("quit"));

        // close connection
        System.out.println("Closing connection");
        reader.close();
        outputStream.close();
        writer.close();
        socket.close();
      }

    } catch (IOException ioException) { // IO exception handling
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    } catch (NullPointerException nullPointerException) {
      System.out.println("Client input is invalid: " + nullPointerException.getMessage());
      nullPointerException.printStackTrace();
    }
  }
}
