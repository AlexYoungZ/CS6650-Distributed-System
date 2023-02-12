import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


/**
 * The Mailbox service Sender use ActiveMQ.
 */
public class Sender {

	// default JMS server address: tcp://localhost:61616
	// activeMQ admin console: http://localhost:8161/admin
	private static final String serverAddress = ActiveMQConnection.DEFAULT_BROKER_URL;

	// name the queue Mailbox
	private static final String queueName = "Mailbox";

	/**
	 * The entry point of Mailbox application.
	 *
	 * @param args the input arguments
	 * @throws IOException the io exception
	 */
	public static void main(String[] args) throws IOException {
		Sender sender = new Sender();
		sender.enqueueNotifications();
	}

	/**
	 * Enqueue message.
	 *
	 * @throws IOException the io exception
	 */
	public void enqueueNotifications() throws IOException {
		BufferedReader inlineReader = new BufferedReader(new InputStreamReader(System.in));
		try {

			// create a connection factory to create connections at given address
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(serverAddress);
			System.out.println("JMS Server is running on: " + serverAddress);

			// create a queue connection
			Connection connection = connectionFactory.createConnection();
			connection.start();

			// create a queue session to send/receive message
			// set delivery mode to AUTO_ACKNOWLEDGE in case server crashes and guarantee delivery
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// create a queue on server
			Destination destination = session.createQueue(queueName);

			String messageStr;

			// Enable server to store multiple messages
			while (true) {
				System.out.println("Enter the new notification to send or 'quit' to exit: ");
				messageStr = inlineReader.readLine();
				if ("quit".equals(messageStr)) {
					System.out.println("Stop the Mailbox service");
					break;
				}
				// create a message to send
				MessageProducer producer = session.createProducer(destination);
				TextMessage message = session.createTextMessage(messageStr);

				// send message
				producer.send(message);

				// show message sent on server side console
				System.out.println("Sent message: '" + message.getText() + "' on queue: " + queueName);
			}
			// close the connection
			inlineReader.close();
			connection.close();
		} catch (JMSException jmsException) {
			System.out.println(jmsException.getMessage());
			;
		}
	}
}
