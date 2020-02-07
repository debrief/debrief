package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * contains information describing the propulsion systems of the entity. This
 * information shall be provided for each active propulsion system defined.
 * Section 6.2.68
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class PropulsionSystemData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** powerSetting */
	protected float powerSetting;

	/** engine RPMs */
	protected float engineRpm;

	/** Constructor */
	public PropulsionSystemData() {
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

		if (!(obj instanceof PropulsionSystemData))
			return false;

		final PropulsionSystemData rhs = (PropulsionSystemData) obj;

		if (!(powerSetting == rhs.powerSetting))
			ivarsEqual = false;
		if (!(engineRpm == rhs.engineRpm))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getEngineRpm() {
		return engineRpm;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // powerSetting
		marshalSize = marshalSize + 4; // engineRpm

		return marshalSize;
	}

	public float getPowerSetting() {
		return powerSetting;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(powerSetting);
			dos.writeFloat(engineRpm);
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
		buff.putFloat(powerSetting);
		buff.putFloat(engineRpm);
	} // end of marshal method

	public void setEngineRpm(final float pEngineRpm) {
		engineRpm = pEngineRpm;
	}

	public void setPowerSetting(final float pPowerSetting) {
		powerSetting = pPowerSetting;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			powerSetting = dis.readFloat();
			engineRpm = dis.readFloat();
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
		powerSetting = buff.getFloat();
		engineRpm = buff.getFloat();
	} // end of unmarshal method
} // end of class
