package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Used when the antenna pattern type field has a value of 1. Specifies the
 * direction, pattern, and polarization of radiation from an antenna. Section
 * 6.2.9.2
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
	 * The rotation that transforms the reference coordinate sytem into the beam
	 * coordinate system. Either world coordinates or entity coordinates may be used
	 * as the reference coordinate system, as specified by the reference system
	 * field of the antenna pattern record.
	 */
	protected EulerAngles beamDirection = new EulerAngles();

	protected float azimuthBeamwidth = 0;

	protected float elevationBeamwidth = 0;

	protected short referenceSystem = (short) 0;

	protected short padding1 = (short) 0;

	protected int padding2 = 0;

	/**
	 * This field shall specify the magnitude of the Z-component (in beam
	 * coordinates) of the Electrical field at some arbitrary single point in the
	 * main beam and in the far field of the antenna.
	 */
	protected float ez = (float) 0.0;

	/**
	 * This field shall specify the magnitude of the X-component (in beam
	 * coordinates) of the Electri- cal field at some arbitrary single point in the
	 * main beam and in the far field of the antenna.
	 */
	protected float ex = (float) 0.0;

	/**
	 * This field shall specify the phase angle between EZ and EX in radians. If
	 * fully omni-direc- tional antenna is modeled using beam pattern type one, the
	 * omni-directional antenna shall be repre- sented by beam direction Euler
	 * angles psi, theta, and phi of zero, an azimuth beamwidth of 2PI, and an
	 * elevation beamwidth of PI
	 */
	protected float phase = (float) 0.0;

	/** padding */
	protected long padding3 = 0;

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
		if (!(padding3 == rhs.padding3))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getAzimuthBeamwidth() {
		return azimuthBeamwidth;
	}

	public EulerAngles getBeamDirection() {
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
		marshalSize = marshalSize + 1; // referenceSystem
		marshalSize = marshalSize + 1; // padding1
		marshalSize = marshalSize + 2; // padding2
		marshalSize = marshalSize + 4; // ez
		marshalSize = marshalSize + 4; // ex
		marshalSize = marshalSize + 4; // phase
		marshalSize = marshalSize + 4; // padding3

		return marshalSize;
	}

	public short getPadding1() {
		return padding1;
	}

	public int getPadding2() {
		return padding2;
	}

	public long getPadding3() {
		return padding3;
	}

	public float getPhase() {
		return phase;
	}

	public short getReferenceSystem() {
		return referenceSystem;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			beamDirection.marshal(dos);
			dos.writeFloat(azimuthBeamwidth);
			dos.writeFloat(elevationBeamwidth);
			dos.writeByte((byte) referenceSystem);
			dos.writeByte((byte) padding1);
			dos.writeShort((short) padding2);
			dos.writeFloat(ez);
			dos.writeFloat(ex);
			dos.writeFloat(phase);
			dos.writeInt((int) padding3);
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
		buff.put((byte) referenceSystem);
		buff.put((byte) padding1);
		buff.putShort((short) padding2);
		buff.putFloat(ez);
		buff.putFloat(ex);
		buff.putFloat(phase);
		buff.putInt((int) padding3);
	} // end of marshal method

	public void setAzimuthBeamwidth(final float pAzimuthBeamwidth) {
		azimuthBeamwidth = pAzimuthBeamwidth;
	}

	public void setBeamDirection(final EulerAngles pBeamDirection) {
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

	public void setPadding2(final int pPadding2) {
		padding2 = pPadding2;
	}

	public void setPadding3(final long pPadding3) {
		padding3 = pPadding3;
	}

	public void setPhase(final float pPhase) {
		phase = pPhase;
	}

	public void setReferenceSystem(final short pReferenceSystem) {
		referenceSystem = pReferenceSystem;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			beamDirection.unmarshal(dis);
			azimuthBeamwidth = dis.readFloat();
			elevationBeamwidth = dis.readFloat();
			referenceSystem = (short) dis.readUnsignedByte();
			padding1 = (short) dis.readUnsignedByte();
			padding2 = dis.readUnsignedShort();
			ez = dis.readFloat();
			ex = dis.readFloat();
			phase = dis.readFloat();
			padding3 = dis.readInt();
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
		referenceSystem = (short) (buff.get() & 0xFF);
		padding1 = (short) (buff.get() & 0xFF);
		padding2 = buff.getShort() & 0xFFFF;
		ez = buff.getFloat();
		ex = buff.getFloat();
		phase = buff.getFloat();
		padding3 = buff.getInt();
	} // end of unmarshal method
} // end of class
