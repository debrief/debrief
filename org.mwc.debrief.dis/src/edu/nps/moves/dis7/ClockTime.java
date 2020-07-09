package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Time measurements that exceed one hour are represented by this record. The
 * first field is the hours since the unix epoch (Jan 1 1970, used by most Unix
 * systems and java) and the second field the timestamp units since the top of
 * the hour. Section 6.2.14
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ClockTime extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Hours since midnight, 1970, UTC */
	protected long hour;

	/** Time past the hour, in timestamp form */
	protected Timestamp timePastHour = new Timestamp();

	/** Constructor */
	public ClockTime() {
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

		if (!(obj instanceof ClockTime))
			return false;

		final ClockTime rhs = (ClockTime) obj;

		if (!(hour == rhs.hour))
			ivarsEqual = false;
		if (!(timePastHour.equals(rhs.timePastHour)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public long getHour() {
		return hour;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // hour
		marshalSize = marshalSize + timePastHour.getMarshalledSize(); // timePastHour

		return marshalSize;
	}

	public Timestamp getTimePastHour() {
		return timePastHour;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) hour);
			timePastHour.marshal(dos);
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
		buff.putInt((int) hour);
		timePastHour.marshal(buff);
	} // end of marshal method

	public void setHour(final long pHour) {
		hour = pHour;
	}

	public void setTimePastHour(final Timestamp pTimePastHour) {
		timePastHour = pTimePastHour;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			hour = dis.readInt();
			timePastHour.unmarshal(dis);
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
		hour = buff.getInt();
		timePastHour.unmarshal(buff);
	} // end of unmarshal method
} // end of class
