/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:59:07
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

public interface ScenarioCreatedListenerRMI extends java.rmi.Remote
{
    /** pass on details of the creation of a new scenario
   *
   */
  public void scenarioCreated(int index) throws java.rmi.RemoteException;;
  /** pass on details of the destruction of a scenario
   *
   */
  public void scenarioDestroyed(int index) throws java.rmi.RemoteException;;
}
