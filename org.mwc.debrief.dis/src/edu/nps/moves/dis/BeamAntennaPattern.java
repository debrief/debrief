package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.4.2. Used when the antenna pattern type field has a value of 1.
 * Specifies the direction, patter, and polarization of radiation from an
 * antenna.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class BeamAntennaPattern extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The rotation that transformst he reference coordinate sytem into the beam
	 * coordinate system. Either world coordinates or entity coordinates may be used
	 * as the reference coordinate system, as specified by teh reference system
	 * field of the antenna pattern record.
	 */
	protected Orientation beamDirection = new Orientation();

	protected float azimuthBeamwidth = 0;

	protected float elevationBeamwidth = 0;

	protected float referenceSystem = 0;

	protected short padding1 = (short) 0;

	protected byte padding2 = (byte) 0;

	/**
	 * Magnigute of the z-component in beam coordinates at some arbitrary single
	 * point in the mainbeam and in the far field of the antenna.
	 */
	protected float ez;

	/**
	 * Magnigute of the x-component in beam coordinates at some arbitrary single
	 * point in the mainbeam and in the far field of the antenna.
	 */
	protected float ex;

	/** THe phase angle between Ez and Ex in radians. */
	protected float phase;

	/** Constructor */
	public BeamAntennaPattern() {
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

		if (!(obj instanceof BeamAntennaPattern))
			return false;

		final BeamAntennaPattern rhs = (BeamAntennaPattern) obj;

		if (!(beamDirection.equals(rhs.beamDirection)))
			ivarsEqual = false;
		if (!(azimuthBeamwidth == rhs.azimuthBeamwidth))
			ivarsEqual = false;
		if (!(elevationBeamwidth == rhs.elevationBeamwidth))
			ivarsEqual = false;
		if (!(referenceSystem == rhs.referenceSystem))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(ez == rhs.ez))
			ivarsEqual = false;
		if (!(ex == rhs.ex))
			ivarsEqual = false;
		if (!(phase == rhs.phase))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getAzimuthBeamwidth() {
		return azimuthBeamwidth;
	}

	public Orientation getBeamDirection() {
		return beamDirection;
	}

	public float getElevationBeamwidth() {
		return elevationBeamwidth;
	}

	public float getEx() {
		return ex;
	}

	public float getEz() {
		return ez;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + beamDirection.getMarshalledSize(); // beamDirection
		marshalSize = marshalSize + 4; // azimuthBeamwidth
		marshalSize = marshalSize + 4; // elevationBeamwidth
		marshalSize = marshalSize + 4; // referenceSystem
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 1; // padding2
		marshalSize = marshalSize + 4; // ez
		marshalSize = marshalSize + 4; // ex
		marshalSize = marshalSize + 4; // phase

		return marshalSize;
	}

	public short getPadding1() {
		return padding1;
	}

	public byte getPadding2() {
		return padding2;
	}

	public float getPhase() {
		return phase;
	}

	public float getReferenceSystem() {
		return referenceSystem;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			beamDirection.marshal(dos);
			dos.writeFloat(azimuthBeamwidth);
			dos.writeFloat(elevationBeamwidth);
			dos.writeFloat(referenceSystem);
			dos.writeShort(padding1);
			dos.writeByte(padding2);
			dos.writeFloat(ez);
			dos.writeFloat(ex);
			dos.writeFloat(phase);
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
		beamDirection.marshal(buff);
		buff.putFloat(azimuthBeamwidth);
		buff.putFloat(elevationBeamwidth);
		buff.putFloat(referenceSystem);
		buff.putShort(padding1);
		buff.put(padding2);
		buff.putFloat(ez);
		buff.putFloat(ex);
		buff.putFloat(phase);
	} // end of marshal method

	public void setAzimuthBeamwidth(final float pAzimuthBeamwidth) {
		azimuthBeamwidth = pAzimuthBeamwidth;
	}

	public void setBeamDirection(final Orientation pBeamDirection) {
		beamDirection = pBeamDirection;
	}

	public void setElevationBeamwidth(final float pElevationBeamwidth) {
		elevationBeamwidth = pElevationBeamwidth;
	}

	public void setEx(final float pEx) {
		ex = pEx;
	}

	public void setEz(final float pEz) {
		ez = pEz;
	}

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final byte pPadding2) {
		padding2 = pPadding2;
	}

	public void setPhase(final float pPhase) {
		phase = pPhase;
	}

	public void setReferenceSystem(final float pReferenceSystem) {
		referenceSystem = pReferenceSystem;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			beamDirection.unmarshal(dis);
			azimuthBeamwidth = dis.readFloat();
			elevationBeamwidth = dis.readFloat();
			referenceSystem = dis.readFloat();
			padding1 = dis.readShort();
			padding2 = dis.readByte();
			ez = dis.readFloat();
			ex = dis.readFloat();
			phase = dis.readFloat();
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
		beamDirection.unmarshal(buff);
		azimuthBeamwidth = buff.getFloat();
		elevationBeamwidth = buff.getFloat();
		referenceSystem = buff.getFloat();
		padding1 = buff.getShort();
		padding2 = buff.get();
		ez = buff.getFloat();
		ex = buff.getFloat();
		phase = buff.getFloat();
	} // end of unmarshal method
} // end of class
