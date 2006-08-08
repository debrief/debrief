/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:59:07
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

public interface ParticipantsChangedListenerRMI extends java.rmi.Remote
{
    /** the indicated participant has been added to the scenario
     *
     */
    public void newParticipant(int index) throws java.rmi.RemoteException;
    /** the indicated participant has been removed from the scenario
     *
     */
    public void participantRemoved(int index) throws java.rmi.RemoteException;

    /** the scenario has restarted, reset
     *
     */
    public void restart() throws java.rmi.RemoteException;
}
