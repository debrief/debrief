/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:59:07
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

public interface ScenarioSteppedListenerRMI extends java.rmi.Remote
{
    /** the scenario has stepped forward
     *
     */
    public void step(long newTime) throws java.rmi.RemoteException;

    /** the scenario has restarted, reset
     *
     */
    public void restart() throws java.rmi.RemoteException; 
}
