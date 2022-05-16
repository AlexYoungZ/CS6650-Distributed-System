package neu.siyangzhang;

import java.net.*;
import java.io.*;

public class Server {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Example: java Server 3200");
      System.exit(1);
    }


    int port = Integer.parseInt(args[0]);

    try (ServerSocket serverSocket = new ServerSocket(port) // create service on port
    ) {
      System.out.println("Server is listening on port: " + port);

      Socket socket = serverSocket.accept(); // wait and accept
      System.out.println("Client's connection success!");

      InputStream inputStream = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

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
      // System.out.println(response);

      writer.write("Server response: " + response);
      writer.newLine();
      writer.flush();

      System.out.println("Closing connection");
      reader.close();
      outputStream.close();
      writer.close();
      socket.close();
    } catch (IOException ioException) {
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    }
  }
}
