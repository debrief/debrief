/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 09:56:33
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI;

import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.ParticipantType;
import ASSET.Participants.*;

public interface ParticipantRMI extends java.rmi.Remote
{

  /** get the name of this scenario
   *
   */
  String getName()  throws java.rmi.RemoteException;

  /** get the radiated noise of this participant in this bearing in this medium
   *
   */
  double getRadiatedNoiseFor(int medium, double brg_degs)  throws java.rmi.RemoteException;

  /** get the current status of this object
   *
   */
  Status getStatus() throws java.rmi.RemoteException;

  /** get the category for this participant
   *
   */
  Category getCategory() throws java.rmi.RemoteException;

  /** break down the category components
   *
   */
  String getType() throws java.rmi.RemoteException;;

  /** get the force for this participant
   *
   */
  String getForce() throws java.rmi.RemoteException;;

  /** get the environment for this participant
   *
   */
  String getEnvironment() throws java.rmi.RemoteException;;

  /** get the current activity for this participant
   *
   */
  String getActivity() throws java.rmi.RemoteException;

}
