package org.mwc.debrief.dis.providers.network;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public void connect()
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
				//
				// System.out.print("got PDU of type: " + pdu.getClass().getName());
				// if(pdu instanceof EntityStatePdu)
				// {
				// EntityID eid = ((EntityStatePdu)pdu).getEntityID();
				// Vector3Double position = ((EntityStatePdu)pdu).getEntityLocation();
				// System.out.print(" EID:[" + eid.getSite() + ", " +
				// eid.getApplication() + ", " + eid.getEntity() + "] ");
				// System.out.print(" Location in DIS coordinates: [" + position.getX()
				// + ", " + position.getY() + ", " + position.getZ() + "]");
				// }
				// System.out.println();

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

}