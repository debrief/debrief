package edu.nps.moves.examples;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Iterator;
import java.util.List;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.disutil.PduFactory;

/**
 * Receives PDUs from the network in IEEE format.
 *
 * @author DMcG
 * @version $Id:$
 */
public class EspduReceiver {

	/**
	 * Max size of a PDU in binary format that we can receive. This is actually
	 * somewhat outdated--PDUs can be larger--but this is a reasonable starting
	 * point
	 */
	public static final int MAX_PDU_SIZE = 8192;

	public static void main(final String args[]) {
		MulticastSocket socket;
		DatagramPacket packet;
		final InetAddress address;
		final PduFactory pduFactory = new PduFactory();

		try {
			// Specify the socket to receive data
			socket = new MulticastSocket(3001);
			socket.setBroadcast(true);

			// address = InetAddress.getByName(EspduSender.DEFAULT_MULTICAST_GROUP);
			// socket.joinGroup(address);

			// Loop infinitely, receiving datagrams
			while (true) {
				final byte buffer[] = new byte[MAX_PDU_SIZE];
				packet = new DatagramPacket(buffer, buffer.length);

				socket.receive(packet);

				final List<Pdu> pduBundle = pduFactory.getPdusFromBundle(packet.getData());
				System.out.println("Bundle size is " + pduBundle.size());

				final Iterator it = pduBundle.iterator();

				while (it.hasNext()) {
					final Pdu aPdu = (Pdu) it.next();

					System.out.print("got PDU of type: " + aPdu.getClass().getName());
					if (aPdu instanceof EntityStatePdu) {
						final EntityID eid = ((EntityStatePdu) aPdu).getEntityID();
						final Vector3Double position = ((EntityStatePdu) aPdu).getEntityLocation();
						System.out.print(
								" EID:[" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");
						System.out.print(" Location in DIS coordinates: [" + position.getX() + ", " + position.getY()
								+ ", " + position.getZ() + "]");
					}
					System.out.println();
				} // end trop through PDU bundle

			} // end while
		} // End try
		catch (final Exception e) {

			System.out.println(e);
		}

	} // end main
} // end class
