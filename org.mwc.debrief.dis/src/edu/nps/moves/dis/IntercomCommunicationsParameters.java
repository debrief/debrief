package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 5.2.46. Intercom communcations parameters
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IntercomCommunicationsParameters extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Type of intercom parameters record */
	protected int recordType;

	/** length of record-specifid field, in octets */
	protected int recordLength;

	/** variable length variablelist of data parameters */
	protected List<OneByteChunk> parameterValues = new ArrayList<OneByteChunk>();

	/** Constructor */
	public IntercomCommunicationsParameters() {
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

	/**
	 * Compare all fields that contribute to the state, ignoring transient and
	 * static fields, for <code>this</code> and the supplied object
	 *
	 * @param obj the object to compare to
	 * @return true if the objects are equal, false otherwise.
	 */
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof IntercomCommunicationsParameters))
			return false;

		final IntercomCommunicationsParameters rhs = (IntercomCommunicationsParameters) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(recordLength == rhs.recordLength))
			ivarsEqual = false;

		for (int idx = 0; idx < parameterValues.size(); idx++) {
			if (!(parameterValues.get(idx).equals(rhs.parameterValues.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // recordType
		marshalSize = marshalSize + 2; // recordLength
		for (int idx = 0; idx < parameterValues.size(); idx++) {
			final OneByteChunk listElement = parameterValues.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public List<OneByteChunk> getParameterValues() {
		return parameterValues;
	}

	public int getRecordLength() {
		return parameterValues.size();
	}

	public int getRecordType() {
		return recordType;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) recordType);
			dos.writeShort((short) parameterValues.size());

			for (int idx = 0; idx < parameterValues.size(); idx++) {
				final OneByteChunk aOneByteChunk = parameterValues.get(idx);
				aOneByteChunk.marshal(dos);
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
	public void marshal(final java.nio.ByteBuffer buff) {
		buff.putShort((short) recordType);
		buff.putShort((short) parameterValues.size());

		for (int idx = 0; idx < parameterValues.size(); idx++) {
			final OneByteChunk aOneByteChunk = parameterValues.get(idx);
			aOneByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setParameterValues(final List<OneByteChunk> pParameterValues) {
		parameterValues = pParameterValues;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getrecordLength
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setRecordLength(final int pRecordLength) {
		recordLength = pRecordLength;
	}

	public void setRecordType(final int pRecordType) {
		recordType = pRecordType;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = dis.readUnsignedShort();
			recordLength = dis.readUnsignedShort();
			for (int idx = 0; idx < recordLength; idx++) {
				final OneByteChunk anX = new OneByteChunk();
				anX.unmarshal(dis);
				parameterValues.add(anX);
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
	public void unmarshal(final java.nio.ByteBuffer buff) {
		recordType = buff.getShort() & 0xFFFF;
		recordLength = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < recordLength; idx++) {
			final OneByteChunk anX = new OneByteChunk();
			anX.unmarshal(buff);
			parameterValues.add(anX);
		}

	} // end of unmarshal method
} // end of class
