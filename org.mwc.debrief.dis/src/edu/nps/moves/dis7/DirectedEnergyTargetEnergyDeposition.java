package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * DE energy depostion properties for a target entity. Section 6.2.20.4
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DirectedEnergyTargetEnergyDeposition extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Unique ID of the target entity. */
	protected EntityID targetEntityID = new EntityID();

	/** padding */
	protected int padding = 0;

	/** Peak irrandiance */
	protected float peakIrradiance;

	/** Constructor */
	public DirectedEnergyTargetEnergyDeposition() {
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

		if (!(obj instanceof DirectedEnergyTargetEnergyDeposition))
			return false;

		final DirectedEnergyTargetEnergyDeposition rhs = (DirectedEnergyTargetEnergyDeposition) obj;

		if (!(targetEntityID.equals(rhs.targetEntityID)))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;
		if (!(peakIrradiance == rhs.peakIrradiance))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + targetEntityID.getMarshalledSize(); // targetEntityID
		marshalSize = marshalSize + 2; // padding
		marshalSize = marshalSize + 4; // peakIrradiance

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public float getPeakIrradiance() {
		return peakIrradiance;
	}

	public EntityID getTargetEntityID() {
		return targetEntityID;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			targetEntityID.marshal(dos);
			dos.writeShort((short) padding);
			dos.writeFloat(peakIrradiance);
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
		targetEntityID.marshal(buff);
		buff.putShort((short) padding);
		buff.putFloat(peakIrradiance);
	} // end of marshal method

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void setPeakIrradiance(final float pPeakIrradiance) {
		peakIrradiance = pPeakIrradiance;
	}

	public void setTargetEntityID(final EntityID pTargetEntityID) {
		targetEntityID = pTargetEntityID;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			targetEntityID.unmarshal(dis);
			padding = dis.readUnsignedShort();
			peakIrradiance = dis.readFloat();
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
		targetEntityID.unmarshal(buff);
		padding = buff.getShort() & 0xFFFF;
		peakIrradiance = buff.getFloat();
	} // end of unmarshal method
} // end of class
