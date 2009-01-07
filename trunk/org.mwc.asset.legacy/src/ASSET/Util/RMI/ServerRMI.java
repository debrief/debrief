/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:54:39
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;


public interface ServerRMI extends java.rmi.Remote
{

  /** add object as a listener for this type of event
  *
  */
  public void addScenarioCreatedListener(ScenarioCreatedListenerRMI listener) throws java.rmi.RemoteException;

  /** remove listener for this type of event
  *
  */
  public void removeScenarioCreatedListener(ScenarioCreatedListenerRMI listener) throws java.rmi.RemoteException;

  /**
  * Return a particular scenario - so that the scenario can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
  */
  ScenarioRMI getThisScenario(int id) throws java.rmi.RemoteException;

  /** return the hostname of this machine this scenario is currently running on
   *
   */
  public String getHostname() throws java.rmi.RemoteException;

  /**
  * Provide a list of id numbers of scenarios we contain
  * @return list of ids of scenarios we contain
  * @param ditch the param - it's just to please Together*/
  public Integer[] getListOfScenarios() throws java.rmi.RemoteException;

//  /**
//  * Create a new scenario.  The external client can then request the scenario itself to perform any edits
//  * @param scenario_type the type of scenario the client wants
//  * @return the id of the new scenario
//  */
//  int createNewScenario(String scenario_type) throws java.rmi.RemoteException;
//
//  /** destroy a scenario (calls the close() method on the scenario, which triggers the close)
//  *
//  */
//  public void closeScenario(int index) throws java.rmi.RemoteException;

  final public static String LOOKUP_NAME = "ASSET_Server";

}
