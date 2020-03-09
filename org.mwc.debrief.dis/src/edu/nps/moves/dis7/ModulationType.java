package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Information about the type of modulation used for radio transmission. 6.2.59
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ModulationType extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This field shall indicate the spread spectrum technique or combination of
	 * spread spectrum techniques in use. Bit field. 0=freq hopping, 1=psuedo noise,
	 * time hopping=2, reamining bits unused
	 */
	protected int spreadSpectrum;

	/** the major classification of the modulation type. */
	protected int majorModulation;

	/**
	 * provide certain detailed information depending upon the major modulation type
	 */
	protected int detail;

	/**
	 * the radio system associated with this Transmitter PDU and shall be used as
	 * the basis to interpret other fields whose values depend on a specific radio
	 * system.
	 */
	protected int radioSystem;

	/** Constructor */
	public ModulationType() {
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

		if (!(obj instanceof ModulationType))
			return false;

		final ModulationType rhs = (ModulationType) obj;

		if (!(spreadSpectrum == rhs.spreadSpectrum))
			ivarsEqual = false;
		if (!(majorModulation == rhs.majorModulation))
			ivarsEqual = false;
		if (!(detail == rhs.detail))
			ivarsEqual = false;
		if (!(radioSystem == rhs.radioSystem))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getDetail() {
		return detail;
	}

	public int getMajorModulation() {
		return majorModulation;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // spreadSpectrum
		marshalSize = marshalSize + 2; // majorModulation
		marshalSize = marshalSize + 2; // detail
		marshalSize = marshalSize + 2; // radioSystem

		return marshalSize;
	}

	public int getRadioSystem() {
		return radioSystem;
	}

	public int getSpreadSpectrum() {
		return spreadSpectrum;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) spreadSpectrum);
			dos.writeShort((short) majorModulation);
			dos.writeShort((short) detail);
			dos.writeShort((short) radioSystem);
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
		buff.putShort((short) spreadSpectrum);
		buff.putShort((short) majorModulation);
		buff.putShort((short) detail);
		buff.putShort((short) radioSystem);
	} // end of marshal method

	public void setDetail(final int pDetail) {
		detail = pDetail;
	}

	public void setMajorModulation(final int pMajorModulation) {
		majorModulation = pMajorModulation;
	}

	public void setRadioSystem(final int pRadioSystem) {
		radioSystem = pRadioSystem;
	}

	public void setSpreadSpectrum(final int pSpreadSpectrum) {
		spreadSpectrum = pSpreadSpectrum;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			spreadSpectrum = dis.readUnsignedShort();
			majorModulation = dis.readUnsignedShort();
			detail = dis.readUnsignedShort();
			radioSystem = dis.readUnsignedShort();
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
		spreadSpectrum = buff.getShort() & 0xFFFF;
		majorModulation = buff.getShort() & 0xFFFF;
		detail = buff.getShort() & 0xFFFF;
		radioSystem = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
