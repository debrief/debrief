package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Describes the scan volue of an emitter beam. Section 6.2.11.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class BeamData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Specifies the beam azimuth an elevation centers and corresponding half-angles
	 * to describe the scan volume
	 */
	protected float beamAzimuthCenter;

	/** Specifies the beam azimuth sweep to determine scan volume */
	protected float beamAzimuthSweep;

	/** Specifies the beam elevation center to determine scan volume */
	protected float beamElevationCenter;

	/** Specifies the beam elevation sweep to determine scan volume */
	protected float beamElevationSweep;

	/**
	 * allows receiver to synchronize its regenerated scan pattern to that of the
	 * emmitter. Specifies the percentage of time a scan is through its pattern from
	 * its origion.
	 */
	protected float beamSweepSync;

	/** Constructor */
	public BeamData() {
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

		if (!(obj instanceof BeamData))
			return false;

		final BeamData rhs = (BeamData) obj;

		if (!(beamAzimuthCenter == rhs.beamAzimuthCenter))
			ivarsEqual = false;
		if (!(beamAzimuthSweep == rhs.beamAzimuthSweep))
			ivarsEqual = false;
		if (!(beamElevationCenter == rhs.beamElevationCenter))
			ivarsEqual = false;
		if (!(beamElevationSweep == rhs.beamElevationSweep))
			ivarsEqual = false;
		if (!(beamSweepSync == rhs.beamSweepSync))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getBeamAzimuthCenter() {
		return beamAzimuthCenter;
	}

	public float getBeamAzimuthSweep() {
		return beamAzimuthSweep;
	}

	public float getBeamElevationCenter() {
		return beamElevationCenter;
	}

	public float getBeamElevationSweep() {
		return beamElevationSweep;
	}

	public float getBeamSweepSync() {
		return beamSweepSync;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // beamAzimuthCenter
		marshalSize = marshalSize + 4; // beamAzimuthSweep
		marshalSize = marshalSize + 4; // beamElevationCenter
		marshalSize = marshalSize + 4; // beamElevationSweep
		marshalSize = marshalSize + 4; // beamSweepSync

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(beamAzimuthCenter);
			dos.writeFloat(beamAzimuthSweep);
			dos.writeFloat(beamElevationCenter);
			dos.writeFloat(beamElevationSweep);
			dos.writeFloat(beamSweepSync);
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
		buff.putFloat(beamAzimuthCenter);
		buff.putFloat(beamAzimuthSweep);
		buff.putFloat(beamElevationCenter);
		buff.putFloat(beamElevationSweep);
		buff.putFloat(beamSweepSync);
	} // end of marshal method

	public void setBeamAzimuthCenter(final float pBeamAzimuthCenter) {
		beamAzimuthCenter = pBeamAzimuthCenter;
	}

	public void setBeamAzimuthSweep(final float pBeamAzimuthSweep) {
		beamAzimuthSweep = pBeamAzimuthSweep;
	}

	public void setBeamElevationCenter(final float pBeamElevationCenter) {
		beamElevationCenter = pBeamElevationCenter;
	}

	public void setBeamElevationSweep(final float pBeamElevationSweep) {
		beamElevationSweep = pBeamElevationSweep;
	}

	public void setBeamSweepSync(final float pBeamSweepSync) {
		beamSweepSync = pBeamSweepSync;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			beamAzimuthCenter = dis.readFloat();
			beamAzimuthSweep = dis.readFloat();
			beamElevationCenter = dis.readFloat();
			beamElevationSweep = dis.readFloat();
			beamSweepSync = dis.readFloat();
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
		beamAzimuthCenter = buff.getFloat();
		beamAzimuthSweep = buff.getFloat();
		beamElevationCenter = buff.getFloat();
		beamElevationSweep = buff.getFloat();
		beamSweepSync = buff.getFloat();
	} // end of unmarshal method
} // end of class
