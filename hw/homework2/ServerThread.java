package neu.siyangzhang;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread {

  private Socket socket;

  public ServerThread(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      // creat I/O stream, reader and writer
      InputStream inputStream = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

      OutputStream outputStream = socket.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

      // get request sent from client, first reverse string then reverse-case
      String message;

      do {
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
      } while (!message.equals("quit"));

      // close connection
      System.out.println("Closing connection");
      reader.close();
      outputStream.close();
      writer.close();
      socket.close();
    } catch (IOException ioException) { // IO exception handling
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
    }
  }
}
