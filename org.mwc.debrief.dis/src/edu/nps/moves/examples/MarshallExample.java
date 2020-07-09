package edu.nps.moves.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import edu.nps.moves.dis.DetonationPdu;
import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.FirePdu;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.PduContainer;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;

/**
 * Example of using JAXB to marshal and unmarshall PDUs to and from XML. Uses
 * Jaxb 2.1.x.
 *
 * @author DMcG
 * @version $Id:$
 */
public class MarshallExample {

	/**
	 * Shows how to marshal out to XML and read from XML files.
	 *
	 * @param args
	 */
	public static void main(final String args[]) {

		// PduContainer is simply a list of PDUs. The PDUs can be of any type;
		// espdu, fire, detonation, etc. The PDU container is an XML root element,
		// meaning that it can be a top level XML document.

		final PduContainer container = new PduContainer(); // XmlRootElement marked class

		// Create a few PDUs to marshal
		final EntityStatePdu espdu = new EntityStatePdu();
		final FirePdu firePdu = new FirePdu();
		final DetonationPdu detonationPdu = new DetonationPdu();
		final FirePdu bang = new FirePdu();

		// Add them to the XmlRootElement container
		container.getPdus().add(espdu);
		container.getPdus().add(firePdu);
		container.getPdus().add(detonationPdu);
		container.getPdus().add(bang);

		// Create some more espdus, setting the location, velocity, and entity IDs
		// to be random numbers. This will show that the PDU attributes are actually
		// being marshalled.
		for (int idx = 0; idx < 20; idx++) {
			final EntityStatePdu pdu = new EntityStatePdu();

			final Vector3Double loc = pdu.getEntityLocation();
			loc.setX(Math.random());
			loc.setY(Math.random());
			loc.setZ(Math.random());

			final Vector3Float vel = pdu.getEntityLinearVelocity();
			vel.setX((float) Math.random());
			vel.setY((float) Math.random());
			vel.setZ((float) Math.random());

			final EntityID eid = pdu.getEntityID();
			eid.setSite((short) (Math.random() * 1000));
			eid.setApplication((short) (Math.random() * 1000));
			eid.setEntity((short) (Math.random() * 1000));

			container.getPdus().add(pdu);
		}

		// And marshal it out to an XML file. We have the PduContainer, which
		// holds all the PDUs. Marshal it out to the given XML file. This uses
		// jaxb; see the javadoc at the JDK 1.6 documents, in the javax.xml.bind
		// package.

		try {
			// The context, which will contain the package of the classes we marshal out
			final JAXBContext context = JAXBContext.newInstance("edu.nps.moves.dis");

			// Choose the file to send to, and make it pretty-print. The marshaller
			// object is responsible for writing the XML, but it needs to be told
			// a few things, such as the file to write to, the format to write it
			// in, and so on. This is part of JAXB;

			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(container, new FileOutputStream("somePdus.xml"));

			// Just to prove nothing funny is going on, read the XML file back in
			// from the file we just wrote to. Again, this primarily relies on JAXB,
			// from the javax.xml.bind package for Unmarshaller, JaxbContext, and so on.

			final Unmarshaller unmarshaller = context.createUnmarshaller();

			// We get back a PduContainer object from the XML file--this is a container
			// the holds zero or more PDUs.

			final PduContainer unmarshalledObject = (PduContainer) unmarshaller
					.unmarshal(new FileInputStream("somePdus.xml"));
			System.out.println("got " + unmarshalledObject.getNumberOfPdus() + " back");

			// Print out the PDUs; retreive the list of PDUs from the PDU Container
			final List<Pdu> unmarshalledPdus = unmarshalledObject.getPdus();

			System.out.println("Marshalled PDUs out to XML, read them back, contents of list are...");
			for (int idx = 0; idx < unmarshalledPdus.size(); idx++) {
				final Pdu aPdu = unmarshalledPdus.get(idx);
				System.out.println("   Pdu Type: " + aPdu.getClass().getName());

				// For the entity state PDUs we put into the PDU container, print out
				// the contents for the attributes we set
				if (aPdu instanceof EntityStatePdu) {
					final EntityStatePdu es = (EntityStatePdu) aPdu;
					System.out.println("      EID:(" + es.getEntityID().getSite() + ","
							+ es.getEntityID().getApplication() + "," + es.getEntityID().getEntity() + ")");
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}
}
