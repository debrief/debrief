package org.mwc.debrief.dis.diagnostics.senders;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Properties;


import edu.nps.moves.dis.Pdu;

public class NetworkPduSender implements IPduSender
{
  public enum NetworkMode
  {
    UNICAST, MULTICAST, BROADCAST
  };



  private MulticastSocket socket;

  private InetAddress destinationIp;
  
  /** default multicast group we send on */
  public static final String DEFAULT_MULTICAST_GROUP = "239.1.2.3";

  /** Port we send on */
  public static final int PORT = 62040;



  /**
   * Possible system properties, passed in via -Dattr=val networkMode: unicast, broadcast, multicast
   * destinationIp: where to send the packet. If in multicast mode, this can be mcast. To determine
   * bcast destination IP, use an online bcast address caclulator, for example
   * http://www.remotemonitoringsystems.ca/broadcast.php If in mcast mode, a join() will be done on
   * the mcast address. port: port used for both source and destination.
   * 
   * @param args
   */
  public NetworkPduSender()
  {

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
  }

  
  
  @Override
  public String toString()
  {
    return destinationIp.toString();
  }



  @Override
  public void sendPdu(final Pdu pdu)
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

    try
    {
      socket.send(packet);
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void close()
  {
    try
    {
      socket.leaveGroup(destinationIp);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      socket.close();
      socket = null;
    }
    
  }
}