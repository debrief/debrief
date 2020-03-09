package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 64 bit piece of data
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EightByteChunk extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** Eight bytes of arbitrary data */
	protected byte[] otherParameters = new byte[8];

	/** Constructor */
	public EightByteChunk() {
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

		if (!(obj instanceof EightByteChunk))
			return false;

		final EightByteChunk rhs = (EightByteChunk) obj;

		for (int idx = 0; idx < 8; idx++) {
			if (!(otherParameters[idx] == rhs.otherParameters[idx]))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 8 * 1; // otherParameters

		return marshalSize;
	}

	public byte[] getOtherParameters() {
		return otherParameters;
	}

	public void marshal(final DataOutputStream dos) {
		try {

			for (int idx = 0; idx < otherParameters.length; idx++) {
				dos.writeByte(otherParameters[idx]);
			} // end of array marshaling

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

		for (int idx = 0; idx < otherParameters.length; idx++) {
			buff.put(otherParameters[idx]);
		} // end of array marshaling

	} // end of marshal method

	public void setOtherParameters(final byte[] pOtherParameters) {
		otherParameters = pOtherParameters;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			for (int idx = 0; idx < otherParameters.length; idx++) {
				otherParameters[idx] = dis.readByte();
			} // end of array unmarshaling
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
		for (int idx = 0; idx < otherParameters.length; idx++) {
			otherParameters[idx] = buff.get();
		} // end of array unmarshaling
	} // end of unmarshal method
} // end of class
