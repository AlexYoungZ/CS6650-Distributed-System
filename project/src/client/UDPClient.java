import java.io.Console;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Objects;

/** The type Client. */
public class UDPClient {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException the io exception
   */
  public static void main(String[] args) throws IOException {
    // basic args check
    if (args.length < 2) {
      System.out.println("Example: java Client localhost 3200");
      System.exit(1);
    }

    String hostname = args[0];
    // System.out.println("hostname is: "+hostname);

    // initialize ClientLogger
    ClientLogger clientLogger;
    DatagramPacket requestPacket;
    DatagramPacket responsePacket;
    String exception;
    String request;
    String response;
    String operation;
    String message;

    int port = Integer.parseInt(args[1]);

    try { // create socket
      DatagramSocket clientSocket = new DatagramSocket();
      // timeout mechanism: set waiting time out to 20s
      clientSocket.setSoTimeout(20000);
      // get server address
      InetAddress serverAddress = InetAddress.getByName(hostname);
      // create console to interact
      Console console = System.console();

      do {
        byte[] buffer = new byte[256];
        // read user input
        request = console.readLine("Enter text: ");
        //        System.out.println("Check if this doesn't change: " + request);

        if (request.length() != 0) {
          message = request.trim().toLowerCase();

          if (message.contains("put") || message.contains("PUT")) {
            operation = "PUT";
          } else if (message.contains("get") || message.contains("GET")) {
            operation = "GET";
          } else if (message.contains("delete") || message.contains("DELETE")) {
            operation = "DELETE";
          } else {
            operation = "No valid operation";
          }

          // write request into client log
          ClientLogger.clientLoggingRequest(request, operation, port, hostname);

          // create request packet with client input and send it to server
          requestPacket =
              new DatagramPacket(
                  request.getBytes(), request.getBytes().length, serverAddress, port);
          clientSocket.send(requestPacket);

          System.out.println("request: " + request);

          // create receive packet to receive response from server
          responsePacket = new DatagramPacket(buffer, buffer.length);
          clientSocket.receive(responsePacket);

          response = new String(buffer, 0, responsePacket.getLength());

          // write server response into client log
          ClientLogger.clientLoggingResponse(response, operation, serverAddress.toString());

          // display server response at client side
          System.out.println(response);

          // TODO: close here?
          clientSocket.close();
        }
      } while (!Objects.equals(request, "quit"));
      // close up
      System.out.println("Closing UDP client");
      // todo handle close up exception, format UDP log for both client and server
      clientSocket.close();


    } catch (SocketException socketException) { // socket and io exception handle
      exception = socketException.toString();
      System.err.println("Socket exception: " + socketException.getMessage());
      //      socketException.printStackTrace();
      ClientLogger.clientLoggingException(exception);

    } catch (InterruptedIOException interruptedIOException) {
      exception = interruptedIOException.toString();

      System.err.println("Server timed out: " + interruptedIOException.getMessage());
      //      interruptedIOException.printStackTrace();
      ClientLogger.clientLoggingException(exception);

    } catch (IOException ioException) {
      exception = ioException.toString();
      System.err.println("Network I/O exception: " + ioException.getMessage());
      //      ioException.printStackTrace();
      ClientLogger.clientLoggingException(exception);
    }
  }
}
