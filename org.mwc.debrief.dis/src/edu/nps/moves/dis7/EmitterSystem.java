package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * This field shall specify information about a particular emitter system.
 * Section 6.2.23.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EmitterSystem extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Name of the emitter, 16 bit enumeration */
	protected int emitterName;

	/** function of the emitter, 8 bit enumeration */
	protected short emitterFunction;

	/** emitter ID, 8 bit enumeration */
	protected short emitterIDNumber;

	/** Constructor */
	public EmitterSystem() {
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

		if (!(obj instanceof EmitterSystem))
			return false;

		final EmitterSystem rhs = (EmitterSystem) obj;

		if (!(emitterName == rhs.emitterName))
			ivarsEqual = false;
		if (!(emitterFunction == rhs.emitterFunction))
			ivarsEqual = false;
		if (!(emitterIDNumber == rhs.emitterIDNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getEmitterFunction() {
		return emitterFunction;
	}

	public short getEmitterIDNumber() {
		return emitterIDNumber;
	}

	public int getEmitterName() {
		return emitterName;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // emitterName
		marshalSize = marshalSize + 1; // emitterFunction
		marshalSize = marshalSize + 1; // emitterIDNumber

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) emitterName);
			dos.writeByte((byte) emitterFunction);
			dos.writeByte((byte) emitterIDNumber);
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
		buff.putShort((short) emitterName);
		buff.put((byte) emitterFunction);
		buff.put((byte) emitterIDNumber);
	} // end of marshal method

	public void setEmitterFunction(final short pEmitterFunction) {
		emitterFunction = pEmitterFunction;
	}

	public void setEmitterIDNumber(final short pEmitterIDNumber) {
		emitterIDNumber = pEmitterIDNumber;
	}

	public void setEmitterName(final int pEmitterName) {
		emitterName = pEmitterName;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			emitterName = dis.readUnsignedShort();
			emitterFunction = (short) dis.readUnsignedByte();
			emitterIDNumber = (short) dis.readUnsignedByte();
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
		emitterName = buff.getShort() & 0xFFFF;
		emitterFunction = (short) (buff.get() & 0xFF);
		emitterIDNumber = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
