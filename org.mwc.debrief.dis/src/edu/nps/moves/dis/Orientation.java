package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.17. Three floating point values representing an orientation, psi,
 * theta, and phi, aka the euler angles, in radians
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Orientation extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected float psi;

	protected float theta;

	protected float phi;

	/** Constructor */
	public Orientation() {
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

		if (!(obj instanceof Orientation))
			return false;

		final Orientation rhs = (Orientation) obj;

		if (!(psi == rhs.psi))
			ivarsEqual = false;
		if (!(theta == rhs.theta))
			ivarsEqual = false;
		if (!(phi == rhs.phi))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // psi
		marshalSize = marshalSize + 4; // theta
		marshalSize = marshalSize + 4; // phi

		return marshalSize;
	}

	public float getPhi() {
		return phi;
	}

	public float getPsi() {
		return psi;
	}

	public float getTheta() {
		return theta;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeFloat(psi);
			dos.writeFloat(theta);
			dos.writeFloat(phi);
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
		buff.putFloat(psi);
		buff.putFloat(theta);
		buff.putFloat(phi);
	} // end of marshal method

	public void setPhi(final float pPhi) {
		phi = pPhi;
	}

	public void setPsi(final float pPsi) {
		psi = pPsi;
	}

	public void setTheta(final float pTheta) {
		theta = pTheta;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			psi = dis.readFloat();
			theta = dis.readFloat();
			phi = dis.readFloat();
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
		psi = buff.getFloat();
		theta = buff.getFloat();
		phi = buff.getFloat();
	} // end of unmarshal method
} // end of class
