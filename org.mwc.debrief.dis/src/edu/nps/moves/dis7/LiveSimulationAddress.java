package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * A simulation's designation associated with all Live Entity IDs contained in
 * Live Entity PDUs. Section 6.2.55
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class LiveSimulationAddress extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * facility, installation, organizational unit or geographic location may have
	 * multiple sites associated with it. The Site Number is the first component of
	 * the Live Simulation Address, which defines a live simulation.
	 */
	protected short liveSiteNumber;

	/**
	 * An application associated with a live site is termed a live application. Each
	 * live application participating in an event
	 */
	protected short liveApplicationNumber;

	/** Constructor */
	public LiveSimulationAddress() {
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

		if (!(obj instanceof LiveSimulationAddress))
			return false;

		final LiveSimulationAddress rhs = (LiveSimulationAddress) obj;

		if (!(liveSiteNumber == rhs.liveSiteNumber))
			ivarsEqual = false;
		if (!(liveApplicationNumber == rhs.liveApplicationNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getLiveApplicationNumber() {
		return liveApplicationNumber;
	}

	public short getLiveSiteNumber() {
		return liveSiteNumber;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // liveSiteNumber
		marshalSize = marshalSize + 1; // liveApplicationNumber

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) liveSiteNumber);
			dos.writeByte((byte) liveApplicationNumber);
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
		buff.put((byte) liveSiteNumber);
		buff.put((byte) liveApplicationNumber);
	} // end of marshal method

	public void setLiveApplicationNumber(final short pLiveApplicationNumber) {
		liveApplicationNumber = pLiveApplicationNumber;
	}

	public void setLiveSiteNumber(final short pLiveSiteNumber) {
		liveSiteNumber = pLiveSiteNumber;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			liveSiteNumber = (short) dis.readUnsignedByte();
			liveApplicationNumber = (short) dis.readUnsignedByte();
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
		liveSiteNumber = (short) (buff.get() & 0xFF);
		liveApplicationNumber = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
