package org.mwc.debrief.dis.diagnostics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import org.mwc.debrief.dis.diagnostics.senders.IPduSender;
import org.mwc.debrief.dis.diagnostics.senders.NetworkPduSender;
import org.mwc.debrief.dis.listeners.IDISEventListener;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EntityType;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.OneByteChunk;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.dis.VariableDatum;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;
import edu.nps.moves.disutil.CoordinateConversions;

/**
 * Creates and sends ESPDUs in IEEE binary format.
 * 
 * @author DMcG
 */
public class PduGenerator
{

  /**
   * random seed - necessary for reproducible results (for testing)
   * 
   */
  private static final int RANDOM_SEED = 12;

  private static final short STOP_PDU_TERMINATED = 2;

  private short exerciseId;

  private boolean _terminate;

  private Random genny;

  private Collection<Torpedo> torpedoes = new ArrayList<Torpedo>();
  private Collection<Vessel> redParts = new ArrayList<Vessel>();
  private Collection<Vessel> blueParts = new ArrayList<Vessel>();
  private Collection<Vessel> greenParts = new ArrayList<Vessel>();

  public static final short BLUE = 1;
  public static final short RED = 2;
  public static final short GREEN = 3;

  private class Torpedo extends Vessel
  {

    /**
     * if this participant has a target that it aims for
     * 
     */
    public int targetId = -1;

    final public int hostId;
    
    Vessel myTarget = null;

    private Torpedo(final int id, final int hostId, final short force,
        double oLat, double oLong, int targetId)
    {
      super(id, force, oLat, oLong, 0);

      this.targetId = targetId;
      this.hostId = hostId;

      // make it a bit quicker
      distStep *= 1.3;
    }

    @Override
    protected double getCourse(Map<Integer, Vessel> states, EntityID sampleId,
        long lastTime, IPduSender sender)
    {

      // hmm, do we have a target?
      if (targetId != -1)
      {
        // does this target exist
        Vessel[] parts = states.values().toArray(new Vessel[]
        {null});
        for (int i = 0; i < parts.length; i++)
        {
          final Vessel thisP = parts[i];

          // hey, don't look at ourselves...
          if (thisP.id == id)
          {
            // skip to the next loop
            continue;
          }

          // hey, don't look a launch platform
          if (thisP.id == hostId)
          {
            continue;
          }

          // hmm, see if we're very close to this one
          double dLon = thisP.longVal - longVal;
          double dLat = thisP.latVal - latVal;
          double range = Math.sqrt(dLon * dLon + dLat * dLat);

          if (range < 0.01)
          {
            if (thisP.id == targetId)
            {
              sendDetonation(this, thisP.id, sampleId, states, lastTime, sender);

              // ok, change the appearance of the torpedo to destroyed
              this.damage = DESTROYED;

              // and the target to disabled
              thisP.damage = MODERATE_DAMAGE;
            }
            else
            {
              sendCollision(this, thisP.id, sampleId, states, lastTime, sender);

              this.damage = SLIGHT_DAMAGE;
              thisP.damage = SLIGHT_DAMAGE;
            }
          }

          if (thisP.id == targetId)
          {
            double bearing = Math.atan2(dLon, dLat);
            courseRads = bearing;
            
            // hmm, do we already know about our target?
            if(myTarget == null)
            {
              // nope, share the good news
              final String msg = "New target found for:" + id;
              sendMessage(exerciseId, lastTime, IDISEventListener.EVENT_TACTICS_CHANGE, sampleId,
              msg, sender);
            }
            
            myTarget = thisP;
          }

        }

        // did we find it?
        if (myTarget != null)
        {
        }
        else
        {
          // target lost, forget about it
          final String theMsg =
              "target for:" + id + " was lost, was:" + targetId;
          sendMessage(exerciseId, lastTime, IDISEventListener.EVENT_TACTICS_CHANGE, sampleId,
              theMsg, sender);
          System.out.println(theMsg);
          targetId = -1;

          // stop the speed
          distStep = 0;

          // ok, self destruct
          this.damage = DESTROYED;

          sendDetonation(this, -1, sampleId, states, lastTime, sender);

        }

      }
      return courseRads;
    }

  }

