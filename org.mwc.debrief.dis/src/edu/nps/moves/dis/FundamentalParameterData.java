package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.22. Contains electromagnetic emmision regineratin parameters that
 * are variable throughout a scenario dependent on the actions of the
 * participants in the simulation. Also provides basic parametric data that may
 * be used to support low-fidelity simulations.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class FundamentalParameterData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** center frequency of the emission in hertz. */
	protected float frequency;

	/** Bandwidth of the frequencies corresponding to the fequency field. */
	protected float frequencyRange;

	/**
	 * Effective radiated power for the emission in DdBm. For a radar noise jammer,
	 * indicates the peak of the transmitted power.
	 */
	protected float effectiveRadiatedPower;

	/** Average repetition frequency of the emission in hertz. */
	protected float pulseRepetitionFrequency;

	/** Average pulse width of the emission in microseconds. */
	protected float pulseWidth;

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
	public FundamentalParameterData() {
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

		if (!(obj instanceof FundamentalParameterData))
			return false;

		final FundamentalParameterData rhs = (FundamentalParameterData) obj;

		if (!(frequency == rhs.frequency))
			ivarsEqual = false;
		if (!(frequencyRange == rhs.frequencyRange))
			ivarsEqual = false;
		if (!(effectiveRadiatedPower == rhs.effectiveRadiatedPower))
			ivarsEqual = false;
		if (!(pulseRepetitionFrequency == rhs.pulseRepetitionFrequency))
			ivarsEqual = false;
		if (!(pulseWidth == rhs.pulseWidth))
			ivarsEqual = false;
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

	public float getEffectiveRadiatedPower() {
		return effectiveRadiatedPower;
	}

	public float getFrequency() {
		return frequency;
	}

	public float getFrequencyRange() {
		return frequencyRange;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // frequency
		marshalSize = marshalSize + 4; // frequencyRange
		marshalSize = marshalSize + 4; // effectiveRadiatedPower
		marshalSize = marshalSize + 4; // pulseRepetitionFrequency
		marshalSize = marshalSize + 4; // pulseWidth
		marshalSize = marshalSize + 4; // beamAzimuthCenter
		marshalSize = marshalSize + 4; // beamAzimuthSweep
		marshalSize = marshalSize + 4; // beamElevationCenter
		marshalSize = marshalSize + 4; // beamElevationSweep
		marshalSize = marshalSize + 4; // beamSweepSync

		return marshalSize;
	}

	public float getPulseRepetitionFrequency() {
		return pulseRepetitionFrequency;
	}

	public float getPulseWidth() {
		return pulseWidth;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(frequency);
			dos.writeFloat(frequencyRange);
			dos.writeFloat(effectiveRadiatedPower);
			dos.writeFloat(pulseRepetitionFrequency);
			dos.writeFloat(pulseWidth);
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
		buff.putFloat(frequency);
		buff.putFloat(frequencyRange);
		buff.putFloat(effectiveRadiatedPower);
		buff.putFloat(pulseRepetitionFrequency);
		buff.putFloat(pulseWidth);
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

	public void setEffectiveRadiatedPower(final float pEffectiveRadiatedPower) {
		effectiveRadiatedPower = pEffectiveRadiatedPower;
	}

	public void setFrequency(final float pFrequency) {
		frequency = pFrequency;
	}

	public void setFrequencyRange(final float pFrequencyRange) {
		frequencyRange = pFrequencyRange;
	}

	public void setPulseRepetitionFrequency(final float pPulseRepetitionFrequency) {
		pulseRepetitionFrequency = pPulseRepetitionFrequency;
	}

	public void setPulseWidth(final float pPulseWidth) {
		pulseWidth = pPulseWidth;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			frequency = dis.readFloat();
			frequencyRange = dis.readFloat();
			effectiveRadiatedPower = dis.readFloat();
			pulseRepetitionFrequency = dis.readFloat();
			pulseWidth = dis.readFloat();
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
		frequency = buff.getFloat();
		frequencyRange = buff.getFloat();
		effectiveRadiatedPower = buff.getFloat();
		pulseRepetitionFrequency = buff.getFloat();
		pulseWidth = buff.getFloat();
		beamAzimuthCenter = buff.getFloat();
		beamAzimuthSweep = buff.getFloat();
		beamElevationCenter = buff.getFloat();
		beamElevationSweep = buff.getFloat();
		beamSweepSync = buff.getFloat();
	} // end of unmarshal method
} // end of class
