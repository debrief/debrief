/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:56:33
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;


public interface ScenarioRMI extends java.rmi.Remote
{

  /** get the name of this scenario
   *
   */
  String getName()  throws java.rmi.RemoteException;

  /** set the name of this scenario
   *
   */
  void setName(String val) throws java.rmi.RemoteException;

  ////////////////////////////////////////////////////////
  // listener-related
  ////////////////////////////////////////////////////////
  public void addParticipantsChangedListener(ParticipantsChangedListenerRMI listener)throws java.rmi.RemoteException;
  public void removeParticipantsChangedListener(ParticipantsChangedListenerRMI listener)throws java.rmi.RemoteException;
//
//  public void addScenarioRunningListener(ScenarioRunningListenerRMI listener);
//  public void removeScenarioRunningListener(ScenarioRunningListenerRMI listener);

  public void addScenarioSteppedListener(ScenarioSteppedListenerRMI listener)throws java.rmi.RemoteException;
  public void removeScenarioSteppedListener(ScenarioSteppedListenerRMI listener)throws java.rmi.RemoteException;

  // property change listeners
  public void addPropertyChangeListener(PropertyChangeListenerRMI listener)throws java.rmi.RemoteException;
  public void removePropertyChangeListener(PropertyChangeListenerRMI listener)throws java.rmi.RemoteException;

  ////////////////////////////////////////////////////////
  // participant-related
  ////////////////////////////////////////////////////////
  /**
  * Return a particular Participant - so that the Participant can be controlled directly.  Listeners added/removed. Participants added/removed, etc.
  */
  ParticipantRMI getThisParticipant(int id)throws java.rmi.RemoteException;;

  /**
  * Provide a list of id numbers of Participant we contain
  * @return list of ids of Participant we contain
  * @param ditch the param - it's just to please Together*/
  public Integer[] getListOfParticipants()throws java.rmi.RemoteException;;



}
