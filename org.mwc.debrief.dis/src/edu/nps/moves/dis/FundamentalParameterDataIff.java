package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.2.45. Fundamental IFF atc data
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class FundamentalParameterDataIff extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ERP */
	protected float erp;

	/** frequency */
	protected float frequency;

	/** pgrf */
	protected float pgrf;

	/** Pulse width */
	protected float pulseWidth;

	/** Burst length */
	protected long burstLength;

	/** Applicable modes enumeration */
	protected short applicableModes;

	/** padding */
	protected int pad2;

	/** padding */
	protected short pad3;

	/** Constructor */
	public FundamentalParameterDataIff() {
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

		if (!(obj instanceof FundamentalParameterDataIff))
			return false;

		final FundamentalParameterDataIff rhs = (FundamentalParameterDataIff) obj;

		if (!(erp == rhs.erp))
			ivarsEqual = false;
		if (!(frequency == rhs.frequency))
			ivarsEqual = false;
		if (!(pgrf == rhs.pgrf))
			ivarsEqual = false;
		if (!(pulseWidth == rhs.pulseWidth))
			ivarsEqual = false;
		if (!(burstLength == rhs.burstLength))
			ivarsEqual = false;
		if (!(applicableModes == rhs.applicableModes))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(pad3 == rhs.pad3))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getApplicableModes() {
		return applicableModes;
	}

	public long getBurstLength() {
		return burstLength;
	}

	public float getErp() {
		return erp;
	}

	public float getFrequency() {
		return frequency;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // erp
		marshalSize = marshalSize + 4; // frequency
		marshalSize = marshalSize + 4; // pgrf
		marshalSize = marshalSize + 4; // pulseWidth
		marshalSize = marshalSize + 4; // burstLength
		marshalSize = marshalSize + 1; // applicableModes
		marshalSize = marshalSize + 2; // pad2
		marshalSize = marshalSize + 1; // pad3

		return marshalSize;
	}

	public int getPad2() {
		return pad2;
	}

	public short getPad3() {
		return pad3;
	}

	public float getPgrf() {
		return pgrf;
	}

	public float getPulseWidth() {
		return pulseWidth;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(erp);
			dos.writeFloat(frequency);
			dos.writeFloat(pgrf);
			dos.writeFloat(pulseWidth);
			dos.writeInt((int) burstLength);
			dos.writeByte((byte) applicableModes);
			dos.writeShort((short) pad2);
			dos.writeByte((byte) pad3);
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
		buff.putFloat(erp);
		buff.putFloat(frequency);
		buff.putFloat(pgrf);
		buff.putFloat(pulseWidth);
		buff.putInt((int) burstLength);
		buff.put((byte) applicableModes);
		buff.putShort((short) pad2);
		buff.put((byte) pad3);
	} // end of marshal method

	public void setApplicableModes(final short pApplicableModes) {
		applicableModes = pApplicableModes;
	}

	public void setBurstLength(final long pBurstLength) {
		burstLength = pBurstLength;
	}

	public void setErp(final float pErp) {
		erp = pErp;
	}

	public void setFrequency(final float pFrequency) {
		frequency = pFrequency;
	}

	public void setPad2(final int pPad2) {
		pad2 = pPad2;
	}

	public void setPad3(final short pPad3) {
		pad3 = pPad3;
	}

	public void setPgrf(final float pPgrf) {
		pgrf = pPgrf;
	}

	public void setPulseWidth(final float pPulseWidth) {
		pulseWidth = pPulseWidth;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			erp = dis.readFloat();
			frequency = dis.readFloat();
			pgrf = dis.readFloat();
			pulseWidth = dis.readFloat();
			burstLength = dis.readInt();
			applicableModes = (short) dis.readUnsignedByte();
			pad2 = dis.readUnsignedShort();
			pad3 = (short) dis.readUnsignedByte();
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
		erp = buff.getFloat();
		frequency = buff.getFloat();
		pgrf = buff.getFloat();
		pulseWidth = buff.getFloat();
		burstLength = buff.getInt();
		applicableModes = (short) (buff.get() & 0xFF);
		pad2 = buff.getShort() & 0xFFFF;
		pad3 = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
