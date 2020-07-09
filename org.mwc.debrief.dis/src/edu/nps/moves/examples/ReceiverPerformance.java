package edu.nps.moves.examples;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.disutil.PduFactory;

public class ReceiverPerformance {
	public static final int PORT = 62040;
	public static final String MULTICAST_GROUP = "239.1.2.3";
	public static final boolean USE_FAST_ESPDU = false;

	public static void main(final String args[]) {
		PduFactory factory;
		MulticastSocket socket = null;
		InetAddress address = null;

		try {
			socket = new MulticastSocket(PORT);
			address = InetAddress.getByName(MULTICAST_GROUP);
			socket.joinGroup(address);

			factory = new PduFactory();

			while (true) {
				final byte buffer[] = new byte[1500];
				DatagramPacket packet;

				packet = new DatagramPacket(buffer, buffer.length);

				socket.receive(packet);

				final Pdu pdu = factory.createPdu(packet.getData());
				System.out.println("pdu is " + pdu.getPduType());
			}

		} catch (final Exception e) {
			System.out.println(e);
		}

	}

}