  private class Vessel
  {
    final static int NO_DAMAGE = 0;
    final static int SLIGHT_DAMAGE = 1;
    final static int MODERATE_DAMAGE = 2;
    final static int DESTROYED = 3;

    final int id;
    protected double latVal;
    protected double longVal;
    protected double courseRads;

    private long previousTime = 0;

    double distStep = 0.01;

    /**
     * allow specification of the current appearance of this platform
     * 
     */
    protected int damage = NO_DAMAGE;
    final public short force;
    protected double speedVal;

    private Vessel(final int id, final short force, double oLat, double oLong,
        double range)
    {
      this.id = id;
      this.force = force;
      latVal = oLat + genny.nextDouble() * range;
      longVal = oLong + genny.nextDouble() * range;
      courseRads = Math.toRadians(((int) (genny.nextDouble() * 36d)) * 10d);
    }

    protected double getCourse(Map<Integer, Vessel> states, EntityID sampleId,
        long lastTime, IPduSender sender)
    {
      // see if we're going to do a random turn
      if (genny.nextDouble() > 0.8)
      {
        final double newCourse = ((int) (genny.nextDouble() * 36d)) * 10d;
        courseRads = Math.toRadians(newCourse);
      }

      return courseRads;
    }

    public void update(Map<Integer, Vessel> states, int idx, long lastTime,
        EntityID sampleId, IPduSender sender)
    {
      courseRads = getCourse(states, sampleId, lastTime, sender);

      // ok, handle the movement
      double dLat = Math.cos(courseRads) * distStep;
      double dLon = Math.sin(courseRads) * distStep;

      longVal += dLon;
      latVal += dLat;

      // remember the speed
      if (previousTime != 0)
      {
        speedVal = distStep / (lastTime - previousTime);
      }

      // remember the previous time
      previousTime = lastTime;
    }
  }

