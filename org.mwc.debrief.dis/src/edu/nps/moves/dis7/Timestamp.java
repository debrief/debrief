package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * LSB is absolute or relative timestamp. Scale is 2^31 - 1 divided into one
 * hour.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Timestamp extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** timestamp */
	protected long timestamp;

	/** Constructor */
	public Timestamp() {
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

		if (!(obj instanceof Timestamp))
			return false;

		final Timestamp rhs = (Timestamp) obj;

		if (!(timestamp == rhs.timestamp))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // timestamp

		return marshalSize;
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * 0 relative timestamp, 1 host synchronized timestamp
	 */
	public int getTimestamp_timestampType() {
		final long val = this.timestamp & 0x1;
		return (int) (val >> 0);
	}

	/**
	 * 2^31-1 per hour time units
	 */
	public int getTimestamp_timestampValue() {
		final long val = this.timestamp & 0xFE;
		return (int) (val >> 1);
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) timestamp);
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
		buff.putInt((int) timestamp);
	} // end of marshal method

	public void setTimestamp(final long pTimestamp) {
		timestamp = pTimestamp;
	}

	/**
	 * 0 relative timestamp, 1 host synchronized timestamp
	 */
	public void setTimestamp_timestampType(final int val) {
		long aVal = 0;
		this.timestamp &= (~0x1); // clear bits
		aVal = val << 0;
		this.timestamp = this.timestamp | aVal;
	}

	/**
	 * 2^31-1 per hour time units
	 */
	public void setTimestamp_timestampValue(final int val) {
		long aVal = 0;
		this.timestamp &= (~0xFE); // clear bits
		aVal = val << 1;
		this.timestamp = this.timestamp | aVal;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			timestamp = dis.readInt();
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
		timestamp = buff.getInt();
	} // end of unmarshal method
} // end of class
