
package edu.nps.moves.disutil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.PduContainer;

/**
 * Reads concatenated PDUs, not necessarily of the same type or the same length,
 * from a binary file. The assumption is that the PDUs were placed in IEEE
 * format, concatenated one after the other, into the file.
 * <p>
 *
 * It's not uncommon to place serveral PDUs in a single datagram packet, since
 * placing one in each datagram can cause a very heavy network load. This class
 * is useful for extracting the PDUs from that one big byte array. It's also not
 * uncommon to see people simply write out IEEE PDUs in a file, one after the
 * other. It's a useful format, but then you need to read it back, which is what
 * this does.
 * <p>
 *
 * This also includes an "index" method that returns a big array with the input
 * stream pointer position of the start of each PDU. This is useful for indexing
 * into the input stream for semi-random access to PDUs in the stream.
 * <p>
 *
 * This class is too profliigate with memory and should be rewritten to take
 * advantage of some NIO classes.
 *
 * @author DMcG
 */
public class ConcatenatedDisPdus {
	/**
	 * Useful for some testing, maybe some example code
	 *
	 * @param args
	 */
	public static void main(final String args[]) {
		try {
			FileInputStream fis = new FileInputStream(args[0]);
			ConcatenatedDisPdus concat = new ConcatenatedDisPdus(fis);

			final PduContainer container = concat.getAllPdusInPduContainer();
			System.out.println("got back " + container.getNumberOfPdus());

			final JAXBContext context = JAXBContext.newInstance("edu.nps.moves.dis");
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(container, new FileOutputStream("someWorkbenchPdus.xml"));

			fis.close();
			fis = new FileInputStream(args[0]);
			concat = new ConcatenatedDisPdus(fis);
			concat.getIndexes();

		} catch (final Exception e) {
			System.out.println(e);
		}
	}

	private final InputStream is;

	private final PduFactory pduFactory;

	public ConcatenatedDisPdus(final InputStream is) {
		this.is = is;
		pduFactory = new PduFactory();
	}

	/**
	 * Returns a List of all the PDUs remaining in the input stream
	 *
	 * @return List of all the PDUs remaining
	 */
	public List<Pdu> getAllPdus() {
		Pdu aPdu;
		final List<Pdu> pdus = new ArrayList<Pdu>();

		while ((aPdu = this.getNextPdu()) != null) {
			pdus.add(aPdu);
		}

		return pdus;
	}

	/**
	 * Returns all the PDUs in the concatenated PDU input stream, starting with the
	 * current file position.
	 * <p>
	 *
	 * @return a PduContainer with all the pdus remainig in the input stream
	 */
	public PduContainer getAllPdusInPduContainer() {
		final List<Pdu> pdus = this.getAllPdus();
		final PduContainer container = new PduContainer();
		container.setPdus(pdus);

		return container;
	}

	/**
	 * Returns an array of all the index positions in the input stream that are the
	 * starting points for each PDU. Once this has been done, if you want to read
	 * PDUs at the indexes, you should close the input stream and re-open it so that
	 * the position pointer is at zero. Or, if the stream supports it, reset the
	 * pointer position to zero.
	 *
	 * @return array of ints, each entry the starting point (in bytes) of a PDU
	 */
	public int[] getIndexes() {
		final List<Integer> indexes = new ArrayList<Integer>();

		int currentPos = 0;
		indexes.add(new Integer(currentPos));

		try {
			while (true) // loop ends via thrown exception (EOF) or finding a PDU length of zero or less
			{
				final byte[] initialBytes = new byte[10];
				is.read(initialBytes);

				short pduLength = 0;
				final int ch1 = initialBytes[8] & 0x000000ff;
				final int ch2 = initialBytes[9] & 0x000000ff;
				pduLength = (short) ((ch1 << 8) + (ch2 << 0));

				// System.out.println("PDU length in index is " + pduLength);
				if (pduLength <= 0) {
					break;
				}

				currentPos = currentPos + pduLength;
				indexes.add(new Integer(currentPos));
				is.skip(pduLength - 10);
			}
		} catch (final Exception e) {
			System.out.println("end of input");
		}

		// Convert the list to an array. We may be able to get rid of a lot of
		// this with autoboxing, but that's 1.6 specific (I think.)
		final int[] buf = new int[indexes.size()];
		for (int idx = 0; idx < indexes.size(); idx++) {
			buf[idx] = indexes.get(idx).intValue();
		}

		return buf;

	}

	/**
	 * Ugh--this is memory inefficient and should be rewritten so that the PDU
	 * factory can simply take an input stream. This can probably be done via the
	 * MappedByteBuffer in nio.
	 * <p>
	 *
	 * Note that this will fail horribly if the length field is wrong, or if any one
	 * of the length fields before this was wrong.
	 * <p>
	 *
	 * This depends on the input stream being open and not reset through multiple
	 * calls to getNextPdu().
	 */
	public Pdu getNextPdu() {
		try {
			final byte[] initialBytes = new byte[10];
			is.read(initialBytes);

			short pduLength = 0;

			// The length field is a short at index 8 and 9. We need to mask
			// in order to remove any sign bits.
			final int ch1 = initialBytes[8] & 0x000000ff;
			final int ch2 = initialBytes[9] & 0x000000ff;
			pduLength = (short) ((ch1 << 8) + (ch2 << 0));
			// System.out.println("PduLength=" + pduLength);

			// We know how long the array to hold a single PDU is; now we
			// can create that array
			final byte[] fullArray = new byte[pduLength];

			// Copy in the starting bytes (which we've already read) and the
			// rest of the bytes for this PDU, which we have not.
			System.arraycopy(initialBytes, 0, fullArray, 0, 10);
			is.read(fullArray, 10, pduLength - 10);

			// And create a PDU from that
			final ByteBuffer bbuf = ByteBuffer.wrap(fullArray);
			return pduFactory.createPdu(bbuf);

		} catch (final Exception e) {
			System.out.println(e);
		}

		return null;
	}

}
