/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 10:01:30
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Client;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.Util.RMI.ParticipantRMI;
import ASSET.Util.RMI.ScenarioCreatedListenerRMI;
import ASSET.Util.RMI.ScenarioRMI;
import ASSET.Util.RMI.ServerRMI;
import ASSET.Util.RMI.Server.ServerImpl;

public class ObserverBaseImpl extends UnicastRemoteObject implements
        Remote,
        ScenarioCreatedListenerRMI,
        ASSET.Util.RMI.ScenarioSteppedListenerRMI,
        ASSET.Util.RMI.ParticipantsChangedListenerRMI,
        ASSET.Util.RMI.PropertyChangeListenerRMI
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*****************************************************************
   * member variables
   ****************************************************************/

  /** the server we are currently connected to
   *
   */
  ServerRMI _myServer = null;

  /** the current scenario we are looking at
   *
   */
  private ScenarioRMI _currentScenario = null;

  /** the scenarios held within the current server
   *
   */
  final Vector<StubWrapper> _theScenarios = new Vector<StubWrapper>(0,1);

  /** the participant we are currently looking at
   *
   */
  ParticipantRMI _currentParticipant = null;

  /** the current participant we are inspecting
   *
   */
  final Vector<StubWrapper> _theParticipants = new Vector<StubWrapper>(0,1);



  /*****************************************************************
   * constructor
   ****************************************************************/

  ObserverBaseImpl() throws RemoteException
  {
    super();
  }


  public void scenarioCreated(final int index) throws RemoteException
  {
    final ScenarioRMI thisScen = _myServer.getThisScenario(index);
    _theScenarios.add(new StubWrapper(thisScen.getName(),thisScen));
    thisScen.addPropertyChangeListener(this);
  }

  public void scenarioDestroyed(final int index) throws RemoteException
  {
    // pass through the participants to find this one
    final ScenarioRMI oldScen = _myServer.getThisScenario(index);
    for (int i = 0; i < _theScenarios.size(); i++)
    {
      final StubWrapper wrapper = (StubWrapper) _theScenarios.elementAt(i);
      if(wrapper.value == oldScen)
      {
        _theScenarios.remove(wrapper);
      }
    }

    // and clear the prop change listener
    oldScen.removePropertyChangeListener(this);
  }

  public void step(long newTime) throws RemoteException
  {
    // update the plot, etc
  }

  public void restart() throws RemoteException
  {
  }

  public void newParticipant(final int index) throws RemoteException
  {
    final ParticipantRMI newPart = _currentScenario.getThisParticipant(index);
    _theParticipants.add(new StubWrapper(newPart.getName(), newPart));
  }

  public void participantRemoved(final int index) throws RemoteException
  {
    // pass through the participants to find this one
    final ParticipantRMI oldPart = _currentScenario.getThisParticipant(index);
    for (int i = 0; i < _theParticipants.size(); i++)
    {
      final StubWrapper wrapper = (StubWrapper) _theParticipants.elementAt(i);
      if(wrapper.value == oldPart)
        _theParticipants.remove(wrapper);
    }
  }

  /*****************************************************************
   * GUI support methods
   ****************************************************************/

  /** get the list of scenarios we can find
   *
   */
  Remote[] getServers()
  {
    Remote[] res = null;
//    try {

      final java.util.Properties newProps = new java.util.Properties();
      newProps.setProperty("jip.rmi.multicast.address", "230.0.0.1");
      newProps.setProperty("jip.rmi.multicast.port", "4446");
      newProps.setProperty("jip.rmi.unicast.port", "5000");
      newProps.setProperty("jip.rmi.unicast.portRange", "10");
      newProps.setProperty("jip.rmi.protocol.header", "RMI_DISCO");
      newProps.setProperty("jip.rmi.protocol.delim", "~");
      newProps.setProperty("jip.rmi.registry.urlPrefix", "jip.rmi.");

//      jip.rmi.Discovery.setProperties("G:\\Asset\\dev2\\rmi_desc\\example\\client\\rmidisco.properties");
      ASSET.Util.jip.rmi.Discovery.setProperties(newProps);
//    } catch (IOException e) {
//      System.out.println("failed to read in RMI properties");
//    }

    //Use RMIDiscovery to locate the service

    try {
      System.out.println("Attempting RMI discovery....");

      res = ASSET.Util.jip.rmi.RMIDiscovery.lookupAll(ServerRMI.class,ServerImpl.LOOKUP_NAME);

      System.out.println("number matching services found:" + res.length);
    } catch (java.rmi.ConnectException e) {
      System.out.println("Failed to find list of RMI servers" + e.getMessage());
    }

    return res;
//    _myServer = (ServerRMI)remote;
  }

  /** connect to the named server
   *
   */
  void connect(String server_name)
  {
    // do a disconnect first
    disconnect();

    // was a name supplied?
    if(server_name == null)
      server_name = "localhost";

    try
    {
      System.out.println("about to connect!");


      // get the server
      _myServer = (ServerRMI)Naming.lookup("rmi://" + server_name + "/" + ServerRMI.LOOKUP_NAME);

      // listen to the scenarios within the server
      final Integer[] indices = _myServer.getListOfScenarios();

      // get the list of scenarios currently stored in the server
      for (int i = 0; i < indices.length; i++)
      {
        final int index = indices[i].intValue();
        scenarioCreated(index);
      }

      _myServer.addScenarioCreatedListener(this);

      System.out.println("connected!");
    }
    catch (NotBoundException e)
    {
      e.printStackTrace();
    }
    catch (java.net.MalformedURLException e)
    {
      e.printStackTrace();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }

    // and update the gui
    updateScenarioGUI();
  }



  /** user has disconnected
   *
   */
  void disconnect()
  {
    if(_myServer != null)
    {
      try
      {
        // first drop the scenario
        if(_currentScenario != null)
        {
          _currentScenario.removeParticipantsChangedListener(this);
          _currentScenario.removeScenarioSteppedListener(this);
          _currentScenario = null;
          _currentParticipant = null;
        }

        _myServer.removeScenarioCreatedListener(this);

        for (int i = 0; i < _theScenarios.size(); i++)
        {
          final StubWrapper wrapper = (StubWrapper) _theScenarios.elementAt(i);
          final ScenarioRMI rmi = (ScenarioRMI)wrapper.value;
          // listen to property changes for this scenario
          System.out.println("trying to remove scenario named:" + rmi.getName());
          rmi.removePropertyChangeListener(this);
        }

        _theScenarios.removeAllElements();
        _theParticipants.removeAllElements();
        _myServer = null;
      }
      catch (RemoteException e)
      {
        System.out.println("FAILED TO DISCONNECT:" + e.getMessage());
      }
    }
  }


  /** the user has selected a scenario
   *
   */
  void scenarioSelected(final ScenarioRMI newScen)
  {
    // are we currently listening to a scenario?
    if(_currentScenario != null)
    {
      // stop listening to scenario events
      try
      {
        _currentScenario.removeParticipantsChangedListener(this);
        _currentScenario.removeScenarioSteppedListener(this);
        _theParticipants.removeAllElements();
      }
      catch (RemoteException e)
      {
        e.printStackTrace();
      }
      _currentScenario = null;
    }

    // just check whether we have a valid scenario
    if(newScen == null)
      return;

    // set this scenario
    _currentScenario = newScen;

    try
    {
      System.out.println("name of new scen is:" + newScen.getName());
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }

    // set the participants
    try
    {
      final Integer[] indices = _currentScenario.getListOfParticipants();
      for (int i = 0; i < indices.length; i++)
      {
        final int thisI = indices[i].intValue();
        final ParticipantRMI newP = _currentScenario.getThisParticipant(thisI);
        _theParticipants.add(new StubWrapper(newP.getName(), newP));
      }

      // and listen out for any new participants being added
      _currentScenario.addParticipantsChangedListener(this);
      _currentScenario.addScenarioSteppedListener(this);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }

    // and update the GUI
    updateParticipantGUI();


  }

  /** the user has selected a participant
   *
   */
  void participantSelected(final ParticipantRMI newPart)
  {
    // set the participant
    _currentParticipant = newPart;
  }

  public void propertyChange(final String property_name, final Object old_val, final Object new_val) throws RemoteException
  {
    if(property_name.equals(ScenarioType.NAME))
    {
      final String new_name = (String)new_val;
      final String old_name = (String)old_val;

      for (int i = 0; i < _theScenarios.size(); i++)
      {
        final StubWrapper wrapper = (StubWrapper) _theScenarios.elementAt(i);
        if(wrapper.name.equals(old_name))
        {
          wrapper.name = new String(new_name);
          updateScenarioGUI();
          break;
        }
      }
    }
    System.out.println(property_name + " change received to:" + new_val);
  }

  void updateParticipantGUI()
  {

  }

  void updateScenarioGUI()
  {
    // don't bother, it's a gui thing
  }

  /*****************************************************************
   * embedded class to wrap a stub object
   ****************************************************************/
  static class StubWrapper
  {
    public String name = null;
    public Object value = null;
    public StubWrapper(final String nameVal, final Object valueVal)
    {
      name = nameVal;
      value = valueVal;
    }
    public String toString()
    {
      return name;
    }
  }


}
