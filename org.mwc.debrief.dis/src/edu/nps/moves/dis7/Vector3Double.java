package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Three double precision floating point values, x, y, and z. Used for world
 * coordinates Section 6.2.97.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Vector3Double extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** X value */
	protected double x;

	/** y Value */
	protected double y;

	/** Z value */
	protected double z;

	/** Constructor */
	public Vector3Double() {
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

		if (!(obj instanceof Vector3Double))
			return false;

		final Vector3Double rhs = (Vector3Double) obj;

		if (!(x == rhs.x))
			ivarsEqual = false;
		if (!(y == rhs.y))
			ivarsEqual = false;
		if (!(z == rhs.z))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 8; // x
		marshalSize = marshalSize + 8; // y
		marshalSize = marshalSize + 8; // z

		return marshalSize;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeDouble(x);
			dos.writeDouble(y);
			dos.writeDouble(z);
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
		buff.putDouble(x);
		buff.putDouble(y);
		buff.putDouble(z);
	} // end of marshal method

	public void setX(final double pX) {
		x = pX;
	}

	public void setY(final double pY) {
		y = pY;
	}

	public void setZ(final double pZ) {
		z = pZ;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			x = dis.readDouble();
			y = dis.readDouble();
			z = dis.readDouble();
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
		x = buff.getDouble();
		y = buff.getDouble();
		z = buff.getDouble();
	} // end of unmarshal method
} // end of class
