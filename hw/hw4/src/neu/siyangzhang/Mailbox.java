package neu.siyangzhang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.naming.InitialContext;

import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.QueueSender;
import javax.jms.DeliveryMode;
import javax.jms.QueueSession;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

/** The Mailbox to send notification to subscribers. */
public class Mailbox {

  /**
   * The entry point of mailbox application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    Mailbox mailbox = new Mailbox();
    mailbox.enqueueMessage();
  }

  /** Enqueue message. */
  public void enqueueMessage() {
    BufferedReader inlineReader = new BufferedReader(new InputStreamReader(System.in));
    try {
      // Prompt for the JNDI Queue connection factory name
      //      System.out.println("Enter the Queue Connection Factory name:");
      //      String queueConnFactoryName = inlineReader.readLine();
      String queueConnFactoryName = "queue/queueFactory";
      //      System.out.println("Enter the Queue name:");
      //      String queueName = inlineReader.readLine();
      String queueName = "queue/queue0";

      // Look up for the administered objects of the Queue
      // get the initial context
      InitialContext context = new InitialContext();

      // lookup the queue object
      Queue queueReference = (Queue) context.lookup(queueName);
      context.close();

      // lookup the queue connection factory
      QueueConnectionFactory queueConnFactory =
          (QueueConnectionFactory) context.lookup(queueConnFactoryName);

      // Create the JMS objects from administered objects
      // create a queue connection
      QueueConnection queueConnection = queueConnFactory.createQueueConnection();
      // create a queue session
      QueueSession queueSession =
          queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      // create a queue sender
      QueueSender queueSender = queueSession.createSender(queueReference);
      queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

      // Enqueue multiple text messages entered one after the other
      String messageContent = null;
      while (true) {
        System.out.println("Enter the new message to send or 'quit' to exit the program:");
        messageContent = inlineReader.readLine();
        if ("quit".equals(messageContent)) break;
        // create a message to send
        TextMessage textMessage = queueSession.createTextMessage(messageContent);
        queueSender.send(textMessage);
        System.out.println("Messages sent: " + textMessage.getText());
      }
      // Clean Up
      inlineReader.close();
      queueConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
