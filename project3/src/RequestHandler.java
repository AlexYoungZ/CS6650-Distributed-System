import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** client request handler. */
public class RequestHandler {
  static String key;
  static String value;


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
   * Handle client request and return server's response as string.
   *
   * @param request the client request
   * @param map the server hashmap
   * @param clientHost the client address
   * @return the response string
   */
  public static String handleRequest(
      String request, ConcurrentHashMap<String, String> map, String clientHost)
      throws ArrayIndexOutOfBoundsException {

    Integer len = request.length();
    String message = request.trim().toLowerCase();


    String pair;
    String response;

    if (message.contains("put")) {
      pair = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")")).trim();

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
      response = String.format(" Received quit request from %s", clientHost);
    } else {
      response =
          String.format(
              " Received malformed request of length %d from %s", len, clientHost);
    }
    return response;
  }
}