  public static void main(String args[])
  {

    String millis = null;
    String numParts = null;
    String numMessages = null;
    String destinationIpString = null;
    String portString = null;
    String networkModeString = null;

    // the input argument should be a file
    if (args.length == 1)
    {
      String fName = args[0];
      File iFile = new File(fName);
      String inputData = null;
      if (iFile.exists())
      {
        // ok, read it in
        Scanner scanner;
        try
        {
          scanner = new Scanner(iFile, "UTF-8");
          inputData = scanner.useDelimiter("\\A").next();
          scanner.close(); // Put this call in a finally block
        }
        catch (FileNotFoundException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      // did we extract any data?
      if (inputData != null)
      {
        // ok, parse it
        String[] items = inputData.trim().split(" ");
        if (items.length >= 6)
        {
          destinationIpString = items[0];
          portString = items[1];
          networkModeString = items[2];
          millis = items[3];
          numParts = items[4];
          numMessages = items[5];
        }
      }
    }
    else
    {
      // try to get them from system properties instead

      // get system properties, and put them into the args
      // All system properties, passed in on the command line via
      // -Dattribute=value
      Properties systemProperties = System.getProperties();

      // IP address we send to
      destinationIpString = systemProperties.getProperty("group");

      // Port we send to, and local port we open the socket on
      portString = systemProperties.getProperty("port");

      // Network mode: unicast, multicast, broadcast
      networkModeString = systemProperties.getProperty("mode");

      // IP address we send to
      millis = systemProperties.getProperty("millis");

      // Port we send to, and local port we open the socket on
      numParts = systemProperties.getProperty("participants");

      // Network mode: unicast, multicast, broadcast
      numMessages = systemProperties.getProperty("messages");
    }
    // insert the properties into the args
    if (millis != null)
    {
      args = new String[]
      {millis, numParts, numMessages};
    }

    PduGenerator sender = new PduGenerator();
    sender.run(new NetworkPduSender(destinationIpString, portString,
        networkModeString), args);
  }

  public void run(IPduSender sender, String args[])
  {
    // initialise our random number generator
    genny = new Random(RANDOM_SEED);

    /** an entity state pdu */
    EntityStatePdu espdu = new EntityStatePdu();
    // DisTime disTime = DisTime.getInstance();

    // declare the states
    final Map<Integer, Vessel> states = new HashMap<Integer, Vessel>();

    // sort out the runtime arguments
    long stepMillis = 500;
    long numParts = 1;
    int numMessages = 1000;

    // try to extract the step millis from the args
    if (args.length >= 1 && args[0].length() > 1)
    {
      stepMillis = Long.parseLong(args[0]);
    }

    if (args.length >= 2)
    {
      numParts = Long.parseLong(args[1]);
    }

    if (args.length >= 3)
    {
      numMessages = Integer.parseInt(args[2]);
    }

    // Initialize values in the Entity State PDU object. The exercise ID is
    // a way to differentiate between different virtual worlds on one network.
    // Note that some values (such as the PDU type and PDU family) are set
    // automatically when you create the ESPDU.

    exerciseId = (short) (Math.random() * 1000d);

    espdu.setExerciseID(exerciseId);

    // The EID is the unique identifier for objects in the world. This
    // EID should match up with the ID for the object specified in the
    // VMRL/x3d/virtual world.
    EntityID eid = espdu.getEntityID();
    eid.setSite(0);
    eid.setApplication(1);

    int entityId = 2;

    eid.setEntity(entityId);

    // Set the entity type. SISO has a big list of enumerations, so that by
    // specifying various numbers we can say this is an M1A2 American tank,
    // the USS Enterprise, and so on. We'll make this a tank. There is a
    // separate project elsehwhere in this project that implements DIS
    // enumerations in C++ and Java, but to keep things simple we just use
    // numbers here.
    EntityType entityType = espdu.getEntityType();
    entityType.setEntityKind((short) 1); // Platform (vs lifeform, munition,
                                         // sensor, etc.)
    entityType.setCountry(225); // USA
    entityType.setDomain((short) 1); // Land (vs air, surface, subsurface,
                                     // space)
    entityType.setCategory((short) 1); // Tank
    entityType.setSubcategory((short) 1); // M1 Abrams
    entityType.setSpec((short) 3); // M1A2 Abrams

    int randomHour = (int) (2 + genny.nextDouble() * 20);
    @SuppressWarnings("deprecation")
    long lastTime = new Date(2015, 1, 1, randomHour, 0).getTime();

    // Loop through sending 100 ESPDUs
    try
    {
      _terminate = false;

      System.out.println("Sending DIS messages for " + numMessages
          + " simulation cycles to " + sender.toString());

      final double startX = 50.1;
      final double startY = -1.87;
      final double randomArea = 0.06;

      // generate the inital states
      for (int i = 0; i < numParts; i++)
      {

        // sort out affiliation
        short force = (short) (genny.nextInt(3) + 1);

        int eId = i + 1;// 1 + (int) (Math.random() * 20d);
        Vessel newS = new Vessel(eId, force, startX, startY, randomArea);
        states.put(eId, newS);

        switch (force)
        {
        case BLUE:
          blueParts.add(newS);
          break;
        case RED:
          redParts.add(newS);
          break;
        case GREEN:
          greenParts.add(newS);
          break;
        }

      }

      // generate correct number of messages
      for (int idx = 0; idx < numMessages; idx++)
      {

        // just check if we're being terminated early
        if (_terminate)
        {
          break;
        }

        // increment time
        lastTime += 5 * 60 * 1000;

        espdu.setTimestamp(lastTime);

        // get an array of participants. we don't use an interator,
        // to avoid concurrent modification
        Vessel[] parts = states.values().toArray(new Vessel[]
        {null});
        for (int i = 0; i < parts.length; i++)
        {
          Vessel thisS = parts[i];

          // get the subject to move forward
          thisS.update(states, idx, lastTime, eid, sender);

          // and send out an update
          sendStatusUpdate(thisS, eid, espdu, sender);
        }

        // put in a random launch
        if (genny.nextDouble() >= 0.82 && torpedoes.size() < 2
            && blueParts.size() > 0)
        {
          System.out.println("===== LAUNCH ===== ");
          final Vessel launchPlatform = selectRandomEntity(redParts);

          // try to give the new vehicle a target
          Vessel targetId = selectRandomEntity(blueParts);

          final int newId = (int) (1000 + (genny.nextDouble() * 1000d));
          Torpedo torpedo =
              new Torpedo(newId, launchPlatform.id, RED, launchPlatform.latVal,
                  launchPlatform.longVal, targetId.id);

          // and remember it
          states.put(newId, torpedo);
          torpedoes.add(torpedo);

          // also send out the "fired" message
          FirePdu fire = new FirePdu();
          fire.setExerciseID(espdu.getExerciseID());
          fire.setTimestamp(lastTime);
          eid.setEntity(newId);
          fire.setFiringEntityID(eid);

          // and the location
          Vessel launcher = states.get(launchPlatform.id);
          Vector3Double wLoc = new Vector3Double();
          wLoc.setX(launcher.longVal);
          wLoc.setY(launcher.latVal);
          wLoc.setZ(randomArea);
          fire.setLocationInWorldCoordinates(wLoc);

          // ok, send it out
          sender.sendPdu(fire);

          System.out.println(": launch of:" + newId + " from:"
              + launchPlatform.id + " aiming for:" + targetId.id);

          // and send out an update
          sendStatusUpdate(torpedo, eid, espdu, sender);
        }

        // Send every 1 sec. Otherwise this will be all over in a fraction of a
        // second.
        Thread.sleep(stepMillis);
      }

      // ok, data complete. send stop PDU
      // The byte array here is the packet in DIS format. We put that into a
      // datagram and send it.
      StopFreezePdu stopPdu = new StopFreezePdu();
      stopPdu.setTimestamp(lastTime);
      stopPdu.setExerciseID(espdu.getExerciseID());
      stopPdu.setReason(STOP_PDU_TERMINATED);

      // and send it
      sender.sendPdu(stopPdu);

      // tell the sender to pack in
      sender.close();

      System.out.println("COMPLETE SENT!");
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

  }

  private void sendStatusUpdate(Vessel thisS, EntityID eid,
      EntityStatePdu espdu, IPduSender sender)
  {
    // update the affiliation
    espdu.setForceId(thisS.force);

    eid.setEntity(thisS.id);

    double disCoordinates[] =
        CoordinateConversions.getXYZfromLatLonDegrees(thisS.latVal,
            thisS.longVal, 0.0);
    Vector3Double location = espdu.getEntityLocation();
    location.setX(disCoordinates[0]);
    location.setY(disCoordinates[1]);
    location.setZ(disCoordinates[2]);

    // sort out the course & speed
    Orientation orientation = espdu.getEntityOrientation();
    orientation.setPhi((float) thisS.courseRads);

    // turn the speed into the 3-vector components
    Vector3Float velocity = espdu.getEntityLinearVelocity();
    velocity.setX((float) thisS.speedVal);
    espdu.setEntityLinearVelocity(velocity);

    // also specify the target appearance
    espdu.setEntityAppearance_damage(thisS.damage);

    // and send it
    sender.sendPdu(espdu);
  }

  private void sendMessage(short exerciseID, long lastTime, long eventType,
      EntityID eid, String msg, IPduSender sender)
  {

    // build up the PDU
    EventReportPdu dp = new EventReportPdu();
    dp.setExerciseID(exerciseID);
    dp.setTimestamp(lastTime);
    dp.setEventType(eventType);

    // produce random participant.
    dp.setOriginatingEntityID(eid);

    // INSERTING TEXT STRING
    //
    VariableDatum d = new VariableDatum();
    byte[] theBytes = msg.getBytes();
    List<OneByteChunk> chunks = new ArrayList<OneByteChunk>();

    for (int i = 0; i < theBytes.length; i++)
    {
      byte thisB = theBytes[i];
      OneByteChunk chunk = new OneByteChunk();
      chunk.setOtherParameters(new byte[]
      {thisB});
      chunks.add(chunk);
    }
    d.setVariableData(chunks);
    d.setVariableDatumLength(theBytes.length);
    d.setVariableDatumID(lastTime);
    List<VariableDatum> datums = new ArrayList<VariableDatum>();
    datums.add(d);
    dp.setVariableDatums(datums);

    // and send it
    sender.sendPdu(dp);
  }

  private Vessel selectRandomEntity(Collection<Vessel> collection)
  {
    int index = (int) (genny.nextDouble() * (double) collection.size());
    Iterator<Vessel> sIter2 = collection.iterator();
    Vessel res = null;
    for (int i = 0; i <= index; i++)
    {
      res = sIter2.next();
    }
    return res;
  }

  public void terminate()
  {
    _terminate = true;
  }

  private void sendDetonation(Vessel firingPlatform, int recipientId,
      EntityID eid, Map<Integer, Vessel> states, long lastTime,
      IPduSender sender)
  {
    // store the id of the firing platform
    eid.setEntity(firingPlatform.id);

    // remove it from the relevant list
    switch (firingPlatform.force)
    {
    case BLUE:
      System.err.println("blue should not detonate!");
      blueParts.remove(firingPlatform);
      break;
    case RED:
      redParts.remove(firingPlatform);
      break;
    case GREEN:
      System.err.println("green should not detonate!");
      greenParts.remove(firingPlatform);
      break;
    }

    // and remove the exploding platform
    states.remove(firingPlatform.id);

    // build up the PDU
    DetonationPdu dp = new DetonationPdu();
    dp.setExerciseID(exerciseId);
    dp.setFiringEntityID(eid);
    dp.setTimestamp(lastTime);

    // and the location
    double disCoordinates[] =
        CoordinateConversions.getXYZfromLatLonDegrees(firingPlatform.latVal,
            firingPlatform.longVal, 0.0);
    Vector3Float location = new Vector3Float();
    location.setX((float) disCoordinates[0]);
    location.setY((float) disCoordinates[1]);
    location.setZ((float) disCoordinates[2]);
    dp.setLocationInEntityCoordinates(location);

    Vector3Double wLoc = new Vector3Double();
    wLoc.setX(firingPlatform.longVal);
    wLoc.setY(firingPlatform.latVal);
    wLoc.setZ(0);
    dp.setLocationInWorldCoordinates(wLoc);

    // and send it
    sender.sendPdu(dp);

    System.out.println(": " + firingPlatform.id + " destroyed " + recipientId);
  }

  private void sendCollision(Vessel movingPlatform, int recipientId,
      EntityID movingId, Map<Integer, Vessel> states, long lastTime,
      IPduSender sender)
  {
    EntityID victimE = new EntityID();
    victimE.setApplication(movingId.getApplication());
    victimE.setEntity(recipientId);
    victimE.setSite(movingId.getSite());

    // store the id of the firing platform
    movingId.setEntity(movingPlatform.id);

    // build up the PDU
    CollisionPdu coll = new CollisionPdu();
    coll.setExerciseID(exerciseId);
    movingId.setEntity(recipientId);
    coll.setCollidingEntityID(movingId);
    coll.setTimestamp(lastTime);

    // and the location
    double disCoordinates[] =
        CoordinateConversions.getXYZfromLatLonDegrees(movingPlatform.latVal,
            movingPlatform.longVal, 0.0);
    Vector3Float location = new Vector3Float();
    location.setX((float) disCoordinates[0]);
    location.setY((float) disCoordinates[1]);
    location.setZ((float) disCoordinates[2]);
    coll.setLocation(location);

    // and send it
    sender.sendPdu(coll);

    System.out.println(": " + movingPlatform.id + " collided with "
        + recipientId);
  }

}
