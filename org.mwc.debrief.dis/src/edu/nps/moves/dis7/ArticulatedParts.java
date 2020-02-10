package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * articulated parts for movable parts and a combination of moveable/attached
 * parts of an entity. Section 6.2.94.2
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ArticulatedParts extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** the identification of the Variable Parameter record. Enumeration from EBV */
	protected short recordType = (short) 0;

	/**
	 * indicate the change of any parameter for any articulated part. Starts at
	 * zero, incremented for each change
	 */
	protected short changeIndicator = (short) 0;

	/**
	 * the identification of the articulated part to which this articulation
	 * parameter is attached. This field shall be specified by a 16-bit unsigned
	 * integer. This field shall contain the value zero if the articulated part is
	 * attached directly to the entity.
	 */
	protected int partAttachedTo = 0;

	/** the type of parameter represented, 32 bit enumeration */
	protected long parameterType;

	/**
	 * The definition of the 64 bits shall be determined based on the type of
	 * parameter specified in the Parameter Type field
	 */
	protected long parameterValue;

	/** Constructor */
	public ArticulatedParts() {
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

		if (!(obj instanceof ArticulatedParts))
			return false;

		final ArticulatedParts rhs = (ArticulatedParts) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(changeIndicator == rhs.changeIndicator))
			ivarsEqual = false;
		if (!(partAttachedTo == rhs.partAttachedTo))
			ivarsEqual = false;
		if (!(parameterType == rhs.parameterType))
			ivarsEqual = false;
		if (!(parameterValue == rhs.parameterValue))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getChangeIndicator() {
		return changeIndicator;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // recordType
		marshalSize = marshalSize + 1; // changeIndicator
		marshalSize = marshalSize + 2; // partAttachedTo
		marshalSize = marshalSize + 4; // parameterType
		marshalSize = marshalSize + 8; // parameterValue

		return marshalSize;
	}

	public long getParameterType() {
		return parameterType;
	}

	public long getParameterValue() {
		return parameterValue;
	}

	public int getPartAttachedTo() {
		return partAttachedTo;
	}

	public short getRecordType() {
		return recordType;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) recordType);
			dos.writeByte((byte) changeIndicator);
			dos.writeShort((short) partAttachedTo);
			dos.writeInt((int) parameterType);
			dos.writeLong(parameterValue);
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
		buff.put((byte) recordType);
		buff.put((byte) changeIndicator);
		buff.putShort((short) partAttachedTo);
		buff.putInt((int) parameterType);
		buff.putLong(parameterValue);
	} // end of marshal method

	public void setChangeIndicator(final short pChangeIndicator) {
		changeIndicator = pChangeIndicator;
	}

	public void setParameterType(final long pParameterType) {
		parameterType = pParameterType;
	}

	public void setParameterValue(final long pParameterValue) {
		parameterValue = pParameterValue;
	}

	public void setPartAttachedTo(final int pPartAttachedTo) {
		partAttachedTo = pPartAttachedTo;
	}

	public void setRecordType(final short pRecordType) {
		recordType = pRecordType;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = (short) dis.readUnsignedByte();
			changeIndicator = (short) dis.readUnsignedByte();
			partAttachedTo = dis.readUnsignedShort();
			parameterType = dis.readInt();
			parameterValue = dis.readLong();
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
		recordType = (short) (buff.get() & 0xFF);
		changeIndicator = (short) (buff.get() & 0xFF);
		partAttachedTo = buff.getShort() & 0xFFFF;
		parameterType = buff.getInt();
		parameterValue = buff.getLong();
	} // end of unmarshal method
} // end of class
