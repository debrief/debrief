package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Shaft RPMs, used in underwater acoustic clacluations.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ShaftRPMs extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Current shaft RPMs */
	protected short currentShaftRPMs;

	/** ordered shaft rpms */
	protected short orderedShaftRPMs;

	/** rate of change of shaft RPMs */
	protected float shaftRPMRateOfChange;

	/** Constructor */
	public ShaftRPMs() {
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

		if (!(obj instanceof ShaftRPMs))
			return false;

		final ShaftRPMs rhs = (ShaftRPMs) obj;

		if (!(currentShaftRPMs == rhs.currentShaftRPMs))
			ivarsEqual = false;
		if (!(orderedShaftRPMs == rhs.orderedShaftRPMs))
			ivarsEqual = false;
		if (!(shaftRPMRateOfChange == rhs.shaftRPMRateOfChange))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getCurrentShaftRPMs() {
		return currentShaftRPMs;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // currentShaftRPMs
		marshalSize = marshalSize + 2; // orderedShaftRPMs
		marshalSize = marshalSize + 4; // shaftRPMRateOfChange

		return marshalSize;
	}

	public short getOrderedShaftRPMs() {
		return orderedShaftRPMs;
	}

	public float getShaftRPMRateOfChange() {
		return shaftRPMRateOfChange;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort(currentShaftRPMs);
			dos.writeShort(orderedShaftRPMs);
			dos.writeFloat(shaftRPMRateOfChange);
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
		buff.putShort(currentShaftRPMs);
		buff.putShort(orderedShaftRPMs);
		buff.putFloat(shaftRPMRateOfChange);
	} // end of marshal method

	public void setCurrentShaftRPMs(final short pCurrentShaftRPMs) {
		currentShaftRPMs = pCurrentShaftRPMs;
	}

	public void setOrderedShaftRPMs(final short pOrderedShaftRPMs) {
		orderedShaftRPMs = pOrderedShaftRPMs;
	}

	public void setShaftRPMRateOfChange(final float pShaftRPMRateOfChange) {
		shaftRPMRateOfChange = pShaftRPMRateOfChange;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			currentShaftRPMs = dis.readShort();
			orderedShaftRPMs = dis.readShort();
			shaftRPMRateOfChange = dis.readFloat();
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
		currentShaftRPMs = buff.getShort();
		orderedShaftRPMs = buff.getShort();
		shaftRPMRateOfChange = buff.getFloat();
	} // end of unmarshal method
} // end of class
