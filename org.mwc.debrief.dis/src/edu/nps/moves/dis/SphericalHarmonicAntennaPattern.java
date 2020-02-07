package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.4.3. Used when the antenna pattern type in the transmitter pdu is
 * of value 2. Specified the direction and radiation pattern from a radio
 * transmitter's antenna. NOTE: this class must be hand-coded to clean up some
 * implementation details.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SphericalHarmonicAntennaPattern extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected byte harmonicOrder;

	/** Constructor */
	public SphericalHarmonicAntennaPattern() {
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

		if (!(obj instanceof SphericalHarmonicAntennaPattern))
			return false;

		final SphericalHarmonicAntennaPattern rhs = (SphericalHarmonicAntennaPattern) obj;

		if (!(harmonicOrder == rhs.harmonicOrder))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public byte getHarmonicOrder() {
		return harmonicOrder;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // harmonicOrder

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte(harmonicOrder);
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
		buff.put(harmonicOrder);
	} // end of marshal method

	public void setHarmonicOrder(final byte pHarmonicOrder) {
		harmonicOrder = pHarmonicOrder;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			harmonicOrder = dis.readByte();
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
		harmonicOrder = buff.get();
	} // end of unmarshal method
} // end of class
