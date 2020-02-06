package edu.nps.moves.examples;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.nps.moves.dis.AcknowledgePdu;
import edu.nps.moves.dis.ActionRequestPdu;
import edu.nps.moves.dis.CollisionPdu;
import edu.nps.moves.dis.CommentPdu;
import edu.nps.moves.dis.CreateEntityPdu;
import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.RemoveEntityPdu;
import edu.nps.moves.dis.RepairCompletePdu;
import edu.nps.moves.dis.RepairResponsePdu;
import edu.nps.moves.dis.ResupplyCancelPdu;
import edu.nps.moves.dis.ResupplyOfferPdu;
import edu.nps.moves.dis.ResupplyReceivedPdu;
import edu.nps.moves.dis.ServiceRequestPdu;
import edu.nps.moves.dis.StartResumePdu;
import edu.nps.moves.dis.StopFreezePdu;
import edu.nps.moves.disenum.PduType;

/**
 * This is an example that sends many/most types of PDUs. Useful for testing
 * standards compliance or getting a full set of PDUs. It also writes the
 * generated PDUs to an XML file.
 *
 * @author DMcG
 * @version $Id:$
 */
public class PduSender {

	public static final int PORT = 62040;
	public static final String MULTICAST_ADDRESS = "239.1.2.3";

	public static void main(final String args[]) {
		if (args.length == 2) {
			final PduSender sender = new PduSender(Integer.parseInt(args[0]), args[1]);
			sender.run();
		} else {
			System.out.println("Usage: PduSender <port> <multicast group>");
		}
	}

	private int port;

	InetAddress multicastAddress;

	public PduSender(final int port, final String multicast) {
		try {
			this.port = port;
			multicastAddress = InetAddress.getByName(multicast);
			if (!multicastAddress.isMulticastAddress()) {
				System.out.println("Not a multicast address: " + multicast);
			}
		} catch (final Exception e) {
			System.out.println("Unable to open socket");
		}
	}

	public void run() {
		try {
			final List<Pdu> generatedPdus = new ArrayList<Pdu>();

			// Loop through all the enumerated PDU types, create a PDU for each type,
			// and add that PDU to a list.
			for (final PduType pdu : PduType.values()) {
				Pdu aPdu = null;

				switch (pdu) {
				case ENTITY_STATE:
					aPdu = new EntityStatePdu();
					break;

				case COMMENT:
					aPdu = new CommentPdu();
					break;

				case FIRE:
					aPdu = new FirePdu();
					break;

				case DETONATION:
					aPdu = new DetonationPdu();
					break;

				case COLLISION:
					aPdu = new CollisionPdu();
					break;

				case SERVICE_REQUEST:
					aPdu = new ServiceRequestPdu();
					break;

				case RESUPPLY_OFFER:
					aPdu = new ResupplyOfferPdu();
					break;

				case RESUPPLY_RECEIVED:
					aPdu = new ResupplyReceivedPdu();
					break;

				case RESUPPLY_CANCEL:
					aPdu = new ResupplyCancelPdu();
					break;

				case REPAIR_COMPLETE:
					aPdu = new RepairCompletePdu();
					break;

				case REPAIR_RESPONSE:
					aPdu = new RepairResponsePdu();
					break;

				case CREATE_ENTITY:
					aPdu = new CreateEntityPdu();
					break;

				case REMOVE_ENTITY:
					aPdu = new RemoveEntityPdu();
					break;

				case START_RESUME:
					aPdu = new StartResumePdu();
					break;

				case STOP_FREEZE:
					aPdu = new StopFreezePdu();
					break;

				case ACKNOWLEDGE:
					aPdu = new AcknowledgePdu();
					break;

				case ACTION_REQUEST:
					aPdu = new ActionRequestPdu();
					break;

				default:
					System.out.print("PDU of type " + pdu + " not created or sent ");
					System.out.println();
				}

				if (aPdu != null) {
					generatedPdus.add(aPdu);
				}
			}

			// Sort the created PDUs by class name
			Collections.sort(generatedPdus, new ClassNameComparator());

			// Send the PDUs we created
			final InetAddress localMulticastAddress = InetAddress.getByName(MULTICAST_ADDRESS);
			final MulticastSocket socket = new MulticastSocket(PORT);
			socket.joinGroup(localMulticastAddress);

			for (int idx = 0; idx < generatedPdus.size(); idx++) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				final DataOutputStream dos = new DataOutputStream(baos);
				byte[] buffer;

				final Pdu aPdu = generatedPdus.get(idx);
				aPdu.marshal(dos);

				buffer = baos.toByteArray();
				final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, localMulticastAddress, PORT);
				socket.send(packet);
				System.out.println("Sent PDU of type " + aPdu.getClass().getName());
			}

			// write the PDUs out to an XML file.
			// PduContainer container = new PduContainer();
			// container.setPdus(generatedPdus);
			// container.marshallToXml("examplePdus.xml");
		} catch (final Exception e) {
			System.out.println(e);
		}
	}
}
