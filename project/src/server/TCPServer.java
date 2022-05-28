import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/** The Server app. */
public class TCPServer {

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
    HashMap<String, String> map = new HashMap<>();
    // initialize exception
    String exception;

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

          // initialize parameters
          String operation; // operation after formatting
          String response;
          message = request.trim().toLowerCase();

          // get Operation type
          operation = TCPHandler.getOperationType(request);

          // call TCP handler to handle client request and return response
          response = TCPHandler.handleTCPRequest(request, map, clientIpAddress, port);

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

    } catch (IOException ioException) {
      // IO exception handling and server log exception
      exception = ioException.toString();
      System.out.println("IO exception: " + ioException.getMessage());
      //      ioException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    } catch (NullPointerException | IndexOutOfBoundsException nullPointerException) {
      // null pointer exception handling and server log exception
      exception = nullPointerException.toString();
      System.out.println("Client input is invalid: " + nullPointerException.getMessage());
      //      nullPointerException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    }
  }
}
