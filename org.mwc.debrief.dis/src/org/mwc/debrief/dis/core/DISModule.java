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
import org.mwc.debrief.dis.listeners.IDISStartResumeListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EntityType;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.OneByteChunk;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.dis.VariableDatum;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

public class DISModule implements IDISModule, IDISGeneralPDUListener
{
  private static final int ESTIMATED_KIND = 1;
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
  private List<IDISStartResumeListener> _startResumeListeners =
      new ArrayList<IDISStartResumeListener>();
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
      public void add(long time, short exerciseId, long senderId,
          String hisName, int eventType, String message)
      {
        if (eventType == IDISEventListener.EVENT_LAUNCH)
        {
          if (_entityNames.get(senderId) == null)
          {
            // ok, extract the message
            String name = extractNameFor(message);

            // did we manage it?
            if (name != null)
            {
              _entityNames.put((int) senderId, name);
            }
          }
        }
      }
    };
  }

  protected String extractNameFor(String message)
  {
    // Entity 1 called SubmarineSouth has been created or launched.
    final String called = "called ";
    final String has = "has been";

    String res = null;

    if (message.contains(called) && message.contains(has))
    {
      int nameStart = message.indexOf(called) + called.length();
      int nameEnd = message.indexOf(has) - 1;
      res = message.substring(nameStart, nameEnd);
    }
    return res;
  }

  /**
   * retrieve the name for this entity id, or generate a default one
   * 
   * @param id
   *          the id we're looking against
   * @return its name (or a generated one)
   */
  private String nameFor(long id)
  {
    String name = _entityNames.get((Integer) (int) id);
    if (name == null)
    {
      name = "DIS_" + id;
    }

    return name;
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
  public void addStartResumeListener(IDISStartResumeListener handler)
  {
    _startResumeListeners.add(handler);
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

    boolean isEstimated = pdu.getEntityType().getEntityKind() == ESTIMATED_KIND;

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
    String hisName = nameFor(hisId);

    EntityType cat = pdu.getEntityType();
    short kind = cat.getEntityKind();
    short domain = cat.getDomain();
    short category = cat.getCategory();

    Iterator<IDISFixListener> fIter = _fixListeners.iterator();
    while (fIter.hasNext())
    {
      IDISFixListener thisF = (IDISFixListener) fIter.next();
      thisF.add(time, eid, hisId, hisName, force, kind, domain, category,
          isEstimated, worldCoords[0], worldCoords[1], -worldCoords[2],
          orientation.getPhi(), speedMs, pdu.getEntityAppearance_damage());
    }
  }

  private void handleEvent(EventReportPdu pdu)
  {
    short eid = pdu.getExerciseID();
    long time = convertTime(pdu.getTimestamp());
    int originator = pdu.getOriginatingEntityID().getEntity();
    int eType = (int) pdu.getEventType();
    String msg = "";

    // try to get the data
    List<VariableDatum> items = pdu.getVariableDatums();
    for (int i = 0; i < items.size(); i++)
    {
      VariableDatum val = items.get(i);
      List<OneByteChunk> chunks = val.getVariableData();
      final int thisLen = (int) val.getVariableDatumLength();
      byte[] bytes = new byte[thisLen];
      Iterator<OneByteChunk> iter = chunks.iterator();
      int ctr = 0;
      for (int l = 0; l < thisLen; l++)
      {
        OneByteChunk thisB = (OneByteChunk) iter.next();
        final byte thisByte = thisB.getOtherParameters()[0];

        // if (eType == 10004)
        // {
        // System.out.println("byte:" + thisByte + " str:" + new String(new byte[]{thisByte}));
        // }

        if (thisByte > 10)
        {
          bytes[ctr++] = thisByte;
        }
        else if (thisByte == 1 || thisByte == 9)
        {
          bytes[ctr++] = 32;
        }
      }
      String newS = new String(bytes);
      msg += newS;
    }

    if (msg.length() == 0)
    {
      msg = "Unset";
    }

    // sort out his name
    _nameListener.add(time, eid, originator, null, eType, msg);

    // now try to retrieve name
    String hisName = nameFor(originator);

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

    // we get two sets of coordinates in a detonation. Track both sets
    Vector3Float eLoc = pdu.getLocationInEntityCoordinates();
    double[] locArr = new double[]
    {eLoc.getX(), eLoc.getY(), eLoc.getZ()};
    @SuppressWarnings("unused")
    double[] eWorldCoords = CoordinateConversions.xyzToLatLonDegrees(locArr);

    Vector3Double wLoc = pdu.getLocationInWorldCoordinates();
    double[] worldArr = new double[]
    {wLoc.getX(), wLoc.getY(), wLoc.getZ()};
    double[] worldCoords = CoordinateConversions.xyzToLatLonDegrees(worldArr);

    long time = pdu.getTimestamp();
    int hisId = pdu.getFiringEntityID().getEntity();

    double[] coordsToUse = worldCoords;

    // sort out his name
    String hisName = nameFor(hisId);

    Iterator<IDISDetonationListener> dIter = _detonationListeners.iterator();
    while (dIter.hasNext())
    {
      IDISDetonationListener thisD = (IDISDetonationListener) dIter.next();
      thisD.add(time, eid, hisId, hisName, coordsToUse[0], coordsToUse[1],
          -coordsToUse[2]);
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

    // whether to track all messages, to learn about what is being sent
    // if(data.getPduType() == 1)
    // {
    // EntityStatePdu esp = (EntityStatePdu) data;
    // System.out.println(new java.util.Date(convertTime(esp.getTimestamp())));
    // }

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
    case 13:
    {
      handleStart((StartResumePdu) data);
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
    long time = convertTime(pdu.getTimestamp());
    int receipientId = pdu.getIssuingEntityID().getEntity();
    int movingId = pdu.getCollidingEntityID().getEntity();

    // sort out his name
    String movingName = nameFor(movingId);
    String recipientName = nameFor(receipientId);

    Iterator<IDISCollisionListener> dIter = _collisionListeners.iterator();
    while (dIter.hasNext())
    {
      IDISCollisionListener thisD = dIter.next();
      thisD.add(time, eid, movingId, movingName, receipientId, recipientName);
    }
  }

  private void handleStart(StartResumePdu pdu)
  {
    short eid = pdu.getExerciseID();
    long time = convertTime(pdu.getTimestamp());
    long replicationId = pdu.getRequestID();

    Iterator<IDISStartResumeListener> dIter = _startResumeListeners.iterator();
    while (dIter.hasNext())
    {
      IDISStartResumeListener thisD = dIter.next();
      thisD.add(time, eid, replicationId);
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
    String hisName = nameFor(hisId);
    String tgtName = nameFor(tgtId);

    Iterator<IDISFireListener> dIter = _fireListeners.iterator();
    while (dIter.hasNext())
    {
      IDISFireListener thisD = dIter.next();
      thisD.add(time, eid, hisId, hisName, tgtId, tgtName, wLoc.getY(), wLoc
          .getX(), wLoc.getZ());
    }
  }

  private void handleStop(StopFreezePdu pdu)
  {
    long time = convertTime(pdu.getTimestamp());
    short eid = pdu.getExerciseID();
    short reason = pdu.getReason();
    int appId = pdu.getOriginatingEntityID().getApplication();

    Iterator<IDISStopListener> dIter = _stopListeners.iterator();
    while (dIter.hasNext())
    {
      IDISStopListener thisD = dIter.next();
      thisD.stop(time, appId, eid, reason);
    }

    // share the complete message
    if (reason == IDISStopListener.PDU_STOP)
    {
      complete("Scenario complete");
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
    return convertThisTime(timeStamp);
  }

  public static long convertThisTime(long timeStamp)
  {
    return timeStamp;
  }
}
