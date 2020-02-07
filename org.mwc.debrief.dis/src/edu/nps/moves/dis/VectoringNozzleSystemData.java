package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Data about a vectoring nozzle system
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class VectoringNozzleSystemData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** horizontal deflection angle */
	protected float horizontalDeflectionAngle;

	/** vertical deflection angle */
	protected float verticalDeflectionAngle;

	/** Constructor */
	public VectoringNozzleSystemData() {
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

		if (!(obj instanceof VectoringNozzleSystemData))
			return false;

		final VectoringNozzleSystemData rhs = (VectoringNozzleSystemData) obj;

		if (!(horizontalDeflectionAngle == rhs.horizontalDeflectionAngle))
			ivarsEqual = false;
		if (!(verticalDeflectionAngle == rhs.verticalDeflectionAngle))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public float getHorizontalDeflectionAngle() {
		return horizontalDeflectionAngle;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // horizontalDeflectionAngle
		marshalSize = marshalSize + 4; // verticalDeflectionAngle

		return marshalSize;
	}

	public float getVerticalDeflectionAngle() {
		return verticalDeflectionAngle;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(horizontalDeflectionAngle);
			dos.writeFloat(verticalDeflectionAngle);
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
		buff.putFloat(horizontalDeflectionAngle);
		buff.putFloat(verticalDeflectionAngle);
	} // end of marshal method

	public void setHorizontalDeflectionAngle(final float pHorizontalDeflectionAngle) {
		horizontalDeflectionAngle = pHorizontalDeflectionAngle;
	}

	public void setVerticalDeflectionAngle(final float pVerticalDeflectionAngle) {
		verticalDeflectionAngle = pVerticalDeflectionAngle;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			horizontalDeflectionAngle = dis.readFloat();
			verticalDeflectionAngle = dis.readFloat();
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
		horizontalDeflectionAngle = buff.getFloat();
		verticalDeflectionAngle = buff.getFloat();
	} // end of unmarshal method
} // end of class
