package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Firing of a directed energy weapon shall be communicated by issuing a
 * Directed Energy Fire PDU Section 7.3.4 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DirectedEnergyFirePdu extends WarfareFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field shall identify the munition type enumeration for the DE weapon beam,
	 * Section 7.3.4
	 */
	protected EntityType munitionType = new EntityType();

	/**
	 * Field shall indicate the simulation time at start of the shot, Section 7.3.4
	 */
	protected ClockTime shotStartTime = new ClockTime();

	/**
	 * Field shall indicate the current cumulative duration of the shot, Section
	 * 7.3.4
	 */
	protected float commulativeShotTime;

	/**
	 * Field shall identify the location of the DE weapon aperture/emitter, Section
	 * 7.3.4
	 */
	protected Vector3Float ApertureEmitterLocation = new Vector3Float();

	/**
	 * Field shall identify the beam diameter at the aperture/emitter, Section 7.3.4
	 */
	protected float apertureDiameter;

	/**
	 * Field shall identify the emissions wavelength in units of meters, Section
	 * 7.3.4
	 */
	protected float wavelength;

	/**
	 * Field shall identify the current peak irradiance of emissions in units of
	 * Watts per square meter, Section 7.3.4
	 */
	protected float peakIrradiance;

	/**
	 * field shall identify the current pulse repetition frequency in units of
	 * cycles per second (Hertz), Section 7.3.4
	 */
	protected float pulseRepetitionFrequency;

	/**
	 * field shall identify the pulse width emissions in units of seconds, Section
	 * 7.3.4
	 */
	protected int pulseWidth;

	/**
	 * 16bit Boolean field shall contain various flags to indicate status
	 * information needed to process a DE, Section 7.3.4
	 */
	protected int flags;

	/**
	 * Field shall identify the pulse shape and shall be represented as an 8-bit
	 * enumeration, Section 7.3.4
	 */
	protected byte pulseShape;

	/** padding, Section 7.3.4 */
	protected short padding1;

	/** padding, Section 7.3.4 */
	protected long padding2;

	/** padding, Section 7.3.4 */
	protected int padding3;

	/** Field shall specify the number of DE records, Section 7.3.4 */
	protected int numberOfDERecords;

	/**
	 * Fields shall contain one or more DE records, records shall conform to the
	 * variable record format (Section6.2.82), Section 7.3.4
	 */
	protected List<StandardVariableSpecification> dERecords = new ArrayList<StandardVariableSpecification>();

	/** Constructor */
	public DirectedEnergyFirePdu() {
		setPduType((short) 68);
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

	@Override
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof DirectedEnergyFirePdu))
			return false;

		final DirectedEnergyFirePdu rhs = (DirectedEnergyFirePdu) obj;

		if (!(munitionType.equals(rhs.munitionType)))
			ivarsEqual = false;
		if (!(shotStartTime.equals(rhs.shotStartTime)))
			ivarsEqual = false;
		if (!(commulativeShotTime == rhs.commulativeShotTime))
			ivarsEqual = false;
		if (!(ApertureEmitterLocation.equals(rhs.ApertureEmitterLocation)))
			ivarsEqual = false;
		if (!(apertureDiameter == rhs.apertureDiameter))
			ivarsEqual = false;
		if (!(wavelength == rhs.wavelength))
			ivarsEqual = false;
		if (!(peakIrradiance == rhs.peakIrradiance))
			ivarsEqual = false;
		if (!(pulseRepetitionFrequency == rhs.pulseRepetitionFrequency))
			ivarsEqual = false;
		if (!(pulseWidth == rhs.pulseWidth))
			ivarsEqual = false;
		if (!(flags == rhs.flags))
			ivarsEqual = false;
		if (!(pulseShape == rhs.pulseShape))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(padding3 == rhs.padding3))
			ivarsEqual = false;
		if (!(numberOfDERecords == rhs.numberOfDERecords))
			ivarsEqual = false;

		for (int idx = 0; idx < dERecords.size(); idx++) {
			if (!(dERecords.get(idx).equals(rhs.dERecords.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public float getApertureDiameter() {
		return apertureDiameter;
	}

	public Vector3Float getApertureEmitterLocation() {
		return ApertureEmitterLocation;
	}

	public float getCommulativeShotTime() {
		return commulativeShotTime;
	}

	public List<StandardVariableSpecification> getDERecords() {
		return dERecords;
	}

	public int getFlags() {
		return flags;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + munitionType.getMarshalledSize(); // munitionType
		marshalSize = marshalSize + shotStartTime.getMarshalledSize(); // shotStartTime
		marshalSize = marshalSize + 4; // commulativeShotTime
		marshalSize = marshalSize + ApertureEmitterLocation.getMarshalledSize(); // ApertureEmitterLocation
		marshalSize = marshalSize + 4; // apertureDiameter
		marshalSize = marshalSize + 4; // wavelength
		marshalSize = marshalSize + 4; // peakIrradiance
		marshalSize = marshalSize + 4; // pulseRepetitionFrequency
		marshalSize = marshalSize + 4; // pulseWidth
		marshalSize = marshalSize + 4; // flags
		marshalSize = marshalSize + 1; // pulseShape
		marshalSize = marshalSize + 1; // padding1
		marshalSize = marshalSize + 4; // padding2
		marshalSize = marshalSize + 2; // padding3
		marshalSize = marshalSize + 2; // numberOfDERecords
		for (int idx = 0; idx < dERecords.size(); idx++) {
			final StandardVariableSpecification listElement = dERecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityType getMunitionType() {
		return munitionType;
	}

	public int getNumberOfDERecords() {
		return dERecords.size();
	}

	public short getPadding1() {
		return padding1;
	}

	public long getPadding2() {
		return padding2;
	}

	public int getPadding3() {
		return padding3;
	}

	public float getPeakIrradiance() {
		return peakIrradiance;
	}

	public float getPulseRepetitionFrequency() {
		return pulseRepetitionFrequency;
	}

	public byte getPulseShape() {
		return pulseShape;
	}

	public int getPulseWidth() {
		return pulseWidth;
	}

	public ClockTime getShotStartTime() {
		return shotStartTime;
	}

	public float getWavelength() {
		return wavelength;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			munitionType.marshal(dos);
			shotStartTime.marshal(dos);
			dos.writeFloat(commulativeShotTime);
			ApertureEmitterLocation.marshal(dos);
			dos.writeFloat(apertureDiameter);
			dos.writeFloat(wavelength);
			dos.writeFloat(peakIrradiance);
			dos.writeFloat(pulseRepetitionFrequency);
			dos.writeInt(pulseWidth);
			dos.writeInt(flags);
			dos.writeByte(pulseShape);
			dos.writeByte((byte) padding1);
			dos.writeInt((int) padding2);
			dos.writeShort((short) padding3);
			dos.writeShort((short) dERecords.size());

			for (int idx = 0; idx < dERecords.size(); idx++) {
				final StandardVariableSpecification aStandardVariableSpecification = dERecords.get(idx);
				aStandardVariableSpecification.marshal(dos);
			} // end of list marshalling

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
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		munitionType.marshal(buff);
		shotStartTime.marshal(buff);
		buff.putFloat(commulativeShotTime);
		ApertureEmitterLocation.marshal(buff);
		buff.putFloat(apertureDiameter);
		buff.putFloat(wavelength);
		buff.putFloat(peakIrradiance);
		buff.putFloat(pulseRepetitionFrequency);
		buff.putInt(pulseWidth);
		buff.putInt(flags);
		buff.put(pulseShape);
		buff.put((byte) padding1);
		buff.putInt((int) padding2);
		buff.putShort((short) padding3);
		buff.putShort((short) dERecords.size());

		for (int idx = 0; idx < dERecords.size(); idx++) {
			final StandardVariableSpecification aStandardVariableSpecification = dERecords.get(idx);
			aStandardVariableSpecification.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setApertureDiameter(final float pApertureDiameter) {
		apertureDiameter = pApertureDiameter;
	}

	public void setApertureEmitterLocation(final Vector3Float pApertureEmitterLocation) {
		ApertureEmitterLocation = pApertureEmitterLocation;
	}

	public void setCommulativeShotTime(final float pCommulativeShotTime) {
		commulativeShotTime = pCommulativeShotTime;
	}

	public void setDERecords(final List<StandardVariableSpecification> pDERecords) {
		dERecords = pDERecords;
	}

	public void setFlags(final int pFlags) {
		flags = pFlags;
	}

	public void setMunitionType(final EntityType pMunitionType) {
		munitionType = pMunitionType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfDERecords method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfDERecords(final int pNumberOfDERecords) {
		numberOfDERecords = pNumberOfDERecords;
	}

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final long pPadding2) {
		padding2 = pPadding2;
	}

	public void setPadding3(final int pPadding3) {
		padding3 = pPadding3;
	}

	public void setPeakIrradiance(final float pPeakIrradiance) {
		peakIrradiance = pPeakIrradiance;
	}

	public void setPulseRepetitionFrequency(final float pPulseRepetitionFrequency) {
		pulseRepetitionFrequency = pPulseRepetitionFrequency;
	}

	public void setPulseShape(final byte pPulseShape) {
		pulseShape = pPulseShape;
	}

	public void setPulseWidth(final int pPulseWidth) {
		pulseWidth = pPulseWidth;
	}

	public void setShotStartTime(final ClockTime pShotStartTime) {
		shotStartTime = pShotStartTime;
	}

	public void setWavelength(final float pWavelength) {
		wavelength = pWavelength;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			munitionType.unmarshal(dis);
			shotStartTime.unmarshal(dis);
			commulativeShotTime = dis.readFloat();
			ApertureEmitterLocation.unmarshal(dis);
			apertureDiameter = dis.readFloat();
			wavelength = dis.readFloat();
			peakIrradiance = dis.readFloat();
			pulseRepetitionFrequency = dis.readFloat();
			pulseWidth = dis.readInt();
			flags = dis.readInt();
			pulseShape = dis.readByte();
			padding1 = (short) dis.readUnsignedByte();
			padding2 = dis.readInt();
			padding3 = dis.readUnsignedShort();
			numberOfDERecords = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfDERecords; idx++) {
				final StandardVariableSpecification anX = new StandardVariableSpecification();
				anX.unmarshal(dis);
				dERecords.add(anX);
			}

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
	@Override
	public void unmarshal(final java.nio.ByteBuffer buff) {
		super.unmarshal(buff);

		munitionType.unmarshal(buff);
		shotStartTime.unmarshal(buff);
		commulativeShotTime = buff.getFloat();
		ApertureEmitterLocation.unmarshal(buff);
		apertureDiameter = buff.getFloat();
		wavelength = buff.getFloat();
		peakIrradiance = buff.getFloat();
		pulseRepetitionFrequency = buff.getFloat();
		pulseWidth = buff.getInt();
		flags = buff.getInt();
		pulseShape = buff.get();
		padding1 = (short) (buff.get() & 0xFF);
		padding2 = buff.getInt();
		padding3 = buff.getShort() & 0xFFFF;
		numberOfDERecords = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfDERecords; idx++) {
			final StandardVariableSpecification anX = new StandardVariableSpecification();
			anX.unmarshal(buff);
			dERecords.add(anX);
		}

	} // end of unmarshal method
} // end of class
