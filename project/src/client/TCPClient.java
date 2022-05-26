import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/** The type Client. */
public class TCPClient {

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
    String exception;

    int port = Integer.parseInt(args[1]);

    try (Socket socket = new Socket(hostname, port)) { // create socket
      Console console = System.console(); // create console to interact

      // timeout mechanism: set waiting time out to 20s
      socket.setSoTimeout(20000);

      // get server address
      InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
      String serverAddress = socketAddress.getAddress().getHostAddress();

      // create output stream and writer
      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      String request;
      String response;
      String operation;
      String message; // request after clean up

      do {
        // read user input, write it into output stream then flush
        request = console.readLine("Enter text: ");

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

        writer.write(request);
        writer.newLine();
        writer.flush();
        // System.out.println("request: " + request);

        // create input stream and reader then retrieve response from server
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        response = reader.readLine();

        // write server response into client log
        ClientLogger.clientLoggingResponse(response, operation, serverAddress);

        // display server response at client side
        System.out.println(response);

      } while (!request.equals("quit"));

      // close up
      System.out.println("Closing connection");
      outputStream.close();
      writer.close();
      //      reader.close();

    } catch (SocketException socketException) { // socket and io exception handle
      exception = socketException.toString();
      System.err.println("Socket exception: " + socketException.getMessage());
      socketException.printStackTrace();
      ClientLogger.clientLoggingException(exception);

    } catch (InterruptedIOException interruptedIOException) {
      exception = interruptedIOException.toString();

      System.err.println("Server timed out: " + interruptedIOException.getMessage());
      interruptedIOException.printStackTrace();
      ClientLogger.clientLoggingException(exception);

    } catch (IOException ioException) {
      exception = ioException.toString();
      System.err.println("Network I/O exception: " + ioException.getMessage());
      ioException.printStackTrace();
      ClientLogger.clientLoggingException(exception);
    }
  }
}
