import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
  /*
   * URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
   *
   * default broker URL is : tcp://localhost:61616"
   *
   * admin console will run at http://localhost:8161/admin
   */
  private static final String serverAddress = ActiveMQConnection.DEFAULT_BROKER_URL;

  /*
   * Queue Name.You can create any/many queue names as per your requirement.
   */
  private static final String queueName = "queue1";

  public static void main(String[] args) throws JMSException {
    System.out.println("JMS Server is running on: " + serverAddress);

    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(serverAddress);

    // create a queue connection
    Connection connection = connectionFactory.createConnection();
    connection.start();

    // create a queue session to send/receive message
    // set delivery mode to AUTO_ACKNOWLEDGE in case server crashes and guarantee delivery
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // create a queue on server
    Destination destination = session.createQueue(queueName);

    //
    MessageProducer producer = session.createProducer(destination);
    TextMessage message = session.createTextMessage("Mail content: hello word!");

    producer.send(message);

    System.out.println("Message Sent: " + message.getText());

    // close the connection
    connection.close();
  }
}
