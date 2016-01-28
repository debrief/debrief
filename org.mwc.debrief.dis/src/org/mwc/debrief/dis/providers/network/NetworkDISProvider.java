package org.mwc.debrief.dis.providers.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
   * start listening
   * 
   */
  public void attach()
  {

    Job job = new Job("Handle incoming")
    {
      @Override
      protected IStatus run(IProgressMonitor monitor)
      {
        // set the running flag to true
        _running = true;

        startListening();
        // use this to open a Shell in the UI thread
        return Status.OK_STATUS;
      }

    };
    job.setUser(false);
    job.schedule();
  }

  /**
   * stop listening
   * 
   */
  public void detach()
  {
    // set the flag, so we naturally stop
    _running = false;
  }

  /**
   * start listening
   * 
   */
  private void startListening()
  {
    MulticastSocket socket;
    DatagramPacket packet;
    InetAddress address;
    PduFactory pduFactory = new PduFactory();

    try
    {
      // Specify the socket to receive data
      socket = new MulticastSocket(_myPrefs.getPort());
      address = InetAddress.getByName(_myPrefs.getIPAddress());
      socket.joinGroup(address);

      // Loop infinitely, receiving datagrams
      while (_running)
      {
        byte buffer[] = new byte[MAX_PDU_SIZE];
        packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

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
          System.err.println("PDU not recognised");
        }

      } // end while

      // ok, we've finished
      socket.leaveGroup(address);

    } // End try
    catch (Exception e)
    {
      e.printStackTrace();
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