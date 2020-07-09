package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * information about a specific UA emmtter. Section 6.2.2.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AcousticEmitter extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** the system for a particular UA emitter, and an enumeration */
	protected int acousticSystemName;

	/** The function of the acoustic system */
	protected short acousticFunction;

	/** The UA emitter identification number relative to a specific system */
	protected short acousticIDNumber;

	/** Constructor */
	public AcousticEmitter() {
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

		if (!(obj instanceof AcousticEmitter))
			return false;

		final AcousticEmitter rhs = (AcousticEmitter) obj;

		if (!(acousticSystemName == rhs.acousticSystemName))
			ivarsEqual = false;
		if (!(acousticFunction == rhs.acousticFunction))
			ivarsEqual = false;
		if (!(acousticIDNumber == rhs.acousticIDNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getAcousticFunction() {
		return acousticFunction;
	}

	public short getAcousticIDNumber() {
		return acousticIDNumber;
	}

	public int getAcousticSystemName() {
		return acousticSystemName;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // acousticSystemName
		marshalSize = marshalSize + 1; // acousticFunction
		marshalSize = marshalSize + 1; // acousticIDNumber

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) acousticSystemName);
			dos.writeByte((byte) acousticFunction);
			dos.writeByte((byte) acousticIDNumber);
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
		buff.putShort((short) acousticSystemName);
		buff.put((byte) acousticFunction);
		buff.put((byte) acousticIDNumber);
	} // end of marshal method

	public void setAcousticFunction(final short pAcousticFunction) {
		acousticFunction = pAcousticFunction;
	}

	public void setAcousticIDNumber(final short pAcousticIDNumber) {
		acousticIDNumber = pAcousticIDNumber;
	}

	public void setAcousticSystemName(final int pAcousticSystemName) {
		acousticSystemName = pAcousticSystemName;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			acousticSystemName = dis.readUnsignedShort();
			acousticFunction = (short) dis.readUnsignedByte();
			acousticIDNumber = (short) dis.readUnsignedByte();
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
		acousticSystemName = buff.getShort() & 0xFFFF;
		acousticFunction = (short) (buff.get() & 0xFF);
		acousticIDNumber = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
