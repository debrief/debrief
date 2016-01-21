package org.mwc.debrief.dis.providers.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.core.IDISPreferences;
import org.mwc.debrief.dis.diagnostics.TestFixListener;
import org.mwc.debrief.dis.diagnostics.TestPrefs;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.providers.IPDUProvider;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.disutil.PduFactory;
import edu.nps.moves.examples.EspduSender;

/**
 * data provider that listens for data on a network multicast socket
 * 
 * @author Ian
 * 
 */
public class NetworkDISProvider implements IPDUProvider
{
	@SuppressWarnings("unused")
	private final IDISNetworkPrefs _myPrefs;
	private List<IDISGeneralPDUListener> _gen = new ArrayList<IDISGeneralPDUListener>();

	/**
	 * Max size of a PDU in binary format that we can receive. This is actually
	 * somewhat outdated--PDUs can be larger--but this is a reasonable starting
	 * point
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
	public void start()
	{
		MulticastSocket socket;
		DatagramPacket packet;
		InetAddress address;
		PduFactory pduFactory = new PduFactory();

		try
		{
			// Specify the socket to receive data
			socket = new MulticastSocket(EspduSender.PORT);
			address = InetAddress.getByName(EspduSender.DEFAULT_MULTICAST_GROUP);
			socket.joinGroup(address);

			// Loop infinitely, receiving datagrams
			while (true)
			{
				byte buffer[] = new byte[MAX_PDU_SIZE];
				packet = new DatagramPacket(buffer, buffer.length);

				socket.receive(packet);

				Pdu pdu = pduFactory.createPdu(packet.getData());

				// share the good news
				Iterator<IDISGeneralPDUListener> gIter = _gen.iterator();
				while (gIter.hasNext())
				{
					IDISGeneralPDUListener git = (IDISGeneralPDUListener) gIter.next();
					git.logPDU(pdu);
				}

			} // end while
		} // End try
		catch (Exception e)
		{

			System.out.println(e);
		}
	}

	/**
	 * stop listening
	 * 
	 */
	public void disconnect()
	{

	}

	public static void main(String[] args)
	{

		IDISModule subject = new DISModule();
		IDISPreferences prefs = new TestPrefs(true, "file.txt");
		IDISNetworkPrefs netPrefs = new CoreNetPrefs(
				EspduSender.DEFAULT_MULTICAST_GROUP, EspduSender.PORT);
		IPDUProvider provider = new NetworkDISProvider(netPrefs);
		TestFixListener fixL = new TestFixListener();

		subject.addGeneralPDUListener(new IDISGeneralPDUListener()
		{

			@Override
			public void logPDU(Pdu pdu)
			{
				System.out.println("data at:" + pdu);
			}

			@Override
			public void complete(String reason)
			{
				// TODO Auto-generated method stub

			}
		});

		subject.setPrefs(prefs);
		subject.addFixListener(fixL);
		subject.setProvider(provider);

		// tell the network provider to start
		provider.start();

	}

}