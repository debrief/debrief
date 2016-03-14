package org.mwc.debrief.dis.diagnostics.senders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.SimulationManagementFamilyPdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;

public class NetworkPduSender implements IPduSender
{
  
  /** interface for objects that are interested in listening to DIS control messages
   * 
   * @author ian
   *
   */
  public static interface IDISControlMessageListener
  {
    void doStop(int appId, short exId);

    void doPlay(int appId, short exId);

    void doPause(int appId, short exId);
  }

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

  private IDISControlMessageListener _controlListener = null;

  private boolean _running = true;

  /**
   * Possible system properties, passed in via -Dattr=val networkMode: unicast, broadcast, multicast
   * destinationIp: where to send the packet. If in multicast mode, this can be mcast. To determine
   * bcast destination IP, use an online bcast address caclulator, for example
   * http://www.remotemonitoringsystems.ca/broadcast.php If in mcast mode, a join() will be done on
   * the mcast address. port: port used for both source and destination.
   * 
   * @param portString
   * @param destinationIpString
   * @param networkModeString
   * 
   * @param args
   */
  public NetworkPduSender(String destinationIpString, String portString,
      String networkModeString, IDISControlMessageListener statusListener)
  {

    _controlListener = statusListener;

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

          doListening();

        }
      }

    }
    catch (Exception e)
    {
      System.out.println("Unable to initialize networking. Exiting.");
      System.out.println(e);
      System.exit(-1);
    }
  }

  private void doListening()
  {
    Runnable doListen = new Runnable()
    {

      @Override
      public void run()
      {

        // also listen on the port
        _running = true;

        while (_running)
        {
          byte buffer[] = new byte[NetworkDISProvider.MAX_PDU_SIZE];
          DatagramPacket packet =
              new DatagramPacket(buffer, buffer.length);

          try
          {
            socket.receive(packet);

            byte[] data = packet.getData();

            ////////////////////////////////
            //
            // note: we aren't using the PDU factory to create the PDU, so we can
            //  avoid the dependency on the dis-enums jar = it complicates the scripts
            //
            ////////////////////////////////
            
            // Promote a signed byte to an int, then do a bitwise AND to wipe out everthing but the 
            // first eight bits. This effectively lets us read this as an unsigned byte
            int pduType = 0x000000FF & (int) data[2]; // The pdu type is a one-byte, unsigned byte in the third byte position.

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            Pdu pdu = null;
            
            switch(pduType)
            {
            case 13:
            { 
              pdu = new StartResumePdu();
              break;
            }
            case 14:
            {
              pdu = new StopFreezePdu();
            } 
            }
            
            ////////////////////////////////
            
            if (pdu != null)
            {
              pdu.unmarshal(dis);
              
              
              if (pdu instanceof SimulationManagementFamilyPdu)
              {
                SimulationManagementFamilyPdu simP =
                    (SimulationManagementFamilyPdu) pdu;
                int appId =
                    simP.getOriginatingEntityID().getApplication();
                short exId = simP.getExerciseID();
                
                switch (pdu.getPduType())
                {
                case 13:
                {
                  // start resume
                  if (_controlListener != null)
                  {
                    _controlListener.doPlay(appId, exId);
                  }
                  break;
                }
                case 14:
                {
                  if (_controlListener != null)
                  {
                    // stop pause
                    StopFreezePdu stopper = (StopFreezePdu) simP;
                    short reason = stopper.getReason();
                    if (reason == IDISStopListener.PDU_FREEZE)
                    {
                      _controlListener.doPause(appId, exId);
                    }
                    else
                    {
                      _controlListener.doStop(appId, exId);
                    }
                    break;
                  }
                }
                }
              }
            }
          }
          catch (IOException ex)
          {
            _running = false;
          }
        }
      }
    };
    
    Thread runner = new Thread(doListen);
    runner.start();
  }

  @Override
  public String toString()
  {
    return destinationIp.toString() + " on " + PORT;
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
    _running = false;

    socket.close();
    socket = null;
  }
}