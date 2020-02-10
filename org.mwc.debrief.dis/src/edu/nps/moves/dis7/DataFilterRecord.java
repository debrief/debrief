package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * identify which of the optional data fields are contained in the Minefield
 * Data PDU or requested in the Minefield Query PDU. This is a 32-bit record.
 * For each field, true denotes that the data is requested or present and false
 * denotes that the data is neither requested nor present. Section 6.2.16
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DataFilterRecord extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** Bitflags field */
	protected long bitFlags;

	/** Constructor */
	public DataFilterRecord() {
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

		if (!(obj instanceof DataFilterRecord))
			return false;

		final DataFilterRecord rhs = (DataFilterRecord) obj;

		if (!(bitFlags == rhs.bitFlags))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public long getBitFlags() {
		return bitFlags;
	}

	/**
	 * boolean
	 */
	public int getBitFlags_fusing() {
		final long val = this.bitFlags & 0x100;
		return (int) (val >> 8);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_groundBurialDepthOffset() {
		final long val = this.bitFlags & 0x1;
		return (int) (val >> 0);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_mineEmplacementTime() {
		final long val = this.bitFlags & 0x40;
		return (int) (val >> 6);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_mineOrientation() {
		final long val = this.bitFlags & 0x8;
		return (int) (val >> 3);
	}

	/**
	 * padding
	 */
	public int getBitFlags_padding() {
		final long val = this.bitFlags & 0xff800;
		return (int) (val >> 11);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_paintScheme() {
		final long val = this.bitFlags & 0x400;
		return (int) (val >> 10);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_reflectance() {
		final long val = this.bitFlags & 0x20;
		return (int) (val >> 5);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_scalarDetectionCoefficient() {
		final long val = this.bitFlags & 0x200;
		return (int) (val >> 9);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_snowBurialDepthOffset() {
		final long val = this.bitFlags & 0x4;
		return (int) (val >> 2);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_thermalContrast() {
		final long val = this.bitFlags & 0x10;
		return (int) (val >> 4);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_tripDetonationWire() {
		final long val = this.bitFlags & 0x80;
		return (int) (val >> 7);
	}

	/**
	 * boolean
	 */
	public int getBitFlags_waterBurialDepthOffset() {
		final long val = this.bitFlags & 0x2;
		return (int) (val >> 1);
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // bitFlags

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) bitFlags);
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
		buff.putInt((int) bitFlags);
	} // end of marshal method

	public void setBitFlags(final long pBitFlags) {
		bitFlags = pBitFlags;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_fusing(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x100); // clear bits
		aVal = val << 8;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_groundBurialDepthOffset(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x1); // clear bits
		aVal = val << 0;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_mineEmplacementTime(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x40); // clear bits
		aVal = val << 6;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_mineOrientation(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x8); // clear bits
		aVal = val << 3;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * padding
	 */
	public void setBitFlags_padding(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0xff800); // clear bits
		aVal = val << 11;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_paintScheme(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x400); // clear bits
		aVal = val << 10;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_reflectance(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x20); // clear bits
		aVal = val << 5;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_scalarDetectionCoefficient(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x200); // clear bits
		aVal = val << 9;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_snowBurialDepthOffset(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x4); // clear bits
		aVal = val << 2;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_thermalContrast(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x10); // clear bits
		aVal = val << 4;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_tripDetonationWire(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x80); // clear bits
		aVal = val << 7;
		this.bitFlags = this.bitFlags | aVal;
	}

	/**
	 * boolean
	 */
	public void setBitFlags_waterBurialDepthOffset(final int val) {
		long aVal = 0;
		this.bitFlags &= (~0x2); // clear bits
		aVal = val << 1;
		this.bitFlags = this.bitFlags | aVal;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			bitFlags = dis.readInt();
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
		bitFlags = buff.getInt();
	} // end of unmarshal method
} // end of class
