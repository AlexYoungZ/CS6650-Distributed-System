import java.util.HashMap;
import java.util.Objects;

/** UDP client request handler. */
public class UDPHandler {

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
   * @param clientRequest the client request
   * @param serverMap the server hashmap
   * @param clientAddress the client address
   * @param clientPort the client port
   * @return the response string
   */
  public static String handleUDPRequest(
      String clientRequest,
      HashMap<String, String> serverMap,
      String clientAddress,
      Integer clientPort) {
    String message = clientRequest.trim().toLowerCase();

    // initialize parameters
    Integer len = clientRequest.length();
    String key;
    String value;
    String pair; // key,value
    String response;

    if (message.contains("put")) {
      pair = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      key = pair.split(",")[0].trim();
      value = pair.split(",")[1].trim();

      serverMap.put(key, value);
      response = "Put key: " + key + ", value: " + value + " pair in map";
    } else if (message.contains("get")) {

      key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      if (serverMap.containsKey(key)) {
        value = serverMap.get(key);
        response = "Get value: " + value + " with given key: " + key;
      } else {
        response = "Didn't find matching value with given key: " + key;
      }
    } else if (message.contains("delete")) {

      key = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
      if (serverMap.containsKey(key)) {
        value = serverMap.remove(key);
        response = "Delete value: " + value + " with given key: " + key;
      } else if (message.contains("quit")) {
        response = "All packets sent done, client close connection";
      } else {
        response = "Didn't find matching value with given key: " + key;
      }
    } else if (Objects.equals(message, "quit")) {
      System.out.println("Closing connection");
      response = String.format(" Received quit request from %s : %d", clientAddress, clientPort);
    } else {
      response =
          String.format(
              " Received malformed request of length %d from %s : %d",
              len, clientAddress, clientPort);
    }
    return response;
  }
}
