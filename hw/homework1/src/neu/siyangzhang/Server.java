package neu.siyangzhang;

import java.net.*;
import java.io.*;

public class Server {

  public static void main(String[] args) {

    // basic args check
    if (args.length < 1) {
      System.out.println("Example: java Server 3200");
      System.exit(1);
    }

    int port = Integer.parseInt(args[0]);

    try (ServerSocket serverSocket = new ServerSocket(port) // create server socket on given port
    ) {
      System.out.println("Server is listening on port: " + port);

      Socket socket = serverSocket.accept(); // wait and accept
      System.out.println("Client's connection success!");

      // creat I/O stream, reader and writer
      InputStream inputStream = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      // get request sent from client, first reverse string then reverse-case
      String message;
      message = reader.readLine();
      StringBuilder sb = new StringBuilder(message).reverse();
      for (int i = 0; i < sb.length(); i++) {
        char c = sb.charAt(i);
        if (Character.isLowerCase(c)) {
          sb.setCharAt(i, Character.toUpperCase(c));
        } else {
          sb.setCharAt(i, Character.toLowerCase(c));
        }
      }
      String response = sb.toString();
      //       System.out.println(response);

      //  write response and flush
      writer.write("Server response: " + response);
      writer.newLine();
      writer.flush();

      // close connection
      System.out.println("Closing connection");
      reader.close();
      outputStream.close();
      writer.close();
      socket.close();
    } catch (IOException ioException) { // IO exception handling
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    } catch (NullPointerException nullPointerException) {
      System.out.println("Client input is invalid: " + nullPointerException.getMessage());
      nullPointerException.printStackTrace();
    }
  }
}
