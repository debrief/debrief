package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Regeneration parameters for active emission systems that are variable
 * throughout a scenario. Section 6.2.91
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class UAFundamentalParameter extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Which database record shall be used. An enumeration from EBV document */
	protected int activeEmissionParameterIndex;

	/**
	 * The type of scan pattern, If not used, zero. An enumeration from EBV document
	 */
	protected int scanPattern;

	/** center azimuth bearing of th emain beam. In radians. */
	protected float beamCenterAzimuthHorizontal;

	/**
	 * Horizontal beamwidth of th emain beam Meastued at the 3dB down point of peak
	 * radiated power. In radians.
	 */
	protected float azimuthalBeamwidthHorizontal;

	/**
	 * center of the d/e angle of th emain beam relative to the stablised de angle
	 * of the target. In radians.
	 */
	protected float beamCenterDepressionElevation;

	/**
	 * vertical beamwidth of the main beam. Meastured at the 3dB down point of peak
	 * radiated power. In radians.
	 */
	protected float beamwidthDownElevation;

	/** Constructor */
	public UAFundamentalParameter() {
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

		if (!(obj instanceof UAFundamentalParameter))
			return false;

		final UAFundamentalParameter rhs = (UAFundamentalParameter) obj;

		if (!(activeEmissionParameterIndex == rhs.activeEmissionParameterIndex))
			ivarsEqual = false;
		if (!(scanPattern == rhs.scanPattern))
			ivarsEqual = false;
		if (!(beamCenterAzimuthHorizontal == rhs.beamCenterAzimuthHorizontal))
			ivarsEqual = false;
		if (!(azimuthalBeamwidthHorizontal == rhs.azimuthalBeamwidthHorizontal))
			ivarsEqual = false;
		if (!(beamCenterDepressionElevation == rhs.beamCenterDepressionElevation))
			ivarsEqual = false;
		if (!(beamwidthDownElevation == rhs.beamwidthDownElevation))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getActiveEmissionParameterIndex() {
		return activeEmissionParameterIndex;
	}

	public float getAzimuthalBeamwidthHorizontal() {
		return azimuthalBeamwidthHorizontal;
	}

	public float getBeamCenterAzimuthHorizontal() {
		return beamCenterAzimuthHorizontal;
	}

	public float getBeamCenterDepressionElevation() {
		return beamCenterDepressionElevation;
	}

	public float getBeamwidthDownElevation() {
		return beamwidthDownElevation;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // activeEmissionParameterIndex
		marshalSize = marshalSize + 2; // scanPattern
		marshalSize = marshalSize + 4; // beamCenterAzimuthHorizontal
		marshalSize = marshalSize + 4; // azimuthalBeamwidthHorizontal
		marshalSize = marshalSize + 4; // beamCenterDepressionElevation
		marshalSize = marshalSize + 4; // beamwidthDownElevation

		return marshalSize;
	}

	public int getScanPattern() {
		return scanPattern;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) activeEmissionParameterIndex);
			dos.writeShort((short) scanPattern);
			dos.writeFloat(beamCenterAzimuthHorizontal);
			dos.writeFloat(azimuthalBeamwidthHorizontal);
			dos.writeFloat(beamCenterDepressionElevation);
			dos.writeFloat(beamwidthDownElevation);
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
		buff.putShort((short) activeEmissionParameterIndex);
		buff.putShort((short) scanPattern);
		buff.putFloat(beamCenterAzimuthHorizontal);
		buff.putFloat(azimuthalBeamwidthHorizontal);
		buff.putFloat(beamCenterDepressionElevation);
		buff.putFloat(beamwidthDownElevation);
	} // end of marshal method

	public void setActiveEmissionParameterIndex(final int pActiveEmissionParameterIndex) {
		activeEmissionParameterIndex = pActiveEmissionParameterIndex;
	}

	public void setAzimuthalBeamwidthHorizontal(final float pAzimuthalBeamwidthHorizontal) {
		azimuthalBeamwidthHorizontal = pAzimuthalBeamwidthHorizontal;
	}

	public void setBeamCenterAzimuthHorizontal(final float pBeamCenterAzimuthHorizontal) {
		beamCenterAzimuthHorizontal = pBeamCenterAzimuthHorizontal;
	}

	public void setBeamCenterDepressionElevation(final float pBeamCenterDepressionElevation) {
		beamCenterDepressionElevation = pBeamCenterDepressionElevation;
	}

	public void setBeamwidthDownElevation(final float pBeamwidthDownElevation) {
		beamwidthDownElevation = pBeamwidthDownElevation;
	}

	public void setScanPattern(final int pScanPattern) {
		scanPattern = pScanPattern;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			activeEmissionParameterIndex = dis.readUnsignedShort();
			scanPattern = dis.readUnsignedShort();
			beamCenterAzimuthHorizontal = dis.readFloat();
			azimuthalBeamwidthHorizontal = dis.readFloat();
			beamCenterDepressionElevation = dis.readFloat();
			beamwidthDownElevation = dis.readFloat();
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
		activeEmissionParameterIndex = buff.getShort() & 0xFFFF;
		scanPattern = buff.getShort() & 0xFFFF;
		beamCenterAzimuthHorizontal = buff.getFloat();
		azimuthalBeamwidthHorizontal = buff.getFloat();
		beamCenterDepressionElevation = buff.getFloat();
		beamwidthDownElevation = buff.getFloat();
	} // end of unmarshal method
} // end of class
