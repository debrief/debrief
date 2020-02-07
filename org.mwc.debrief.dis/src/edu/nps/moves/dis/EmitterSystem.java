package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.11. This field shall specify information about a particular
 * emitter system
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
	protected short function;

	/** emitter ID, 8 bit enumeration */
	protected short emitterIdNumber;

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
		if (!(function == rhs.function))
			ivarsEqual = false;
		if (!(emitterIdNumber == rhs.emitterIdNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getEmitterIdNumber() {
		return emitterIdNumber;
	}

	public int getEmitterName() {
		return emitterName;
	}

	public short getFunction() {
		return function;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // emitterName
		marshalSize = marshalSize + 1; // function
		marshalSize = marshalSize + 1; // emitterIdNumber

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) emitterName);
			dos.writeByte((byte) function);
			dos.writeByte((byte) emitterIdNumber);
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
		buff.put((byte) function);
		buff.put((byte) emitterIdNumber);
	} // end of marshal method

	public void setEmitterIdNumber(final short pEmitterIdNumber) {
		emitterIdNumber = pEmitterIdNumber;
	}

	public void setEmitterName(final int pEmitterName) {
		emitterName = pEmitterName;
	}

	public void setFunction(final short pFunction) {
		function = pFunction;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			emitterName = dis.readUnsignedShort();
			function = (short) dis.readUnsignedByte();
			emitterIdNumber = (short) dis.readUnsignedByte();
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
		function = (short) (buff.get() & 0xFF);
		emitterIdNumber = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
