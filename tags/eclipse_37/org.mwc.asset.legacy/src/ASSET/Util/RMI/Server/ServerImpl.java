/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 10:05:02
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Vector;

import ASSET.ScenarioType;
import ASSET.ServerType;
import ASSET.Server.ScenarioCreatedListener;
import ASSET.Util.RMI.ScenarioCreatedListenerRMI;
import ASSET.Util.RMI.ScenarioRMI;
import ASSET.Util.RMI.ServerRMI;

public class ServerImpl extends UnicastRemoteObject implements ServerRMI, ScenarioCreatedListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*****************************************************************
   * member variables
   ****************************************************************/

  private Vector<ScenarioCreatedListenerRMI> _myListeners;
  private ServerType _myServer;

  /** keep an internal cache of wrappers
   *
   */
  private HashMap<ScenarioType, ScenarioRMIImpl> _scenarioWrappersCache = new HashMap<ScenarioType, ScenarioRMIImpl>();


  /*****************************************************************
   * constructor
   ****************************************************************/

  public ServerImpl(final ServerType myServer) throws RemoteException
  {
    super();
    _myListeners = new Vector<ScenarioCreatedListenerRMI>(0,1);
    _myServer = myServer;

    // finally listen to the server
    _myServer.addScenarioCreatedListener(this);

    // and do the connection
    try
    {
      System.setSecurityManager(new RMISecurityManager()
       {
          public void checkPropertiesAccess(){}
          public void checkPropertyAccess(String key){}
       }
      );


      // try to create the registry
      java.rmi.registry.LocateRegistry.createRegistry(java.rmi.registry.Registry.REGISTRY_PORT);

      final String hostname = InetAddress.getLocalHost().getHostName();
      System.out.println("about to RMI entry on " + hostname);

      //create a jini like lookup service for the RMI service
      //with the service instance and its name

      System.out.println("SWITCH TO HARDCODED RMI DISCOVERY PROPERTIES");

      final java.util.Properties newProps = new java.util.Properties();
      newProps.setProperty("jip.rmi.multicast.address", "230.0.0.1");
      newProps.setProperty("jip.rmi.multicast.port", "4446");
      newProps.setProperty("jip.rmi.unicast.port", "5000");
      newProps.setProperty("jip.rmi.unicast.portRange", "10");
      newProps.setProperty("jip.rmi.protocol.header", "RMI_DISCO");
      newProps.setProperty("jip.rmi.protocol.delim", "~");
      newProps.setProperty("jip.rmi.registry.urlPrefix", "jip.rmi.");


      ASSET.Util.jip.rmi.Discovery.setProperties(newProps);
//      jip.rmi.Discovery.setProperties("G:\\Asset\\dev2\\rmi_desc\\example\\client\\rmidisco.properties");
      ASSET.Util.jip.rmi.RMILookup.bind(this ,ServerRMI.LOOKUP_NAME);

      Naming.bind(ServerImpl.LOOKUP_NAME, this);

      System.out.println("Hey, connected!");

      } catch (java.rmi.AlreadyBoundException e) {
        MWC.Utilities.Errors.Trace.trace("Server name already in use, cancelling.");
      } catch (java.rmi.server.ExportException e) {
        MWC.Utilities.Errors.Trace.trace("Unable to create registry, must be running already.");
      } catch (java.rmi.ConnectException e) {
          MWC.Utilities.Errors.Trace.trace("You must run rmiregistry before "
                 + "starting the ASSET server.");
      } catch (java.net.UnknownHostException e) {
          MWC.Utilities.Errors.Trace.trace("Can't get current host.");
//      } catch (java.net.MalformedURLException e) {
//          MWC.Utilities.Errors.Trace.trace("Can't bind the registrar.");
      } catch (Exception e) {
          MWC.Utilities.Errors.Trace.trace(e, "Unknown exception on server startup");
      }

  }


  /*****************************************************************
   * member variables
   ****************************************************************/

  public String getHostname() throws RemoteException {
    String res = "UNKNOWN";
    try {
      res = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      MWC.Utilities.Errors.Trace.trace(e, "Failed to find hostname");
    }
    return res;
  }



  public void createScenario(final String type)
  {
    _myServer.createNewScenario(type);
  }


  public ScenarioRMI getThisScenario(final int id) throws RemoteException
  {
    final ScenarioType scenario = _myServer.getThisScenario(id);


    // have we got it cached already?
    ScenarioRMIImpl wrapper =  (ScenarioRMIImpl)_scenarioWrappersCache.get(scenario);
    if(wrapper == null)
    {
      wrapper = new ScenarioRMIImpl(scenario);
      _scenarioWrappersCache.put(scenario, wrapper);
    }

    return wrapper;
  }

  public Integer[] getListOfScenarios() throws RemoteException
  {
    return _myServer.getListOfScenarios();
  }

  public void addScenarioCreatedListener(final ScenarioCreatedListenerRMI listener) throws RemoteException
  {
    _myListeners.add(listener);
  }

  public void removeScenarioCreatedListener(final ScenarioCreatedListenerRMI listener) throws RemoteException
  {
    _myListeners.remove(listener);
  }

  public void destroyScenario(final int id)
  {
    for (int i = 0; i < _myListeners.size(); i++)
    {
      final ScenarioCreatedListenerRMI rmi = (ScenarioCreatedListenerRMI) _myListeners.elementAt(i);
      try
      {
        rmi.scenarioDestroyed(id);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _myListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void scenarioCreated(final int index)
  {
    for (int i = 0; i < _myListeners.size(); i++)
    {
      final ScenarioCreatedListenerRMI rmi = (ScenarioCreatedListenerRMI) _myListeners.elementAt(i);
      try
      {
        rmi.scenarioCreated(index);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _myListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }

  public void scenarioDestroyed(final int index)
  {
    for (int i = 0; i < _myListeners.size(); i++)
    {
      final ScenarioCreatedListenerRMI rmi = (ScenarioCreatedListenerRMI) _myListeners.elementAt(i);
      try
      {
        rmi.scenarioDestroyed(index);
      }
      catch (java.rmi.ConnectIOException e)
      {
        MWC.Utilities.Errors.Trace.trace("Listener dissappeared, deleting:" + rmi);
        _myListeners.remove(rmi);
      }
      catch (RemoteException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Unknown remote exception");
      }
    }
  }


}
