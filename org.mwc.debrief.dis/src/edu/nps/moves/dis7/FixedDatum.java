package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Fixed Datum Record. Section 6.2.38
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class FixedDatum extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the fixed datum, an enumeration */
	protected long fixedDatumID;

	/** Value for the fixed datum */
	protected long fixedDatumValue;

	/** Constructor */
	public FixedDatum() {
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

		if (!(obj instanceof FixedDatum))
			return false;

		final FixedDatum rhs = (FixedDatum) obj;

		if (!(fixedDatumID == rhs.fixedDatumID))
			ivarsEqual = false;
		if (!(fixedDatumValue == rhs.fixedDatumValue))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public long getFixedDatumID() {
		return fixedDatumID;
	}

	public long getFixedDatumValue() {
		return fixedDatumValue;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // fixedDatumID
		marshalSize = marshalSize + 4; // fixedDatumValue

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) fixedDatumID);
			dos.writeInt((int) fixedDatumValue);
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
		buff.putInt((int) fixedDatumID);
		buff.putInt((int) fixedDatumValue);
	} // end of marshal method

	public void setFixedDatumID(final long pFixedDatumID) {
		fixedDatumID = pFixedDatumID;
	}

	public void setFixedDatumValue(final long pFixedDatumValue) {
		fixedDatumValue = pFixedDatumValue;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			fixedDatumID = dis.readInt();
			fixedDatumValue = dis.readInt();
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
		fixedDatumID = buff.getInt();
		fixedDatumValue = buff.getInt();
	} // end of unmarshal method
} // end of class
