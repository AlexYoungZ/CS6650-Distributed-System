import java.rmi.Remote;
import java.rmi.RemoteException;

/** The Proposer interface. */
public interface ProposerInterface extends Remote {
  /**
   * Create unique and increasing proposal id.
   *
   * @return the int
   * @throws RemoteException the remote exception
   */
  int createProposalID() throws RemoteException;
}
