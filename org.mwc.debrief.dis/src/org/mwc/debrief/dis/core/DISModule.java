package org.mwc.debrief.dis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mwc.debrief.dis.listeners.IDISCollisionListener;
import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFireListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.OneByteChunk;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.dis.VariableDatum;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule, IDISGeneralPDUListener
{
  private List<IDISFixListener> _fixListeners =
      new ArrayList<IDISFixListener>();
  private Map<Integer, List<IDISEventListener>> _eventListeners =
      new HashMap<Integer, List<IDISEventListener>>();
  private List<IDISDetonationListener> _detonationListeners =
      new ArrayList<IDISDetonationListener>();
  private List<IDISGeneralPDUListener> _generalListeners =
      new ArrayList<IDISGeneralPDUListener>();
  private List<IDISScenarioListener> _scenarioListeners =
      new ArrayList<IDISScenarioListener>();
  private List<IDISStopListener> _stopListeners =
      new ArrayList<IDISStopListener>();
  private List<IDISCollisionListener> _collisionListeners =
      new ArrayList<IDISCollisionListener>();
  private boolean _newStart = false;
  private List<IDISFireListener> _fireListeners =
      new ArrayList<IDISFireListener>();

  private Map<Integer, String> _entityNames = new HashMap<Integer, String>();
  final private IDISEventListener _nameListener;
  
  public DISModule()
  {
    // SPECIAL PROCESSING - declare ourselves as the first event listener
    // so we can intercept launch events (and retrieve the name)
    _nameListener = new IDISEventListener()
    {
      
      @Override
      public void add(long time, short exerciseId, long senderId, String hisName,
          int eventType, String message)
      {
        if (eventType == IDISEventListener.EVENT_LAUNCH)
        {
          if (_entityNames.get(senderId) == null)
          {
            // ok, extract the message
            String name = extractNameFor(message);
            _entityNames.put((int) senderId, name);
          }
        }
      }
    };
  }

  protected String extractNameFor(String message)
  {
    String res = null;
    String[] split = message.trim().split(":");
    if(split.length == 2)
      res = split[1];
    return res;
  }

  @Override
  public void addFixListener(IDISFixListener handler)
  {
    _fixListeners.add(handler);
  }

  @Override
  public void addCollisionListener(IDISCollisionListener handler)
  {
    _collisionListeners.add(handler);
  }

  @Override
  public void addEventListener(IDISEventListener handler)
  {
    addEventListener(handler, null);
  }

  @Override
  public void addEventListener(IDISEventListener handler, Integer eType)
  {
    List<IDISEventListener> matches = _eventListeners.get(null);
    if (matches == null)
    {
      matches = new ArrayList<IDISEventListener>();
      _eventListeners.put(null, matches);
    }
    matches.add(handler);
  }

  @Override
  public void addFireListener(IDISFireListener handler)
  {
    _fireListeners.add(handler);
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
    final short force = pdu.getForceId();
    final long hisId = pdu.getEntityID().getEntity();
    long time = convertTime(pdu.getTimestamp());
    Vector3Double loc = pdu.getEntityLocation();
    double[] locArr = new double[]
    {loc.getX(), loc.getY(), loc.getZ()};
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
    Orientation orientation = pdu.getEntityOrientation();
    Vector3Float velocity = pdu.getEntityLinearVelocity();

    double speedMs =
        Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY()
            * velocity.getY());

    // entity state
    String hisName = _entityNames.get((int)hisId);
    
    Iterator<IDISFixListener> fIter = _fixListeners.iterator();
    while (fIter.hasNext())
    {
      IDISFixListener thisF = (IDISFixListener) fIter.next();
      thisF.add(time, eid, hisId, hisName, force, worldCoords[0],
          worldCoords[1], worldCoords[2], orientation.getPhi(), speedMs, pdu
              .getEntityAppearance_damage());
    }
  }

  private void handleEvent(EventReportPdu pdu)
  {
    short eid = pdu.getExerciseID();
    long time = convertTime(pdu.getTimestamp());
    int originator = pdu.getOriginatingEntityID().getEntity();
    int eType = (int) pdu.getEventType();
    String msg = "Empty";

    // try to get the data

    List<VariableDatum> items = pdu.getVariableDatums();
    if (items.size() > 0)
    {
      VariableDatum val = items.get(0);
      List<OneByteChunk> chunks = val.getVariableData();
      byte[] bytes = new byte[chunks.size()];
      Iterator<OneByteChunk> iter = chunks.iterator();
      int ctr = 0;
      while (iter.hasNext())
      {
        OneByteChunk thisB = (OneByteChunk) iter.next();
        bytes[ctr++] = thisB.getOtherParameters()[0];
      }
      msg = new String(bytes);
    }

    // sort out his name
    _nameListener.add(time, eid, originator, null, eType, msg);

    // now try to retrieve name
    String hisName = _entityNames.get(originator);
    
    // first send out to specific listeners
    List<IDISEventListener> specificListeners = _eventListeners.get(eType);
    if (specificListeners != null)
    {
      Iterator<IDISEventListener> eIter = specificListeners.iterator();
      while (eIter.hasNext())
      {
        IDISEventListener thisE = (IDISEventListener) eIter.next();
        thisE.add(time, eid, originator, hisName, eType, msg);
      }
    }

    // and now to general listeners
    List<IDISEventListener> generalListeners = _eventListeners.get(null);
    if (generalListeners != null)
    {
      Iterator<IDISEventListener> eIter = generalListeners.iterator();
      while (eIter.hasNext())
      {
        IDISEventListener thisE = (IDISEventListener) eIter.next();
        thisE.add(time, eid, originator, hisName, eType, msg);
      }
    }


  }

  private void handleDetonation(DetonationPdu pdu)
  {
    short eid = pdu.getExerciseID();
    Vector3Float eLoc = pdu.getLocationInEntityCoordinates();
    @SuppressWarnings("unused")
    Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
    double[] locArr = new double[]
    {eLoc.getX(), eLoc.getY(), eLoc.getZ()};
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
    long time = pdu.getTimestamp();
    int hisId = pdu.getFiringEntityID().getEntity();
    
    // sort out his name
    String hisName = _entityNames.get(hisId);

    Iterator<IDISDetonationListener> dIter = _detonationListeners.iterator();
    while (dIter.hasNext())
    {
      IDISDetonationListener thisD = (IDISDetonationListener) dIter.next();
      thisD.add(time, eid, hisId, hisName, worldCoords[0],
          worldCoords[1], worldCoords[2]);
    }

  }

  @Override
  public void addGeneralPDUListener(IDISGeneralPDUListener listener)
  {
    _generalListeners.add(listener);
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

    // and now the specific listeners
    final short type = data.getPduType();
    switch (type)
    {
    case 1:
    {
      handleFix((EntityStatePdu) data);
      break;
    }
    case 2:
    {
      handleFire((FirePdu) data);
      break;
    }
    case 3:
    {
      handleDetonation((DetonationPdu) data);
      break;
    }
    case 4:
    {
      handleCollision((CollisionPdu) data);
      break;
    }
    case 21:
    {
      handleEvent((EventReportPdu) data);
      break;
    }
    case 14:
    {
      handleStop((StopFreezePdu) data);
      break;
    }
    default:
      System.err.println("PDU type not handled:" + type);
    }
  }

  private void handleCollision(CollisionPdu pdu)
  {
    short eid = pdu.getExerciseID();
    Vector3Float eLoc = pdu.getLocation();
    double[] locArr = new double[]
    {eLoc.getX(), eLoc.getY(), eLoc.getZ()};
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);
    long time = convertTime(pdu.getTimestamp());
    int receipientId = pdu.getIssuingEntityID().getEntity();
    int movingId = pdu.getCollidingEntityID().getEntity();

    // sort out his name
    String hisName = _entityNames.get(movingId);

    Iterator<IDISCollisionListener> dIter = _collisionListeners.iterator();
    while (dIter.hasNext())
    {
      IDISCollisionListener thisD = dIter.next();
      thisD.add(time, eid, movingId, hisName, receipientId,
          worldCoords[0], worldCoords[1], worldCoords[2]);
    }
  }

  private void handleFire(FirePdu pdu)
  {
    short eid = pdu.getExerciseID();
    Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
    long time = convertTime(pdu.getTimestamp());
    int hisId = pdu.getFiringEntityID().getEntity();
    int tgtId = pdu.getTargetEntityID().getEntity();
    
    // sort out his name
    String hisName = _entityNames.get(hisId);
    String tgtName = _entityNames.get(tgtId);

    Iterator<IDISFireListener> dIter = _fireListeners.iterator();
    while (dIter.hasNext())
    {
      IDISFireListener thisD = dIter.next();
      thisD.add(time, eid, hisId, hisName, tgtId, tgtName, wLoc.getY(), wLoc.getX(), wLoc.getZ());
    }
  }

  private void handleStop(StopFreezePdu pdu)
  {
    long time = convertTime(pdu.getTimestamp());
    short eid = pdu.getExerciseID();
    short reason = pdu.getReason();

    Iterator<IDISStopListener> dIter = _stopListeners.iterator();
    while (dIter.hasNext())
    {
      IDISStopListener thisD = dIter.next();
      thisD.stop(time, eid, reason);
    }

    // share the complete message
    complete("Scenario complete");
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
    
    // also wipe our locally cached data (entity names)
    _entityNames.clear();
  }

  @Override
  public void addStopListener(IDISStopListener idisStopListener)
  {
    _stopListeners.add(idisStopListener);
  }

  /**
   * encapsulate timestamp conversions
   * 
   * @param timeStamp
   * @return
   */
  public long convertTime(long timeStamp)
  {
    return timeStamp * 60000;
  }

}
