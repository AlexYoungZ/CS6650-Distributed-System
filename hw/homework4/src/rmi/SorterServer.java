import java.rmi.Naming;

public class SorterServer {

  //  public SorterServer() {
  //
  //  }

  public static void main(String[] args) {
    try {
      Sorter sorter = new SorterImpl();
      Naming.rebind("rmi://localhost:5000/SorterService", sorter);
    } catch (Exception exception) {
      System.out.println("Exception: " + exception.getMessage());
    }
//    new SorterServer();
  }
}
