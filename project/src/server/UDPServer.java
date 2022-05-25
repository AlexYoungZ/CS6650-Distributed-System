import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

/** The type Server app. */
public class UDPServer {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) throws IOException {

    // basic args check
    if (args.length < 1) {
      System.out.println("Example: java Server 3200");
      System.exit(1);
    }

    // initialize hashmap
    HashMap<String, String> map = new HashMap<String, String>();

    // initialize ServerLogger
    ServerLogger serverLogger;
    DatagramPacket requestPacket;
    DatagramPacket responsePacket;

    String exception;

    int port = Integer.parseInt(args[0]);

    try (DatagramSocket serverSocket =
        new DatagramSocket(port) // create server socket on given port
    ) {
      System.out.println("Server is listening on port: " + port);

      while (true) {
        byte[] buffer = new byte[256];

        requestPacket = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(requestPacket);

        // get client address and port
        InetAddress clientAddress = requestPacket.getAddress();
        int clientPort = requestPacket.getPort();

        System.out.println("Receive packet from " + clientAddress.toString() + " : " + clientPort);

        // get request sent from client, only accept PUT/GET/DELETE with a valid KEY
        String request = new String(requestPacket.getData(), 0, requestPacket.getLength());
        String message;

        do {
          //          request = reader.readLine();
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
            } else if (message.contains("quit")) {
              response = "All packets sent done, client close connection"; // TODO still need this?
            } else {
              response = "Didn't find matching value with given key: " + key;
            }
          } else {
            response =
                String.format(
                    " Received malformed request of length %d from %s : %d",
                    len, clientAddress, clientPort);
            operation = "No operation";
          }

          // display response at server side
          System.out.println("Server response: " + response);

          //  write to server log file use Server logger
          ServerLogger.serverLogging(
              request, response, operation, clientPort, clientAddress.toString());

          // create response packet
          responsePacket =
              new DatagramPacket(
                  response.getBytes(), response.getBytes().length, clientAddress, clientPort);

          serverSocket.send(responsePacket);

        } while (!message.equals("quit"));

        // close connection
        System.out.println("Closing connection");
        serverSocket.close();
        break;
      }

    } catch (IOException ioException) {
      // IO exception handling and server log exception
      exception = ioException.toString();
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    } catch (NullPointerException nullPointerException) {
      // null pointer exception handling and server log exception
      exception = nullPointerException.toString();
      System.out.println("Client input is invalid: " + nullPointerException.getMessage());
      nullPointerException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    }
  }
}
