import java.util.HashMap;
import java.util.Objects;

/** TCP client request handler. */
public class TCPHandler {

  /**
   * Gets operation type.
   *
   * @param request the request
   * @return the operation type
   */
  public static String getOperationType(String request) {
    String operation;
    String message = request.trim().toLowerCase();
    if (message.contains("put")) {
      operation = "PUT";
    } else if (message.contains("get")) {
      operation = "GET";
    } else if (message.contains("delete")) {
      operation = "DELETE";
    } else if (Objects.equals(message, "quit")) {
      operation = "No operation";
    } else {
      operation = "No operation";
    }
    return operation;
  }

  /**
   * Handle udp client request and return server's response as string.
   *
   * @param request the client request
   * @param map the server hashmap
   * @param clientIpAddress the client address
   * @param port the client port
   * @return the response string
   */
  public static String handleUDPRequest(
      String request, HashMap<String, String> map, String clientIpAddress, Integer port) {

    Integer len = request.length();
    String message = request.trim().toLowerCase();

    String key;
    String value;
    String pair;
    String response;

    if (message.contains("put")) {
      pair = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      key = pair.split(",")[0].trim();
      value = pair.split(",")[1].trim();

      map.put(key, value);
      response = "Put key: " + key + ", value: " + value + " pair in map";
    } else if (message.contains("get")) {

      key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      System.out.println("key: " + key);
      if (map.containsKey(key)) {
        value = map.get(key);
        response = "Get value: " + value + " with given key: " + key;
      } else {
        response = "Didn't find matching value with given key: " + key;
      }
    } else if (message.contains("delete")) {

      key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      System.out.println("key: " + key);
      if (map.containsKey(key)) {
        value = map.remove(key);
        response = "Delete value: " + value + " with given key: " + key;
      } else {
        response = "Didn't find matching value with given key: " + key;
      }
    } else if (Objects.equals(message, "quit")) {
      // close connection
      System.out.println("Closing connection");
      response = String.format(" Received quit request from %s : %d", clientIpAddress, port);
    } else {
      response =
          String.format(
              " Received malformed request of length %d from %s : %d", len, clientIpAddress, port);
    }

    return response;
  }
}
