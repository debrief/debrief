package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Used in UA PDU
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ApaData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Index of APA parameter */
	protected int parameterIndex;

	/** Index of APA parameter */
	protected short parameterValue;

	/** Constructor */
	public ApaData() {
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

		if (!(obj instanceof ApaData))
			return false;

		final ApaData rhs = (ApaData) obj;

		if (!(parameterIndex == rhs.parameterIndex))
			ivarsEqual = false;
		if (!(parameterValue == rhs.parameterValue))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // parameterIndex
		marshalSize = marshalSize + 2; // parameterValue

		return marshalSize;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public short getParameterValue() {
		return parameterValue;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) parameterIndex);
			dos.writeShort(parameterValue);
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
		buff.putShort((short) parameterIndex);
		buff.putShort(parameterValue);
	} // end of marshal method

	public void setParameterIndex(final int pParameterIndex) {
		parameterIndex = pParameterIndex;
	}

	public void setParameterValue(final short pParameterValue) {
		parameterValue = pParameterValue;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			parameterIndex = dis.readUnsignedShort();
			parameterValue = dis.readShort();
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
		parameterIndex = buff.getShort() & 0xFFFF;
		parameterValue = buff.getShort();
	} // end of unmarshal method
} // end of class
