package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Used in UA PDU
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AcousticBeamData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** beam data length */
	protected int beamDataLength;

	/** beamIDNumber */
	protected short beamIDNumber;

	/** padding */
	protected int pad2;

	/** fundamental data parameters */
	protected AcousticBeamFundamentalParameter fundamentalDataParameters = new AcousticBeamFundamentalParameter();

	/** Constructor */
	public AcousticBeamData() {
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

		if (!(obj instanceof AcousticBeamData))
			return false;

		final AcousticBeamData rhs = (AcousticBeamData) obj;

		if (!(beamDataLength == rhs.beamDataLength))
			ivarsEqual = false;
		if (!(beamIDNumber == rhs.beamIDNumber))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(fundamentalDataParameters.equals(rhs.fundamentalDataParameters)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getBeamDataLength() {
		return beamDataLength;
	}

	public short getBeamIDNumber() {
		return beamIDNumber;
	}

	public AcousticBeamFundamentalParameter getFundamentalDataParameters() {
		return fundamentalDataParameters;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // beamDataLength
		marshalSize = marshalSize + 1; // beamIDNumber
		marshalSize = marshalSize + 2; // pad2
		marshalSize = marshalSize + fundamentalDataParameters.getMarshalledSize(); // fundamentalDataParameters

		return marshalSize;
	}

	public int getPad2() {
		return pad2;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) beamDataLength);
			dos.writeByte((byte) beamIDNumber);
			dos.writeShort((short) pad2);
			fundamentalDataParameters.marshal(dos);
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
		buff.putShort((short) beamDataLength);
		buff.put((byte) beamIDNumber);
		buff.putShort((short) pad2);
		fundamentalDataParameters.marshal(buff);
	} // end of marshal method

	public void setBeamDataLength(final int pBeamDataLength) {
		beamDataLength = pBeamDataLength;
	}

	public void setBeamIDNumber(final short pBeamIDNumber) {
		beamIDNumber = pBeamIDNumber;
	}

	public void setFundamentalDataParameters(final AcousticBeamFundamentalParameter pFundamentalDataParameters) {
		fundamentalDataParameters = pFundamentalDataParameters;
	}

	public void setPad2(final int pPad2) {
		pad2 = pPad2;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			beamDataLength = dis.readUnsignedShort();
			beamIDNumber = (short) dis.readUnsignedByte();
			pad2 = dis.readUnsignedShort();
			fundamentalDataParameters.unmarshal(dis);
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
		beamDataLength = buff.getShort() & 0xFFFF;
		beamIDNumber = (short) (buff.get() & 0xFF);
		pad2 = buff.getShort() & 0xFFFF;
		fundamentalDataParameters.unmarshal(buff);
	} // end of unmarshal method
} // end of class
