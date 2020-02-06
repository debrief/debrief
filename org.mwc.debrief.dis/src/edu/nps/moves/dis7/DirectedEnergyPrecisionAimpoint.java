package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * DE Precision Aimpoint Record. Section 6.2.20.3
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DirectedEnergyPrecisionAimpoint extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Type of Record */
	protected long recordType = 4000;

	/** Length of Record */
	protected int recordLength = 88;

	/** Padding */
	protected int padding = 0;

	/** Position of Target Spot in World Coordinates. */
	protected Vector3Double targetSpotLocation = new Vector3Double();

	/** Position (meters) of Target Spot relative to Entity Position. */
	protected Vector3Float targetSpotEntityLocation = new Vector3Float();

	/** Velocity (meters/sec) of Target Spot. */
	protected Vector3Float targetSpotVelocity = new Vector3Float();

	/** Acceleration (meters/sec/sec) of Target Spot. */
	protected Vector3Float targetSpotAcceleration = new Vector3Float();

	/** Unique ID of the target entity. */
	protected EntityID targetEntityID = new EntityID();

	/** Target Component ID ENUM, same as in DamageDescriptionRecord. */
	protected short targetComponentID = (short) 0;

	/** Spot Shape ENUM. */
	protected short beamSpotType = (short) 0;

	/** Beam Spot Cross Section Semi-Major Axis. */
	protected float beamSpotCrossSectionSemiMajorAxis = 0;

	/** Beam Spot Cross Section Semi-Major Axis. */
	protected float beamSpotCrossSectionSemiMinorAxis = 0;

	/** Beam Spot Cross Section Orientation Angle. */
	protected float beamSpotCrossSectionOrientationAngle = 0;

	/** Peak irradiance */
	protected float peakIrradiance = 0;

	/** padding */
	protected long padding2 = 0;

	/** Constructor */
	public DirectedEnergyPrecisionAimpoint() {
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

		if (!(obj instanceof DirectedEnergyPrecisionAimpoint))
			return false;

		final DirectedEnergyPrecisionAimpoint rhs = (DirectedEnergyPrecisionAimpoint) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(recordLength == rhs.recordLength))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;
		if (!(targetSpotLocation.equals(rhs.targetSpotLocation)))
			ivarsEqual = false;
		if (!(targetSpotEntityLocation.equals(rhs.targetSpotEntityLocation)))
			ivarsEqual = false;
		if (!(targetSpotVelocity.equals(rhs.targetSpotVelocity)))
			ivarsEqual = false;
		if (!(targetSpotAcceleration.equals(rhs.targetSpotAcceleration)))
			ivarsEqual = false;
		if (!(targetEntityID.equals(rhs.targetEntityID)))
			ivarsEqual = false;
		if (!(targetComponentID == rhs.targetComponentID))
			ivarsEqual = false;
		if (!(beamSpotType == rhs.beamSpotType))
			ivarsEqual = false;
		if (!(beamSpotCrossSectionSemiMajorAxis == rhs.beamSpotCrossSectionSemiMajorAxis))
			ivarsEqual = false;
		if (!(beamSpotCrossSectionSemiMinorAxis == rhs.beamSpotCrossSectionSemiMinorAxis))
			ivarsEqual = false;
		if (!(beamSpotCrossSectionOrientationAngle == rhs.beamSpotCrossSectionOrientationAngle))
			ivarsEqual = false;
		if (!(peakIrradiance == rhs.peakIrradiance))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getBeamSpotCrossSectionOrientationAngle() {
		return beamSpotCrossSectionOrientationAngle;
	}

	public float getBeamSpotCrossSectionSemiMajorAxis() {
		return beamSpotCrossSectionSemiMajorAxis;
	}

	public float getBeamSpotCrossSectionSemiMinorAxis() {
		return beamSpotCrossSectionSemiMinorAxis;
	}

	public short getBeamSpotType() {
		return beamSpotType;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // recordType
		marshalSize = marshalSize + 2; // recordLength
		marshalSize = marshalSize + 2; // padding
		marshalSize = marshalSize + targetSpotLocation.getMarshalledSize(); // targetSpotLocation
		marshalSize = marshalSize + targetSpotEntityLocation.getMarshalledSize(); // targetSpotEntityLocation
		marshalSize = marshalSize + targetSpotVelocity.getMarshalledSize(); // targetSpotVelocity
		marshalSize = marshalSize + targetSpotAcceleration.getMarshalledSize(); // targetSpotAcceleration
		marshalSize = marshalSize + targetEntityID.getMarshalledSize(); // targetEntityID
		marshalSize = marshalSize + 1; // targetComponentID
		marshalSize = marshalSize + 1; // beamSpotType
		marshalSize = marshalSize + 4; // beamSpotCrossSectionSemiMajorAxis
		marshalSize = marshalSize + 4; // beamSpotCrossSectionSemiMinorAxis
		marshalSize = marshalSize + 4; // beamSpotCrossSectionOrientationAngle
		marshalSize = marshalSize + 4; // peakIrradiance
		marshalSize = marshalSize + 4; // padding2

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public long getPadding2() {
		return padding2;
	}

	public float getPeakIrradiance() {
		return peakIrradiance;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public long getRecordType() {
		return recordType;
	}

	public short getTargetComponentID() {
		return targetComponentID;
	}

	public EntityID getTargetEntityID() {
		return targetEntityID;
	}

	public Vector3Float getTargetSpotAcceleration() {
		return targetSpotAcceleration;
	}

	public Vector3Float getTargetSpotEntityLocation() {
		return targetSpotEntityLocation;
	}

	public Vector3Double getTargetSpotLocation() {
		return targetSpotLocation;
	}

	public Vector3Float getTargetSpotVelocity() {
		return targetSpotVelocity;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) recordType);
			dos.writeShort((short) recordLength);
			dos.writeShort((short) padding);
			targetSpotLocation.marshal(dos);
			targetSpotEntityLocation.marshal(dos);
			targetSpotVelocity.marshal(dos);
			targetSpotAcceleration.marshal(dos);
			targetEntityID.marshal(dos);
			dos.writeByte((byte) targetComponentID);
			dos.writeByte((byte) beamSpotType);
			dos.writeFloat(beamSpotCrossSectionSemiMajorAxis);
			dos.writeFloat(beamSpotCrossSectionSemiMinorAxis);
			dos.writeFloat(beamSpotCrossSectionOrientationAngle);
			dos.writeFloat(peakIrradiance);
			dos.writeInt((int) padding2);
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
		buff.putInt((int) recordType);
		buff.putShort((short) recordLength);
		buff.putShort((short) padding);
		targetSpotLocation.marshal(buff);
		targetSpotEntityLocation.marshal(buff);
		targetSpotVelocity.marshal(buff);
		targetSpotAcceleration.marshal(buff);
		targetEntityID.marshal(buff);
		buff.put((byte) targetComponentID);
		buff.put((byte) beamSpotType);
		buff.putFloat(beamSpotCrossSectionSemiMajorAxis);
		buff.putFloat(beamSpotCrossSectionSemiMinorAxis);
		buff.putFloat(beamSpotCrossSectionOrientationAngle);
		buff.putFloat(peakIrradiance);
		buff.putInt((int) padding2);
	} // end of marshal method

	public void setBeamSpotCrossSectionOrientationAngle(final float pBeamSpotCrossSectionOrientationAngle) {
		beamSpotCrossSectionOrientationAngle = pBeamSpotCrossSectionOrientationAngle;
	}

	public void setBeamSpotCrossSectionSemiMajorAxis(final float pBeamSpotCrossSectionSemiMajorAxis) {
		beamSpotCrossSectionSemiMajorAxis = pBeamSpotCrossSectionSemiMajorAxis;
	}

	public void setBeamSpotCrossSectionSemiMinorAxis(final float pBeamSpotCrossSectionSemiMinorAxis) {
		beamSpotCrossSectionSemiMinorAxis = pBeamSpotCrossSectionSemiMinorAxis;
	}

	public void setBeamSpotType(final short pBeamSpotType) {
		beamSpotType = pBeamSpotType;
	}

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void setPadding2(final long pPadding2) {
		padding2 = pPadding2;
	}

	public void setPeakIrradiance(final float pPeakIrradiance) {
		peakIrradiance = pPeakIrradiance;
	}

	public void setRecordLength(final int pRecordLength) {
		recordLength = pRecordLength;
	}

	public void setRecordType(final long pRecordType) {
		recordType = pRecordType;
	}

	public void setTargetComponentID(final short pTargetComponentID) {
		targetComponentID = pTargetComponentID;
	}

	public void setTargetEntityID(final EntityID pTargetEntityID) {
		targetEntityID = pTargetEntityID;
	}

	public void setTargetSpotAcceleration(final Vector3Float pTargetSpotAcceleration) {
		targetSpotAcceleration = pTargetSpotAcceleration;
	}

	public void setTargetSpotEntityLocation(final Vector3Float pTargetSpotEntityLocation) {
		targetSpotEntityLocation = pTargetSpotEntityLocation;
	}

	public void setTargetSpotLocation(final Vector3Double pTargetSpotLocation) {
		targetSpotLocation = pTargetSpotLocation;
	}

	public void setTargetSpotVelocity(final Vector3Float pTargetSpotVelocity) {
		targetSpotVelocity = pTargetSpotVelocity;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = dis.readInt();
			recordLength = dis.readUnsignedShort();
			padding = dis.readUnsignedShort();
			targetSpotLocation.unmarshal(dis);
			targetSpotEntityLocation.unmarshal(dis);
			targetSpotVelocity.unmarshal(dis);
			targetSpotAcceleration.unmarshal(dis);
			targetEntityID.unmarshal(dis);
			targetComponentID = (short) dis.readUnsignedByte();
			beamSpotType = (short) dis.readUnsignedByte();
			beamSpotCrossSectionSemiMajorAxis = dis.readFloat();
			beamSpotCrossSectionSemiMinorAxis = dis.readFloat();
			beamSpotCrossSectionOrientationAngle = dis.readFloat();
			peakIrradiance = dis.readFloat();
			padding2 = dis.readInt();
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
		targetSpotLocation.unmarshal(buff);
		targetSpotEntityLocation.unmarshal(buff);
		targetSpotVelocity.unmarshal(buff);
		targetSpotAcceleration.unmarshal(buff);
		targetEntityID.unmarshal(buff);
		targetComponentID = (short) (buff.get() & 0xFF);
		beamSpotType = (short) (buff.get() & 0xFF);
		beamSpotCrossSectionSemiMajorAxis = buff.getFloat();
		beamSpotCrossSectionSemiMinorAxis = buff.getFloat();
		beamSpotCrossSectionOrientationAngle = buff.getFloat();
		peakIrradiance = buff.getFloat();
		padding2 = buff.getInt();
	} // end of unmarshal method
} // end of class
