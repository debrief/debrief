package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * proivde the means to request a retransmit of a minefield data pdu. Section
 * 7.9.5 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MinefieldResponseNackPdu extends MinefieldFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Minefield ID */
	protected EntityID minefieldID = new EntityID();

	/** entity ID making the request */
	protected EntityID requestingEntityID = new EntityID();

	/** request ID */
	protected short requestID;

	/** how many pdus were missing */
	protected short numberOfMissingPdus;

	/** PDU sequence numbers that were missing */
	protected List<EightByteChunk> missingPduSequenceNumbers = new ArrayList<EightByteChunk>();

	/** Constructor */
	public MinefieldResponseNackPdu() {
		setPduType((short) 40);
	}

	/*
	 * The equals method doesn't always work--mostly it works only on classes that
	 * consist only of primitives. Be careful.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;

		return equalsImpl(obj);
	}

	@Override
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof MinefieldResponseNackPdu))
			return false;

		final MinefieldResponseNackPdu rhs = (MinefieldResponseNackPdu) obj;

		if (!(minefieldID.equals(rhs.minefieldID)))
			ivarsEqual = false;
		if (!(requestingEntityID.equals(rhs.requestingEntityID)))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(numberOfMissingPdus == rhs.numberOfMissingPdus))
			ivarsEqual = false;

		for (int idx = 0; idx < missingPduSequenceNumbers.size(); idx++) {
			if (!(missingPduSequenceNumbers.get(idx).equals(rhs.missingPduSequenceNumbers.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + minefieldID.getMarshalledSize(); // minefieldID
		marshalSize = marshalSize + requestingEntityID.getMarshalledSize(); // requestingEntityID
		marshalSize = marshalSize + 1; // requestID
		marshalSize = marshalSize + 1; // numberOfMissingPdus
		for (int idx = 0; idx < missingPduSequenceNumbers.size(); idx++) {
			final EightByteChunk listElement = missingPduSequenceNumbers.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityID getMinefieldID() {
		return minefieldID;
	}

	public List<EightByteChunk> getMissingPduSequenceNumbers() {
		return missingPduSequenceNumbers;
	}

	public short getNumberOfMissingPdus() {
		return (short) missingPduSequenceNumbers.size();
	}

	public short getRequestID() {
		return requestID;
	}

	public EntityID getRequestingEntityID() {
		return requestingEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			minefieldID.marshal(dos);
			requestingEntityID.marshal(dos);
			dos.writeByte((byte) requestID);
			dos.writeByte((byte) missingPduSequenceNumbers.size());

			for (int idx = 0; idx < missingPduSequenceNumbers.size(); idx++) {
				final EightByteChunk aEightByteChunk = missingPduSequenceNumbers.get(idx);
				aEightByteChunk.marshal(dos);
			} // end of list marshalling

		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of marshal method

	/**
	 * Packs a Pdu into the ByteBuffer.
	 *
	 * @throws java.nio.BufferOverflowException if buff is too small
	 * @throws java.nio.ReadOnlyBufferException if buff is read only
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin writing
	 * @since ??
	 */
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		minefieldID.marshal(buff);
		requestingEntityID.marshal(buff);
		buff.put((byte) requestID);
		buff.put((byte) missingPduSequenceNumbers.size());

		for (int idx = 0; idx < missingPduSequenceNumbers.size(); idx++) {
			final EightByteChunk aEightByteChunk = missingPduSequenceNumbers.get(idx);
			aEightByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setMinefieldID(final EntityID pMinefieldID) {
		minefieldID = pMinefieldID;
	}

	public void setMissingPduSequenceNumbers(final List<EightByteChunk> pMissingPduSequenceNumbers) {
		missingPduSequenceNumbers = pMissingPduSequenceNumbers;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfMissingPdus method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfMissingPdus(final short pNumberOfMissingPdus) {
		numberOfMissingPdus = pNumberOfMissingPdus;
	}

	public void setRequestID(final short pRequestID) {
		requestID = pRequestID;
	}

	public void setRequestingEntityID(final EntityID pRequestingEntityID) {
		requestingEntityID = pRequestingEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			minefieldID.unmarshal(dis);
			requestingEntityID.unmarshal(dis);
			requestID = (short) dis.readUnsignedByte();
			numberOfMissingPdus = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < numberOfMissingPdus; idx++) {
				final EightByteChunk anX = new EightByteChunk();
				anX.unmarshal(dis);
				missingPduSequenceNumbers.add(anX);
			}

		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of unmarshal method

	/**
	 * Unpacks a Pdu from the underlying data.
	 *
	 * @throws java.nio.BufferUnderflowException if buff is too small
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin reading
	 * @since ??
	 */
	@Override
	public void unmarshal(final java.nio.ByteBuffer buff) {
		super.unmarshal(buff);

		minefieldID.unmarshal(buff);
		requestingEntityID.unmarshal(buff);
		requestID = (short) (buff.get() & 0xFF);
		numberOfMissingPdus = (short) (buff.get() & 0xFF);
		for (int idx = 0; idx < numberOfMissingPdus; idx++) {
			final EightByteChunk anX = new EightByteChunk();
			anX.unmarshal(buff);
			missingPduSequenceNumbers.add(anX);
		}

	} // end of unmarshal method
} // end of class
