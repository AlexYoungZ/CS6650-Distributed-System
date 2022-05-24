import java.net.*;
import java.io.*;

public class Client {
  public static void main(String[] args) {
    // basic args check
    if (args.length < 2) {
      System.out.println("Example: java Client localhost 3200");
      System.exit(1);
    }

    String hostname = args[0];
    // System.out.println("hostname is: "+hostname);

    int port = Integer.parseInt(args[1]);

    try (Socket socket = new Socket(hostname, port)) { // create socket
      Console console = System.console(); // create console to interact
      String request;

      // create output stream and writer
      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      do {
        // read user input, write it into output stream then flush
        request = console.readLine("Enter text: ");

//        if (request.length() > 80) {
//          System.out.println("String should be less than 80 characters");
//          System.exit(1);
//        }

        writer.write(request);
        writer.newLine();
        writer.flush();
        // System.out.println("request: " + request);

        // create input stream and reader then retrieve response from server
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String response = reader.readLine();
        System.out.println(response);

      } while (!request.equals("quit"));

      // close up
      System.out.println("Closing connection");
      outputStream.close();
      writer.close();
//      reader.close();

    } catch (SocketException socketException) { // socket and io exception handle
      System.out.println("Socket exception: " + socketException.getMessage());
      socketException.printStackTrace();
    } catch (IOException ioException) {
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    }
  }
}
