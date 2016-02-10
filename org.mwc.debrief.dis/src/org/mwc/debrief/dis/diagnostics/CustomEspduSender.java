package org.mwc.debrief.dis.diagnostics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EntityType;
import edu.nps.moves.dis.EventReportPdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.OneByteChunk;
import edu.nps.moves.dis.Pdu;
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
public class CustomEspduSender
{
  private static final short STOP_PDU_TERMINATED = 2;

  public enum NetworkMode
  {
    UNICAST, MULTICAST, BROADCAST
  };

  /** default multicast group we send on */
  public static final String DEFAULT_MULTICAST_GROUP = "239.1.2.3";

  /** Port we send on */
  public static final int PORT = 62040;

  private boolean _terminate;

  private MulticastSocket socket;

  private InetAddress destinationIp;

  private short exerciseId;

  private class Participant
  {
    private Participant(final int id, double oLat, double oLong, double range)
    {
      this.id = id;
      latVal = oLat + Math.random() * range;
      longVal = oLong + Math.random() * range;
      courseRads = Math.toRadians(((int) (Math.random() * 36d)) * 10d);
    }

    final int id;
    double latVal;
    double longVal;
    double courseRads;

    /**
     * if this participant has a target that it aims for
     * 
     */
    public int targetId = -1;

    public void update(Map<Integer, Participant> states, double distStep,
        int idx, long lastTime, EntityID sampleId)
    {
      // hmm, do we have a target?
      if (targetId != -1)
      {
        Participant myTarget = null;

        // does this target exist
        Participant[] parts = states.values().toArray(new Participant[]
        {null});
        for (int i = 0; i < parts.length; i++)
        {
          final Participant thisP = parts[i];

          // hey, don't look at ourselves...
          if (thisP.id == id)
          {
            // skip to the next loop
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
              sendDetonation(this, thisP.id, sampleId, states, lastTime);
            }
            else
            {
              sendCollision(this, thisP.id, sampleId, states, lastTime);
            }
          }

          if (thisP.id == targetId)
          {
            double bearing = Math.atan2(dLon, dLat);
            courseRads = bearing;
            myTarget = thisP;

            // target lost, forget about it
            // final String theMsg =
            // "platform:" + id + " turned towards:" + targetId;
            // sendMessage(exerciseId, lastTime, 12, sampleId, theMsg);
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
          sendMessage(exerciseId, lastTime, 12, sampleId, theMsg);
          System.out.println(theMsg);
          targetId = -1;
        }

      }

      // ok, handle the movement
      double dLat = Math.cos(courseRads) * distStep;
      double dLon = Math.sin(courseRads) * distStep;

      longVal += dLon;
      latVal += dLat;

      // see if we're going to do a random turn

      if (Math.random() > 0.8 && targetId == -1)
      {
        final double newCourse = ((int) (Math.random() * 36d)) * 10d;
        courseRads = Math.toRadians(newCourse);
      }
    }
  }

  public static void main(String args[])
  {
    CustomEspduSender sender = new CustomEspduSender();
    sender.run(args);
  }

