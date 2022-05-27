import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

/** The UDP Server. */
public class UDPServer {

  /**
   * The entry point of UDPServer.
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

    // initialize Datagram packet
    DatagramPacket requestPacket;
    DatagramPacket responsePacket;

    String exception;

    int port = Integer.parseInt(args[0]);

    try {
      // create server socket on given port
      DatagramSocket serverSocket = new DatagramSocket(port);
      System.out.println("Server is listening on port: " + port);

      byte[] receivedBuffer = new byte[256];
      byte[] sentBuffer = new byte[256];

      while (true) {

        requestPacket = new DatagramPacket(receivedBuffer, receivedBuffer.length);
        serverSocket.receive(requestPacket);

        // get client address and port
        InetAddress clientAddress = requestPacket.getAddress();
        String clientAddressStr = clientAddress.toString();
        int clientPort = requestPacket.getPort();

        System.out.println("Receive packet from " + clientAddressStr + " : " + clientPort);

        // get request sent from client, only accept PUT/GET/DELETE with a valid KEY
        String request = new String(requestPacket.getData(), 0, requestPacket.getLength());

        // initialize parameters
        String operation; // operation after formatting
        String response;


        // get Operation type
        operation = UDPHandler.getOperationType(request);

        // call UDP handler to handle client request and return response
        response = UDPHandler.handleUDPRequest(request, map, clientAddressStr, port);

        // display response at server side
        System.out.println("Server response: " + response);

        //  write to server log file use Server logger
        ServerLogger.serverLogging(request, response, operation, clientPort, clientAddressStr);

        sentBuffer = response.getBytes();

        // create response packet
        responsePacket =
            new DatagramPacket(sentBuffer, sentBuffer.length, clientAddress, clientPort);

        serverSocket.send(responsePacket);
        //        serverSocket.close();
      }

    } catch (IOException ioException) {
      // IO exception handling and server log exception
      exception = ioException.toString();
      System.out.println("IO exception: " + ioException.getMessage());
      ioException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    } catch (NullPointerException nullPointerException) {
      // null pointer exception handling and server log exception
      exception = nullPointerException.toString();
      System.out.println("Client input is invalid: " + nullPointerException.getMessage());
      nullPointerException.printStackTrace();
      ServerLogger.serverExceptionLogging(exception);
    }
  }
}
