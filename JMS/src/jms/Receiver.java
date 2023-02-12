import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * The Subscriber listening to Mailbox.
 */
public class Receiver {


	/**
	 * The entry point of Subscriber application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

			// create a queue connection
			Connection connection = factory.createConnection();

			// start the connection
			connection.start();

			// create a queue session to send/receive message
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create a queue
			Destination queue = session.createQueue("Mailbox");

			MessageConsumer consumer = session.createConsumer(queue);

			Message message = consumer.receive();

			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String text = textMessage.getText();
				System.out.println("Client receive message: " + text);
			}

			// Clean Up
			session.close();
			connection.close();
			System.exit(1);
		} catch (JMSException exception) {
			System.out.println(exception.getMessage());
		}
	}
}