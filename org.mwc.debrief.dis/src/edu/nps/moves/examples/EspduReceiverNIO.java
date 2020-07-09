package edu.nps.moves.examples;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.disutil.PduFactory;

/**
 * Receives PDUs from the network in IEEE format. Very similar to EspduReciver,
 * but this uses Robert Harder's more memory-efficient NIO code.
 *
 * @author DMcG rharder
 * @version $Id:$
 */
public class EspduReceiverNIO {

	/**
	 * Max size of a PDU in binary format that we can receive. This is actually
	 * somewhat outdated--PDUs can be larger--but this is a reasonable starting
	 * point
	 */
	public static final int MAX_PDU_SIZE = 8192; // This has actually been superceded by a larger buffer size, but good
													// enough for now

	public static void main(final String args[]) {
		MulticastSocket socket;
		DatagramPacket packet;
		InetAddress address;
		final PduFactory pduFactory = new PduFactory();

		try {
			// Specify the socket to receive data
			socket = new MulticastSocket(EspduSender.DIS_DESTINATION_PORT);
			address = InetAddress.getByName(EspduSender.DEFAULT_MULTICAST_GROUP);
			socket.joinGroup(address);

			// Loop infinitely, receiving datagrams
			while (true) {
				final byte buffer[] = new byte[MAX_PDU_SIZE];
				packet = new DatagramPacket(buffer, buffer.length);

				socket.receive(packet);

				// Uses the NIO byte buffer class--wrap a ByteBuffer instance around
				// the data we get from the packet
				final ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData());
				final Pdu pdu = pduFactory.createPdu(byteBuffer);

				System.out.println("got PDU of type: " + pdu.getClass().getName());

			} // end while
		} // End try
		catch (final Exception e) {

			System.out.println(e);
		}

	} // end main
} // end class
