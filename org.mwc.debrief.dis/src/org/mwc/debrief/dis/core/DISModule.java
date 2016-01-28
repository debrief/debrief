package org.mwc.debrief.dis.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule, IDISGeneralPDUListener
{
  final private IDISPreferences _disPrefs;
  private List<IDISFixListener> _fixListeners = new ArrayList<IDISFixListener>();
  private List<IDISEventListener> _eventListeners = new ArrayList<IDISEventListener>();
  private List<IDISDetonationListener> _detonationListeners = new ArrayList<IDISDetonationListener>();
  private List<IDISGeneralPDUListener> _generalListeners = new ArrayList<IDISGeneralPDUListener>();
  private List<IDISScenarioListener> _scenarioListeners = new ArrayList<IDISScenarioListener>();
  private boolean _newStart = false;

  public DISModule(final IDISPreferences disPrefs)
  {
    _disPrefs = disPrefs;
  }

  @Override
  public void addFixListener(IDISFixListener handler)
  {
    _fixListeners.add(handler);
  }
  
  @Override
  public void addEventListener(IDISEventListener handler)
  {
    _eventListeners.add(handler);
  }
  

  @Override
  public void addDetonationListener(IDISDetonationListener handler)
  {
    _detonationListeners.add(handler);
  }

  @Override
  public void addScenarioListener(IDISScenarioListener handler)
  {
    _scenarioListeners.add(handler);
  }

  @Override
  public void setProvider(IPDUProvider provider)
  {
    // remember we're restarting
    _newStart = true;

    // register as a listener, to hear about new data
    provider.addListener(this);
  }

  private void handleFix(EntityStatePdu pdu)
  {
    // unpack the data
    final short eid = pdu.getExerciseID();
    final long hisId = pdu.getEntityID().getEntity();
    final long time = pdu.getTimestamp();
    Vector3Double loc = pdu.getEntityLocation();
    double[] locArr = new double[]
    { loc.getX(), loc.getY(), loc.getZ() };
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
    Orientation orientation = pdu.getEntityOrientation();
    Vector3Float velocity = pdu.getEntityLinearVelocity();

    // entity state
    Iterator<IDISFixListener> fIter = _fixListeners.iterator();
    while (fIter.hasNext())
    {
      IDISFixListener thisF = (IDISFixListener) fIter.next();
      thisF.add(time, eid, hisId, worldCoords[0], worldCoords[1],
          worldCoords[2], orientation.getPhi(), velocity.getX());
    }
  }

  private void handleEvent(EventReportPdu pdu)
  {
    short eid = pdu.getExerciseID();
    long time = pdu.getTimestamp();
    int originator = pdu.getOriginatingEntityID().getEntity();
    
    Iterator<IDISEventListener> eIter = _eventListeners.iterator();
    while (eIter.hasNext())
    {
      IDISEventListener thisE = (IDISEventListener) eIter.next();
      thisE.add(time, eid, originator);
    }
        
  }
  
  private void handleDetonation(DetonationPdu pdu)
  {
    short eid = pdu.getExerciseID();
    Vector3Float eLoc = pdu.getLocationInEntityCoordinates();
    Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
    double[] locArr = new double[]
    { eLoc.getX(), eLoc.getY(), eLoc.getZ() };
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
    long time = pdu.getTimestamp();
    int hisId = pdu.getFiringEntityID().getEntity();
    
    Iterator<IDISDetonationListener> dIter = _detonationListeners.iterator();
    while (dIter.hasNext())
    {
      IDISDetonationListener thisD =
          (IDISDetonationListener) dIter.next();
      thisD.add(time, eid, hisId, worldCoords[0], worldCoords[1], worldCoords[2]);      
    }
    
  }

  @Override
  public void addGeneralPDUListener(IDISGeneralPDUListener listener)
  {
    _generalListeners.add(listener);
  }

  @Override
  public IDISPreferences getPrefs()
  {
    return _disPrefs;
  }

  @Override
  public void logPDU(Pdu data)
  {
    // is this new?
    if (_newStart)
    {
      // share the good news
      Iterator<IDISScenarioListener> sIter = _scenarioListeners.iterator();
      while (sIter.hasNext())
      {
        IDISScenarioListener sl = (IDISScenarioListener) sIter.next();
        sl.restart();
      }
      _newStart = false;
    }

    // give it to any general listenrs
    Iterator<IDISGeneralPDUListener> gIter = _generalListeners.iterator();
    while (gIter.hasNext())
    {
      IDISGeneralPDUListener gPdu = (IDISGeneralPDUListener) gIter.next();
      gPdu.logPDU(data);
    }

    // check the type
    final short type = data.getPduType();
    switch (type)
    {
    case 1:
    {
      handleFix((EntityStatePdu) data);
      break;
    }
    case 3:
    {
      handleDetonation((DetonationPdu) data);
      break;
    }
    case 21:
    {
      handleEvent((EventReportPdu) data);
      break;
    }
    default:
      System.err.println("PDU type not handled:" + type);
    }
  }

  

  @Override
  public void complete(String reason)
  {
    // tell any scenario listeners
    Iterator<IDISScenarioListener> sIter = _scenarioListeners.iterator();
    while (sIter.hasNext())
    {
      IDISScenarioListener thisS = (IDISScenarioListener) sIter.next();
      thisS.complete(reason);
    }

    // also tell any general liseners
    Iterator<IDISGeneralPDUListener> gIter = _generalListeners.iterator();
    while (gIter.hasNext())
    {
      IDISGeneralPDUListener git = (IDISGeneralPDUListener) gIter.next();
      git.complete(reason);

    }
  }

}