  /**
   * Possible system properties, passed in via -Dattr=val networkMode: unicast, broadcast, multicast
   * destinationIp: where to send the packet. If in multicast mode, this can be mcast. To determine
   * bcast destination IP, use an online bcast address caclulator, for example
   * http://www.remotemonitoringsystems.ca/broadcast.php If in mcast mode, a join() will be done on
   * the mcast address. port: port used for both source and destination.
   * 
   * @param args
   */
  public void run(String args[])
  {
    /** an entity state pdu */
    EntityStatePdu espdu = new EntityStatePdu();
    // DisTime disTime = DisTime.getInstance();

    // declare the states
    final Map<Integer, Participant> states =
        new HashMap<Integer, Participant>();

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

    // Default settings. These are used if no system properties are set.
    // If system properties are passed in, these are over ridden.
    int port = PORT;
    @SuppressWarnings("unused")
    NetworkMode mode = NetworkMode.MULTICAST;
    destinationIp = null;

    try
    {
      destinationIp = InetAddress.getByName(DEFAULT_MULTICAST_GROUP);
    }
    catch (Exception e)
    {
      System.out.println(e + " Cannot create multicast address");
      System.exit(0);
    }

    // All system properties, passed in on the command line via
    // -Dattribute=value
    Properties systemProperties = System.getProperties();

    // IP address we send to
    String destinationIpString = systemProperties.getProperty("destinationIp");

    // Port we send to, and local port we open the socket on
    String portString = systemProperties.getProperty("port");

    // Network mode: unicast, multicast, broadcast
    String networkModeString = systemProperties.getProperty("networkMode");
    // unicast or multicast or broadcast

    // Set up a socket to send information
    try
    {
      // Port we send to
      if (portString != null)
        port = Integer.parseInt(portString);

      socket = new MulticastSocket(port);

      // Where we send packets to, the destination IP address
      if (destinationIpString != null)
      {
        destinationIp = InetAddress.getByName(destinationIpString);
      }

      // Type of transport: unicast, broadcast, or multicast
      if (networkModeString != null)
      {
        if (networkModeString.equalsIgnoreCase("unicast"))
          mode = NetworkMode.UNICAST;
        else if (networkModeString.equalsIgnoreCase("broadcast"))
          mode = NetworkMode.BROADCAST;
        else if (networkModeString.equalsIgnoreCase("multicast"))
        {
          mode = NetworkMode.MULTICAST;
          if (!destinationIp.isMulticastAddress())
          {
            throw new RuntimeException(
                "Sending to multicast address, but destination address "
                    + destinationIp.toString() + "is not multicast");
          }

          socket.joinGroup(destinationIp);

        }
      } // end networkModeString
    }
    catch (Exception e)
    {
      System.out.println("Unable to initialize networking. Exiting.");
      System.out.println(e);
      System.exit(-1);
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

    int randomHour = (int) (2 + Math.random() * 20);
    @SuppressWarnings("deprecation")
    long lastTime = new Date(2015, 1, 1, randomHour, 0).getTime();

    // Loop through sending 100 ESPDUs
    try
    {
      _terminate = false;

      System.out.println("Sending " + numMessages + " ESPDU packets to "
          + destinationIp.toString());

      final double startX = 50.1;
      final double startY = -1.87;
      final double startZ = 0.01;

      // generate the inital states
      for (int i = 0; i < numParts; i++)
      {
        int eId = i + 1;// 1 + (int) (Math.random() * 20d);
        Participant newS = new Participant(eId, startX, startY, startZ);
        states.put(eId, newS);
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
        Participant[] parts = states.values().toArray(new Participant[]
        {null});
        for (int i = 0; i < parts.length; i++)
        {
          Participant thisS = parts[i];

          eid.setEntity(thisS.id);
          final double distStep = 0.01;

          // get the subject to move forward
          thisS.update(states, distStep, idx, lastTime, eid);

          double disCoordinates[] =
              CoordinateConversions.getXYZfromLatLonDegrees(thisS.latVal,
                  thisS.longVal, 0.0);
          Vector3Double location = espdu.getEntityLocation();
          location.setX(disCoordinates[0]);
          location.setY(disCoordinates[1]);
          location.setZ(disCoordinates[2]);

          // and send it
          sendPdu(espdu);

          location = espdu.getEntityLocation();
        }

        // put in a random event
        double thisR = Math.random();
        if ((states.size() > 1) && (thisR >= 0.6))
        {
          // System.out.println("===== EVENT ===== ");
          //
          // // build up the PDU
          // EventReportPdu dp = new EventReportPdu();
          // dp.setExerciseID(espdu.getExerciseID());
          // dp.setTimestamp(lastTime);
          // dp.setEventType((long) (Math.random() * 50));
          //
          // // produce random participant.
          // int partId = randomEntity(states.values()).id;
          // eid.setEntity(partId);
          // dp.setOriginatingEntityID(eid);
          //
          // sendMessage(espdu.getExerciseID(), lastTime, 12, eid, "some message");
        }

        // put in a random launch
        if (Math.random() >= 0.92)
        {
          System.out.println("===== LAUNCH ===== ");
          final int launchId = randomEntity(states.values()).id;

          final int newId = (int) (1000 + (Math.random() * 1000d));
          Participant newS = new Participant(newId, startX, startY, startZ);

          // try to give the new vehicle a target
          Participant targetId = randomEntity(states.values());
          newS.targetId = targetId.id;

          // and remember it
          states.put(newId, newS);

          // also send out the "fired" message
          FirePdu fire = new FirePdu();
          fire.setExerciseID(espdu.getExerciseID());
          fire.setTimestamp(lastTime);
          eid.setEntity(newId);
          fire.setFiringEntityID(eid);

          // and the location
          Participant launcher = states.get(launchId);
          Vector3Double wLoc = new Vector3Double();
          wLoc.setX(launcher.longVal);
          wLoc.setY(launcher.latVal);
          wLoc.setZ(startZ);
          fire.setLocationInWorldCoordinates(wLoc);

          System.out.println(": launch of:" + newId + " from:" + launchId
              + " aiming for:" + targetId.id);

        }

        // Send every 1 sec. Otherwise this will be all over in a fraction of a
        // second.
        Thread.sleep(stepMillis);
      }

      // ok, data complete. send stop PDU
      // The byte array here is the packet in DIS format. We put that into a
      // datagram and send it.
      StopFreezePdu stopPdu = new StopFreezePdu();
      stopPdu.setReason(STOP_PDU_TERMINATED);

      // and send it
      sendPdu(stopPdu);

      System.out.println("COMPLETE SENT!");
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

  }

  private void sendPdu(final Pdu pdu) throws IOException
  {
    // Marshal out the espdu object to a byte array, then send a datagram
    // packet with that data in it.
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    pdu.marshal(dos);

    // The byte array here is the packet in DIS format. We put that into a
    // datagram and send it.
    byte[] data = baos.toByteArray();

    DatagramPacket packet =
        new DatagramPacket(data, data.length, destinationIp, PORT);

    socket.send(packet);
  }

  private void sendMessage(short exerciseID, long lastTime, long eventType,
      EntityID eid, String msg)
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
    try
    {
      sendPdu(dp);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private Participant randomEntity(Collection<Participant> collection)
  {
    int index = (int) (Math.random() * (double) collection.size());
    Iterator<Participant> sIter2 = collection.iterator();
    Participant res = null;
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

  private void sendDetonation(Participant firingPlatform, int recipientId,
      EntityID eid, Map<Integer, Participant> states, long lastTime)
  {

    // store the id of the firing platform
    eid.setEntity(firingPlatform.id);

    // and remove the recipient
    states.remove(firingPlatform.id);

    // hmm, also remove the detonating platform
    states.remove(recipientId);

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
    try
    {
      sendPdu(dp);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println(": " + firingPlatform.id + " destroyed " + recipientId);
  }

  private void sendCollision(Participant movingPlatform, int recipientId,
      EntityID movingId, Map<Integer, Participant> states, long lastTime)
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
    try
    {
      sendPdu(coll);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println(": " + movingPlatform.id + " collided with "
        + recipientId);
  }

}
