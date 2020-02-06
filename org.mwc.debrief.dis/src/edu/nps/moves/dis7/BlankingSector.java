package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * The Blanking Sector attribute record may be used to convey persistent areas
 * within a scan volume where emitter power for a specific active emitter beam
 * is reduced to an insignificant value. Section 6.2.21.2
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class BlankingSector extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected int recordType = 3500;

	protected int recordLength = 40;

	protected int padding = 0;

	protected short emitterNumber;

	protected short beamNumber;

	protected short stateIndicator;

	protected short padding2 = (short) 0;

	protected float leftAzimuth;

	protected float rightAzimuth;

	protected float lowerElevation;

	protected float upperElevation;

	protected float residualPower;

	protected int padding3 = 0;

	protected int padding4 = 0;

	/** Constructor */
	public BlankingSector() {
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

		if (!(obj instanceof BlankingSector))
			return false;

		final BlankingSector rhs = (BlankingSector) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(recordLength == rhs.recordLength))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;
		if (!(emitterNumber == rhs.emitterNumber))
			ivarsEqual = false;
		if (!(beamNumber == rhs.beamNumber))
			ivarsEqual = false;
		if (!(stateIndicator == rhs.stateIndicator))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(leftAzimuth == rhs.leftAzimuth))
			ivarsEqual = false;
		if (!(rightAzimuth == rhs.rightAzimuth))
			ivarsEqual = false;
		if (!(lowerElevation == rhs.lowerElevation))
			ivarsEqual = false;
		if (!(upperElevation == rhs.upperElevation))
			ivarsEqual = false;
		if (!(residualPower == rhs.residualPower))
			ivarsEqual = false;
		if (!(padding3 == rhs.padding3))
			ivarsEqual = false;
		if (!(padding4 == rhs.padding4))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getBeamNumber() {
		return beamNumber;
	}

	public short getEmitterNumber() {
		return emitterNumber;
	}

	public float getLeftAzimuth() {
		return leftAzimuth;
	}

	public float getLowerElevation() {
		return lowerElevation;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // recordType
		marshalSize = marshalSize + 2; // recordLength
		marshalSize = marshalSize + 2; // padding
		marshalSize = marshalSize + 1; // emitterNumber
		marshalSize = marshalSize + 1; // beamNumber
		marshalSize = marshalSize + 1; // stateIndicator
		marshalSize = marshalSize + 1; // padding2
		marshalSize = marshalSize + 4; // leftAzimuth
		marshalSize = marshalSize + 4; // rightAzimuth
		marshalSize = marshalSize + 4; // lowerElevation
		marshalSize = marshalSize + 4; // upperElevation
		marshalSize = marshalSize + 4; // residualPower
		marshalSize = marshalSize + 4; // padding3
		marshalSize = marshalSize + 4; // padding4

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public short getPadding2() {
		return padding2;
	}

	public int getPadding3() {
		return padding3;
	}

	public int getPadding4() {
		return padding4;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public int getRecordType() {
		return recordType;
	}

	public float getResidualPower() {
		return residualPower;
	}

	public float getRightAzimuth() {
		return rightAzimuth;
	}

	public short getStateIndicator() {
		return stateIndicator;
	}

	public float getUpperElevation() {
		return upperElevation;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt(recordType);
			dos.writeShort((short) recordLength);
			dos.writeShort((short) padding);
			dos.writeByte((byte) emitterNumber);
			dos.writeByte((byte) beamNumber);
			dos.writeByte((byte) stateIndicator);
			dos.writeByte((byte) padding2);
			dos.writeFloat(leftAzimuth);
			dos.writeFloat(rightAzimuth);
			dos.writeFloat(lowerElevation);
			dos.writeFloat(upperElevation);
			dos.writeFloat(residualPower);
			dos.writeInt(padding3);
			dos.writeInt(padding4);
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
		buff.putInt(recordType);
		buff.putShort((short) recordLength);
		buff.putShort((short) padding);
		buff.put((byte) emitterNumber);
		buff.put((byte) beamNumber);
		buff.put((byte) stateIndicator);
		buff.put((byte) padding2);
		buff.putFloat(leftAzimuth);
		buff.putFloat(rightAzimuth);
		buff.putFloat(lowerElevation);
		buff.putFloat(upperElevation);
		buff.putFloat(residualPower);
		buff.putInt(padding3);
		buff.putInt(padding4);
	} // end of marshal method

	public void setBeamNumber(final short pBeamNumber) {
		beamNumber = pBeamNumber;
	}

	public void setEmitterNumber(final short pEmitterNumber) {
		emitterNumber = pEmitterNumber;
	}

	public void setLeftAzimuth(final float pLeftAzimuth) {
		leftAzimuth = pLeftAzimuth;
	}

	public void setLowerElevation(final float pLowerElevation) {
		lowerElevation = pLowerElevation;
	}

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void setPadding2(final short pPadding2) {
		padding2 = pPadding2;
	}

	public void setPadding3(final int pPadding3) {
		padding3 = pPadding3;
	}

	public void setPadding4(final int pPadding4) {
		padding4 = pPadding4;
	}

	public void setRecordLength(final int pRecordLength) {
		recordLength = pRecordLength;
	}

	public void setRecordType(final int pRecordType) {
		recordType = pRecordType;
	}

	public void setResidualPower(final float pResidualPower) {
		residualPower = pResidualPower;
	}

	public void setRightAzimuth(final float pRightAzimuth) {
		rightAzimuth = pRightAzimuth;
	}

	public void setStateIndicator(final short pStateIndicator) {
		stateIndicator = pStateIndicator;
	}

	public void setUpperElevation(final float pUpperElevation) {
		upperElevation = pUpperElevation;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = dis.readInt();
			recordLength = dis.readUnsignedShort();
			padding = dis.readUnsignedShort();
			emitterNumber = (short) dis.readUnsignedByte();
			beamNumber = (short) dis.readUnsignedByte();
			stateIndicator = (short) dis.readUnsignedByte();
			padding2 = (short) dis.readUnsignedByte();
			leftAzimuth = dis.readFloat();
			rightAzimuth = dis.readFloat();
			lowerElevation = dis.readFloat();
			upperElevation = dis.readFloat();
			residualPower = dis.readFloat();
			padding3 = dis.readInt();
			padding4 = dis.readInt();
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
		recordType = buff.getInt();
		recordLength = buff.getShort() & 0xFFFF;
		padding = buff.getShort() & 0xFFFF;
		emitterNumber = (short) (buff.get() & 0xFF);
		beamNumber = (short) (buff.get() & 0xFF);
		stateIndicator = (short) (buff.get() & 0xFF);
		padding2 = (short) (buff.get() & 0xFF);
		leftAzimuth = buff.getFloat();
		rightAzimuth = buff.getFloat();
		lowerElevation = buff.getFloat();
		upperElevation = buff.getFloat();
		residualPower = buff.getFloat();
		padding3 = buff.getInt();
		padding4 = buff.getInt();
	} // end of unmarshal method
} // end of class
