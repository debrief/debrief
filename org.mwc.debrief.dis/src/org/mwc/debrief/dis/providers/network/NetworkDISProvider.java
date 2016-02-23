package org.mwc.debrief.dis.providers.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.disutil.PduFactory;

/**
 * data provider that listens for data on a network multicast socket
 * 
 * @author Ian
 * 
 */
public class NetworkDISProvider implements IPDUProvider
{
  private final IDISNetworkPrefs _myPrefs;
  private List<IDISGeneralPDUListener> _gen =
      new ArrayList<IDISGeneralPDUListener>();
  private boolean _running;
  private MulticastSocket _listenerSocket;
  private Thread newJob;

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
  public void attach()
  {
    Runnable runnable = new Runnable(){

      @Override
      public void run()
      {
        System.out.println("Listing for DIS messages on address:" + _myPrefs.getIPAddress() + " port:" + _myPrefs.getPort());
        startListening();
      }};
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
    _listenerSocket.disconnect();
    _listenerSocket.close();
    
    // force the thread to close
    newJob.stop();
  }

  /**
   * start listening
   * 
   */
  private void startListening()
  {
    // set the running flag to true
    _running = true;

    _listenerSocket = null;
    DatagramPacket packet;
    InetAddress address = null;
    PduFactory pduFactory = new PduFactory();

    try
    {
      // Specify the socket to receive data
      _listenerSocket = new MulticastSocket(_myPrefs.getPort());
      address = InetAddress.getByName(_myPrefs.getIPAddress());
      _listenerSocket.joinGroup(address);

      // Loop infinitely, receiving datagrams
      while (_running)
      {
        byte buffer[] = new byte[MAX_PDU_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);

        _listenerSocket.receive(packet);

        Pdu pdu = pduFactory.createPdu(packet.getData());

        if (pdu != null)
        {
          // share the good news
          Iterator<IDISGeneralPDUListener> gIter = _gen.iterator();
          while (gIter.hasNext())
          {
            IDISGeneralPDUListener git = (IDISGeneralPDUListener) gIter.next();
            git.logPDU(pdu);
          }
        }
        else
        {
          int pduType = 0x000000FF & (int) packet.getData()[2]; // The pdu type is a one-byte, unsigned byte in the third byte position.

          System.err.println("PDU not recognised, maybe:" + pduType);
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
      e.printStackTrace();
    }
    finally
    {
      if (_listenerSocket != null && !_listenerSocket.isClosed())
      {
        // ok, we've finished
        try
        {
          if (address != null)
          {
            _listenerSocket.leaveGroup(address);
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

  //
  // public static void main(String[] args)
  // {
  //
  // IDISPreferences prefs = new TestPrefs(true, "file.txt");
  // IDISModule subject = new DISModule(prefs);
  // IDISNetworkPrefs netPrefs = new CoreNetPrefs(
  // EspduSender.DEFAULT_MULTICAST_GROUP, EspduSender.PORT);
  // IPDUProvider provider = new NetworkDISProvider(netPrefs);
  // subject.setProvider(provider);
  //
  // // tell the network provider to start
  // provider.attach();
  // }

}