package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Information about the discrete positional relationship of the part entity
 * with respect to the its host entity Section 6.2.62
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class NamedLocationIdentification extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the station name within the host at which the part entity is located. If the
	 * part entity is On Station, this field shall specify the representation of the
	 * part’s location data fields. This field shall be specified by a 16-bit
	 * enumeration
	 */
	protected int stationName;

	/**
	 * the number of the particular wing station, cargo hold etc., at which the part
	 * is attached.
	 */
	protected int stationNumber;

	/** Constructor */
	public NamedLocationIdentification() {
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

		if (!(obj instanceof NamedLocationIdentification))
			return false;

		final NamedLocationIdentification rhs = (NamedLocationIdentification) obj;

		if (!(stationName == rhs.stationName))
			ivarsEqual = false;
		if (!(stationNumber == rhs.stationNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // stationName
		marshalSize = marshalSize + 2; // stationNumber

		return marshalSize;
	}

	public int getStationName() {
		return stationName;
	}

	public int getStationNumber() {
		return stationNumber;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) stationName);
			dos.writeShort((short) stationNumber);
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
		buff.putShort((short) stationName);
		buff.putShort((short) stationNumber);
	} // end of marshal method

	public void setStationName(final int pStationName) {
		stationName = pStationName;
	}

	public void setStationNumber(final int pStationNumber) {
		stationNumber = pStationNumber;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			stationName = dis.readUnsignedShort();
			stationNumber = dis.readUnsignedShort();
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
		stationName = buff.getShort() & 0xFFFF;
		stationNumber = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
