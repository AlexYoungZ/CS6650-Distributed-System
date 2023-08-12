import java.io.Serializable;

/** The Task to send among servers. */
public class Task implements Serializable {
  public TaskType Type;
  public ClientRequest clientRequest = new ClientRequest();
  public int clientNumber;
  public int ProposerID; // track server number
  public int ProposalID;

  @Override
  public String toString() {
    return "Task{"
        + "Type="
        + Type
        + ", clientRequest="
        + clientRequest
        + ", ProposerID="
        + ProposerID
        + ", ProposalID="
        + ProposalID
        + '}';
  }
}
