package org.mwc.debrief.dis.providers.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.DISFilters;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.disutil.PduFactory;

/**
 * data provider that listens for data on a network multicast socket
 * 
 * @author Ian
 * 
 */
public class NetworkDISProvider implements IPDUProvider, IDISController
{
  public static final short APPLICATION_ID = 4321;
  public static final short SITE_ID = 5432;
  
  private final IDISNetworkPrefs _myPrefs;
  private List<IDISGeneralPDUListener> _gen =
      new ArrayList<IDISGeneralPDUListener>();
  private boolean _running;
  private MulticastSocket _listenerSocket;
  private Thread newJob;
  private short _curExercise;
  private EntityID _myID;
  private int _thePort;
  private MulticastSocket _senderSocket;
  protected InetAddress _theAddress;

  /**
   * Max size of a PDU in binary format that we can receive. This is actually somewhat
   * outdated--PDUs can be larger--but this is a reasonable starting point
   */
  public static final int MAX_PDU_SIZE = 8192;

  public NetworkDISProvider(IDISNetworkPrefs prefs)
  {
    _myPrefs = prefs;
  }

  @Override
  public void addListener(IDISGeneralPDUListener listener)
  {
    _gen.add(listener);
  }

  /**
   * get the network preferences
   * 
   * @return
   */
  public IDISNetworkPrefs getPrefs()
  {
    return _myPrefs;
  }

  /**
   * start listening
   * 
   */
  public void attach(final DISFilters filters, EntityID eid)
  {
    _myID = eid;
    
    Runnable runnable = new Runnable()
    {

      @Override
      public void run()
      {
        System.out.println("Listing for DIS messages on address:"
            + _myPrefs.getIPAddress() + " port:" + _myPrefs.getPort());

        // Specify the socket to receive data
        _thePort = _myPrefs.getPort();
        try
        {
          _theAddress = InetAddress.getByName(_myPrefs.getIPAddress());

          // Set up a socket to send information
          _senderSocket = new MulticastSocket(_thePort);

          // and start listening
          startListening(filters);
        }
        catch (UnknownHostException ue)
        {
          CorePlugin.logError(Status.ERROR, "DIS couldn't connect to host", ue);
        }
        catch (SocketException se)
        {
          CorePlugin.logError(Status.ERROR, "DIS socket exception", se);
        }
        catch (IOException e)
        {
          CorePlugin.logError(Status.ERROR, "DIS create multicast", e);
        }
      }
    };
    newJob = new Thread(runnable);
    newJob.start();

  }

  /**
   * stop listening
   * 
   */
  public void detach()
  {
    // set the flag, so we naturally stop
    _running = false;

    // and force the socket to close

    // hmm, disconnect was hanging, poss because it's synchronised
    // _listenerSocket.disconnect();

    if (_listenerSocket != null)
      _listenerSocket.close();

    if (_senderSocket != null)
      _senderSocket.close();
  }

  /**
   * start listening
   * 
   * @param filters
   * 
   */
  private void startListening(final DISFilters filters)
  {
    // set the running flag to true
    _running = true;

    _listenerSocket = null;
    DatagramPacket packet;
    PduFactory pduFactory = new PduFactory();

    try
    {
      _listenerSocket = new MulticastSocket(_thePort);
      _listenerSocket.joinGroup(_theAddress);

      // Loop infinitely, receiving datagrams
      while (_running)
      {
        byte buffer[] = new byte[MAX_PDU_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);

        _listenerSocket.receive(packet);

        Pdu pdu = pduFactory.createPdu(packet.getData());

        if (pdu != null)
        {
          // store the current exercise details
          _curExercise = pdu.getExerciseID();

          // check if it matches our filter
          if (filters == null || filters.accepts(pdu))
          {
            // share the good news
            Iterator<IDISGeneralPDUListener> gIter = _gen.iterator();
            while (gIter.hasNext())
            {
              IDISGeneralPDUListener git =
                  (IDISGeneralPDUListener) gIter.next();
              git.logPDU(pdu);
            }
          }
        }
        else
        {
          int pduType = 0x000000FF & (int) packet.getData()[2]; // The pdu type is a one-byte,
                                                                // unsigned byte in the third byte
                                                                // position.

          System.err.println("PDU not recognised, type:" + pduType);
        }

      } // end while

    } // End try
    catch (SocketException se)
    {
      // ok - this happens when the socket is killed.
      // we don't need to do anything
    }
    catch (Exception e)
    {
      CorePlugin.logError(Status.ERROR, "DIS Listening Exception", e);
    }
    finally
    {
      if (_listenerSocket != null && !_listenerSocket.isClosed())
      {
        // ok, we've finished
        try
        {
          if (_theAddress != null)
          {
            _listenerSocket.leaveGroup(_theAddress);
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        finally
        {
          _listenerSocket.close();
        }
      }
    }
  }

  @Override
  public void sendPause()
  {
    // create the Pause PDU
    StopFreezePdu stop = new StopFreezePdu();
    stop.setExerciseID(_curExercise);
    stop.setOriginatingEntityID(_myID);
    stop.setReason(IDISStopListener.PDU_FREEZE);
    sendPDU(stop);
  }

  @Override
  public void sendStop()
  {
    // create the Pause PDU
    StopFreezePdu stop = new StopFreezePdu();
    stop.setExerciseID(_curExercise);
    stop.setOriginatingEntityID(_myID);
    stop.setReason(IDISStopListener.PDU_STOP);
    sendPDU(stop);
  }

  @Override
  public void sendPlay()
  {
    // create the Pause PDU
    StartResumePdu play = new StartResumePdu();
    play.setOriginatingEntityID(_myID);
    play.setExerciseID(_curExercise);

    sendPDU(play);
  }

  private void sendPDU(Pdu pdu)
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
        new DatagramPacket(data, data.length, _theAddress, _thePort);

    try
    {
      _senderSocket.send(packet);
    }
    catch (IOException e)
    {
      CorePlugin.logError(Status.ERROR, "DIS couldn't send PDU", e);
    }
  }
}