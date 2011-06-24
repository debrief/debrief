/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 11:57:05
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Scenario.ParticipantsChangedListener;
import ASSET.Scenario.ScenarioSteppedListener;
import ASSET.Util.RMI.ParticipantRMI;
import ASSET.Util.RMI.ParticipantsChangedListenerRMI;
import ASSET.Util.RMI.PropertyChangeListenerRMI;
import ASSET.Util.RMI.ScenarioRMI;
import ASSET.Util.RMI.ScenarioSteppedListenerRMI;
import ASSET.Util.RMI.Models.ParticipantRMIImpl;

public class ScenarioRMIImpl extends UnicastRemoteObject implements ScenarioRMI,
        ParticipantsChangedListener,
        ScenarioSteppedListener,
        PropertyChangeListener
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/*****************************************************************
   * member variables
   ****************************************************************/

  /** our scenario
   *
   */
  private ScenarioType _myScenario;


  /** list of participants changed listeners
   *
   */
  private Vector<ParticipantsChangedListenerRMI> _partsChangedListeners = new Vector<ParticipantsChangedListenerRMI>(0,1);

  /** list of scenario stepped listenerd
   *
   */
  private Vector<ScenarioSteppedListenerRMI> _scenarioSteppedListeners = new Vector<ScenarioSteppedListenerRMI>(0,1);

  /** property change support for this item
   *
   */
  private Vector<PropertyChangeListenerRMI> _changeListeners = new Vector<PropertyChangeListenerRMI>(0,1);

  /** keep a cache of wrapped participants
   *
   */
  private HashMap<ParticipantType, ParticipantRMIImpl> _participantWrappersCache = new HashMap<ParticipantType, ParticipantRMIImpl>();

  /*****************************************************************
   * constructor
   ****************************************************************/


  /** constructor, with the scenario we are going to wrap
   *
   */
  public ScenarioRMIImpl(final ScenarioType myScenario) throws RemoteException
  {
    super();
    this._myScenario = myScenario;
    _myScenario.addParticipantsChangedListener(this);
    _myScenario.addScenarioSteppedListener(this);
  }

  ////////////////////////////////////////////////////////
  // listener-related
  ////////////////////////////////////////////////////////
  // property change listeners
  public void addPropertyChangeListener(final PropertyChangeListenerRMI listener) throws RemoteException
  {
    if(_changeListeners.isEmpty())
    {
      _myScenario.addPropertyChangeListener(null, this);
    }

    _changeListeners.add(listener);
  }

  public void removePropertyChangeListener(final PropertyChangeListenerRMI listener) throws RemoteException
  {
    _changeListeners.remove(listener);

    if(_changeListeners.isEmpty())
    {
      _myScenario.removePropertyChangeListener(null, this);
    }
  }

  public void propertyChange(final PropertyChangeEvent evt)
  {
    for (int i = 0; i < _changeListeners.size(); i++)
    {
      final PropertyChangeListenerRMI rmi = (PropertyChangeListenerRMI) _changeListeners.elementAt(i);
      try
      {
        rmi.propertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _partsChangedListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void addParticipantsChangedListener(final ParticipantsChangedListenerRMI listener) throws RemoteException
  {
    _partsChangedListeners.add(listener);
  }

  public void addScenarioSteppedListener(final ScenarioSteppedListenerRMI listener) throws RemoteException
  {
    _scenarioSteppedListeners.add(listener);
  }

  public Integer[] getListOfParticipants() throws RemoteException
  {
    final Integer[] list = _myScenario.getListOfParticipants();
    System.out.println("Scen RMI: length is" + list.length);
    return list;
  }

  public String getName() throws RemoteException
  {
    return _myScenario.getName();
  }

  public ParticipantRMI getThisParticipant(final int id) throws RemoteException
  {
    final ParticipantType thisPart = _myScenario.getThisParticipant(id);

    // have we got it cached already?
    ParticipantRMIImpl wrapper =  (ParticipantRMIImpl)_participantWrappersCache.get(thisPart);
    if(wrapper == null)
    {
      wrapper = new ParticipantRMIImpl(thisPart);
      _participantWrappersCache.put(thisPart, wrapper);
    }

    return wrapper;
  }

  public void removeParticipantsChangedListener(final ParticipantsChangedListenerRMI listener) throws RemoteException
  {
    System.out.println("Scenario removing:" + listener);
    _partsChangedListeners.remove(listener);
  }

  public void removeScenarioSteppedListener(final ScenarioSteppedListenerRMI listener) throws RemoteException
  {
    _scenarioSteppedListeners.remove(listener);
  }

  public void setName(final String val) throws RemoteException
  {
    _myScenario.setName(val);
  }

  /*****************************************************************
   * scenario listeners
   ****************************************************************/
  public void newParticipant(final int index)
  {
    System.out.println("about to inform about new part:" + index + " from:" + this);
    for (int i = 0; i < _partsChangedListeners.size(); i++)
    {
      System.out.println("informing about new part:" + index);
      final ParticipantsChangedListenerRMI rmi = (ParticipantsChangedListenerRMI) _partsChangedListeners.elementAt(i);
      try
      {
        rmi.newParticipant(index);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _partsChangedListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void participantRemoved(final int index)
  {
    for (int i = 0; i < _partsChangedListeners.size(); i++)
    {
      final ParticipantsChangedListenerRMI rmi = (ParticipantsChangedListenerRMI) _partsChangedListeners.elementAt(i);
      try
      {
        rmi.participantRemoved(index);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _partsChangedListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void restart(ScenarioType scenario)
  {
    for (int i = 0; i < _scenarioSteppedListeners.size(); i++)
    {
      final ScenarioSteppedListenerRMI rmi = (ScenarioSteppedListenerRMI) _scenarioSteppedListeners.elementAt(i);
      try
      {
        rmi.restart();
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _scenarioSteppedListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void step(ScenarioType scenario, final long newTime)
  {
    for (int i = 0; i < _scenarioSteppedListeners.size(); i++)
    {
      final ScenarioSteppedListenerRMI rmi = (ScenarioSteppedListenerRMI) _scenarioSteppedListeners.elementAt(i);
      try
      {
        rmi.step(newTime);
      }
      catch (java.rmi.ConnectException ce)
      {
        MWC.Utilities.Errors.Trace.trace("Listener died, deleting:" + rmi);
        _scenarioSteppedListeners.remove(rmi);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _scenarioSteppedListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }


}
