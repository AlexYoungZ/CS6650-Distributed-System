package neu.siyangzhang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

/** This is Description */
public class Client {
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Example: java Client localhost 3200"); // since start server at localhost
      System.exit(1);
    }

    if (args.length > 80) {
      System.out.println("String should be less than 80 characters");
      System.exit(1);
    }

    String hostname = args[0];
    // System.out.println("hostname is: "+hostname);

    int port = Integer.parseInt(args[1]);

    try (Socket socket = new Socket(hostname, port)) {
      Console console = System.console();
      String request;
      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      request = console.readLine("Enter text: ");
      writer.write(request);
      writer.newLine();
      writer.flush();
      // System.out.println("request: " + request);

      // retrieve response from server
      InputStream inputStream = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      String response = reader.readLine();
      System.out.println(response);
      System.out.println("Closing connection");
      outputStream.close();
      writer.close();
      reader.close();


    } catch (SocketException socketException) {
      System.out.println("Socket exception: " + socketException.getMessage());
      socketException.printStackTrace();
    } catch (IOException ioException) {
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    }
  }
}
