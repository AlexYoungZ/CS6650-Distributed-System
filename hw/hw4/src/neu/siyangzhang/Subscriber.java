package neu.siyangzhang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

/** The Subscriber listening to Mailbox. */
public class Subscriber implements MessageListener {
  // flag var to stop receive message
  private boolean stopReceivingMessages = false;

  /**
   * The entry point of Subscriber.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    Subscriber subscriber = new Subscriber();
    subscriber.startReceivingMessages();
  }

  /** Start receiving messages. */
  public void startReceivingMessages() {
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
      // lookup the queue connection factory
      QueueConnectionFactory queueConnFactory =
          (QueueConnectionFactory) context.lookup(queueConnFactoryName);
      // lookup the queue object
      Queue queueReference = (Queue) context.lookup(queueName);
      context.close();

      // Create the JMS objects from administered objects
      // create a queue connection
      QueueConnection queueConnection = queueConnFactory.createQueueConnection();
      // create a queue session
      QueueSession queueSession =
          queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      // create a queue receiver
      QueueReceiver queueMessageReceiver = queueSession.createReceiver(queueReference);
      // create message listener on the receiver
      queueMessageReceiver.setMessageListener(this);
      // start the connection
      queueConnection.start();

      // Keep receiving the messages from the queue until the stop
      // receiving messages command is received
      while (!stopReceivingMessages) {
        Thread.sleep(1000);
      }
      // Clean Up
      System.out.println("Messages successfully received so far, Stop receiving messages!");
      queueConnection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onMessage(Message message) {
    try {
      // receive a message and print
      String messageContent = ((TextMessage) message).getText();
      System.out.println(messageContent);
      if ("quit".equals(messageContent)) stopReceivingMessages = true;
    } catch (JMSException e) {
      e.printStackTrace();
      stopReceivingMessages = true;
    }
  }
}
