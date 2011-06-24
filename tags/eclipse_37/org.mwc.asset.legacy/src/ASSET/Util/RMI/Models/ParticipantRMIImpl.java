/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 11:44:06
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Models;

import ASSET.Util.RMI.ParticipantRMI;
import ASSET.Participants.Status;
import ASSET.Participants.Category;
import ASSET.ParticipantType;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ParticipantRMIImpl extends UnicastRemoteObject implements ParticipantRMI
{
  /*****************************************************************
   * member variables
   ****************************************************************/

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/** the participant we are wrapping
   *
   */
  private ParticipantType _myPart;

  /*****************************************************************
   * constructor
   ****************************************************************/


  /** constructor, with the participant we are going to wrap
   *
   */
  public ParticipantRMIImpl(final ParticipantType myPart) throws RemoteException
  {
    super();
    _myPart = myPart;
  }

  /** return our name
   *
   */
  public String getName() throws RemoteException
  {
    return _myPart.getName();
  }

  /** return our status
   *
   */
  public Status getStatus() throws RemoteException
  {
    return _myPart.getStatus();
  }

  public Category getCategory() throws RemoteException
  {
    return _myPart.getCategory();
  }

  public String getType() throws RemoteException
  {
    return _myPart.getCategory().getType();
  }

  public String getForce() throws RemoteException
  {
    return _myPart.getCategory().getForce();
  }

  public String getEnvironment() throws RemoteException
  {
    return _myPart.getCategory().getEnvironment();
  }

  /** get the current activity for this participant
   *
   */
  public String getActivity() throws java.rmi.RemoteException
  {
    return _myPart.getActivity();
  }

  public double getRadiatedNoiseFor(final int medium, final double brg_degs)  throws java.rmi.RemoteException
  {
    return _myPart.getRadiatedNoiseFor(medium, brg_degs);
  }
}
