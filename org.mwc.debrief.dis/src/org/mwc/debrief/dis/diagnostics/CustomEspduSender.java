package org.mwc.debrief.dis.diagnostics;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.EntityType;
import edu.nps.moves.dis.EventReportPdu;
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
  public enum NetworkMode
  {
    UNICAST, MULTICAST, BROADCAST
  };

  /** default multicast group we send on */
  public static final String DEFAULT_MULTICAST_GROUP = "239.1.2.3";

  /** Port we send on */
  public static final int PORT = 62040;

  private boolean _terminate;

  private static class State
  {
    private State(final int id, double oLat, double oLong, double range)
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
  }

  private Map<Integer, State> states = new HashMap<Integer, State>();

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
    MulticastSocket socket = null;
    // DisTime disTime = DisTime.getInstance();

    // clear the states
    states.clear();

    long stepMillis = 1000;
    long numParts = 1;

    // try to extract the step millis from the args
    if (args.length >= 1 && args[0].length() > 1)
    {
      stepMillis = Long.parseLong(args[0]);
    }

    if (args.length >= 2)
    {
      numParts = Long.parseLong(args[1]);
    }

    // Default settings. These are used if no system properties are set.
    // If system properties are passed in, these are over ridden.
    int port = PORT;
    @SuppressWarnings("unused")
    NetworkMode mode = NetworkMode.MULTICAST;
    InetAddress destinationIp = null;

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
    String networkModeString = systemProperties.getProperty("networkMode"); // unicast
                                                                            // or
                                                                            // multicast
                                                                            // or
                                                                            // broadcast

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

    short exerciseId = (short) (Math.random() * 1000d);

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

      System.out.println("Sending 100 ESPDU packets to "
          + destinationIp.toString());

      final double startX = 50.1;
      final double startY = -1.87;
      final double startZ = 0.01;

      // generate the inital states
      for (int i = 0; i < numParts; i++)
      {
        int eId = i + 1;// 1 + (int) (Math.random() * 20d);
        State newS = new State(eId, startX, startY, startZ);
        states.put(eId, newS);
      }

      // generate 100 entries
      for (int idx = 0; idx < 1000; idx++)
      {

        // just check if we're being terminated early
        if (_terminate)
        {
          break;
        }

        // increment time
        lastTime += 5 * 60 * 1000;

        espdu.setTimestamp(lastTime);

        // loop for each participants
        Iterator<Integer> sIter = states.keySet().iterator();
        while (sIter.hasNext())
        {
          final Integer thisId = (Integer) sIter.next();
          final State thisS = states.get(thisId);

          eid.setEntity(thisS.id);

          double courseRads = thisS.courseRads;

          double dLat = Math.cos(courseRads) * 0.01;
          double dLon = Math.sin(courseRads) * 0.01;

          thisS.longVal += dLon;
          thisS.latVal += dLat;

          if ((idx % 10) == 0)
          {
            final double newCourse = ((int) (Math.random() * 36d)) * 10d;
            thisS.courseRads = Math.toRadians(newCourse);
          }

          // lon = lon + (double) ((double) idx / 1000.0);

          double disCoordinates[] =
              CoordinateConversions.getXYZfromLatLonDegrees(thisS.latVal,
                  thisS.longVal, 0.0);
          Vector3Double location = espdu.getEntityLocation();
          location.setX(disCoordinates[0]);
          location.setY(disCoordinates[1]);
          location.setZ(disCoordinates[2]);

          // Optionally, we can do some rotation of the entity
          /*
           * Orientation orientation = espdu.getEntityOrientation(); float psi =
           * orientation.getPsi(); psi = psi + idx; orientation.setPsi(psi);
           * orientation.setTheta((float)(orientation.getTheta() + idx /2.0));
           */

          // You can set other ESPDU values here, such as the velocity,
          // acceleration,
          // and so on.

          // Marshal out the espdu object to a byte array, then send a datagram
          // packet with that data in it.
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(baos);
          espdu.marshal(dos);

          // The byte array here is the packet in DIS format. We put that into a
          // datagram and send it.
          byte[] data = baos.toByteArray();

          DatagramPacket packet =
              new DatagramPacket(data, data.length, destinationIp, PORT);

          socket.send(packet);

          location = espdu.getEntityLocation();
        }

        // put in a random detonation
        if ((states.size() > 1) && (Math.random() >= 0.9))
        {
          System.out.println("===== DETONATION =====");

          Iterator<Integer> iter = states.keySet().iterator();
          State recipient = states.get(iter.next());
          State firingPlatform = states.get(iter.next());

          // store the id of the firing platform
          eid.setEntity(firingPlatform.id);

          // and remove the recipient
          states.remove(recipient.id);

          // build up the PDU
          DetonationPdu dp = new DetonationPdu();
          dp.setExerciseID(espdu.getExerciseID());
          dp.setFiringEntityID(eid);
          dp.setTimestamp(lastTime);

          // and the location
          double disCoordinates[] =
              CoordinateConversions.getXYZfromLatLonDegrees(recipient.latVal,
                  recipient.longVal, 0.0);
          Vector3Float location = new Vector3Float();
          location.setX((float) disCoordinates[0]);
          location.setY((float) disCoordinates[1]);
          location.setZ((float) disCoordinates[2]);
          dp.setLocationInEntityCoordinates(location);

          Vector3Double wLoc = new Vector3Double();
          wLoc.setX(recipient.longVal);
          wLoc.setY(recipient.latVal);
          wLoc.setZ(startZ);
          dp.setLocationInWorldCoordinates(wLoc);

          // Marshal out the espdu object to a byte array, then send a datagram
          // packet with that data in it.
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(baos);
          dp.marshal(dos);

          // The byte array here is the packet in DIS format. We put that into a
          // datagram and send it.
          byte[] data = baos.toByteArray();

          DatagramPacket packet =
              new DatagramPacket(data, data.length, destinationIp, PORT);

          socket.send(packet);
        }
        

        // put in a random event
        double thisR = Math.random();
        if ((states.size() > 1) && (thisR >= 0.6))
        {
          System.out.println("===== EVENT ===== ");

          // build up the PDU
          EventReportPdu dp = new EventReportPdu();
          dp.setExerciseID(espdu.getExerciseID());
          dp.setTimestamp(lastTime);
          dp.setEventType((long) (Math.random() * 50));
          
          // produce random participant.
          int partId = randomEntity();
          eid.setEntity(partId);
          dp.setOriginatingEntityID(eid);

          // Marshal out the espdu object to a byte array, then send a datagram
          // packet with that data in it.
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(baos);
          dp.marshal(dos);

          // The byte array here is the packet in DIS format. We put that into a
          // datagram and send it.
          byte[] data = baos.toByteArray();

          DatagramPacket packet =
              new DatagramPacket(data, data.length, destinationIp, PORT);

          socket.send(packet);
        }

        

        // Send every 1 sec. Otherwise this will be all over in a fraction of a
        // second.
        Thread.sleep(stepMillis);
      }
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

  }

  private int randomEntity()
  {
    int index = (int) (Math.random() * (double) states.size());
    Iterator<Integer> sIter2 = states.keySet().iterator();
    int partId = 0;
    for(int i=0;i<=index;i++)
    {
      partId = sIter2.next();
    }
    return partId;
  }

  public void terminate()
  {
    _terminate = true;
  }

}
