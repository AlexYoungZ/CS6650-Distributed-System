/** The Proposal. */
public class Proposal {
  /** The Proposal id. */
  protected int ProposalID;
  /** The Client request. */
  protected ClientRequest clientRequest;
  /** The Client number. */
  int clientNumber;
  // the number of promise received
  protected int promiseNumReceived;

  @Override
  public String toString() {
    return "Proposal{"
        + "ProposalID="
        + ProposalID
        + ", clientRequest="
        + clientRequest
        + ", promiseNumReceived="
        + promiseNumReceived
        + '}';
  }
}
