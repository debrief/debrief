/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:51:46
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

public interface ParticipantMovedListenerRMI extends java.rmi.Remote
{
  /** this participant has moved
   *
   */
  public void moved(ASSET.Participants.Status newStatus) throws java.rmi.RemoteException;

  /** the scenario has restarted
   *
   */
  public void restart() throws java.rmi.RemoteException;

}
