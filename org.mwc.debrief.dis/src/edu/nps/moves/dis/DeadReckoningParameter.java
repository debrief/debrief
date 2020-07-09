package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * represents values used in dead reckoning algorithms
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DeadReckoningParameter extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** enumeration of what dead reckoning algorighm to use */
	protected short deadReckoningAlgorithm;

	/** other parameters to use in the dead reckoning algorithm */
	protected byte[] otherParameters = new byte[15];

	/** Linear acceleration of the entity */
	protected Vector3Float entityLinearAcceleration = new Vector3Float();

	/** angular velocity of the entity */
	protected Vector3Float entityAngularVelocity = new Vector3Float();

	/** Constructor */
	public DeadReckoningParameter() {
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

		if (!(obj instanceof DeadReckoningParameter))
			return false;

		final DeadReckoningParameter rhs = (DeadReckoningParameter) obj;

		if (!(deadReckoningAlgorithm == rhs.deadReckoningAlgorithm))
			ivarsEqual = false;

		for (int idx = 0; idx < 15; idx++) {
			if (!(otherParameters[idx] == rhs.otherParameters[idx]))
				ivarsEqual = false;
		}

		if (!(entityLinearAcceleration.equals(rhs.entityLinearAcceleration)))
			ivarsEqual = false;
		if (!(entityAngularVelocity.equals(rhs.entityAngularVelocity)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getDeadReckoningAlgorithm() {
		return deadReckoningAlgorithm;
	}

	public Vector3Float getEntityAngularVelocity() {
		return entityAngularVelocity;
	}

	public Vector3Float getEntityLinearAcceleration() {
		return entityLinearAcceleration;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // deadReckoningAlgorithm
		marshalSize = marshalSize + 15 * 1; // otherParameters
		marshalSize = marshalSize + entityLinearAcceleration.getMarshalledSize(); // entityLinearAcceleration
		marshalSize = marshalSize + entityAngularVelocity.getMarshalledSize(); // entityAngularVelocity

		return marshalSize;
	}

	public byte[] getOtherParameters() {
		return otherParameters;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) deadReckoningAlgorithm);

			for (int idx = 0; idx < otherParameters.length; idx++) {
				dos.writeByte(otherParameters[idx]);
			} // end of array marshaling

			entityLinearAcceleration.marshal(dos);
			entityAngularVelocity.marshal(dos);
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
		buff.put((byte) deadReckoningAlgorithm);

		for (int idx = 0; idx < otherParameters.length; idx++) {
			buff.put(otherParameters[idx]);
		} // end of array marshaling

		entityLinearAcceleration.marshal(buff);
		entityAngularVelocity.marshal(buff);
	} // end of marshal method

	public void setDeadReckoningAlgorithm(final short pDeadReckoningAlgorithm) {
		deadReckoningAlgorithm = pDeadReckoningAlgorithm;
	}

	public void setEntityAngularVelocity(final Vector3Float pEntityAngularVelocity) {
		entityAngularVelocity = pEntityAngularVelocity;
	}

	public void setEntityLinearAcceleration(final Vector3Float pEntityLinearAcceleration) {
		entityLinearAcceleration = pEntityLinearAcceleration;
	}

	public void setOtherParameters(final byte[] pOtherParameters) {
		otherParameters = pOtherParameters;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			deadReckoningAlgorithm = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < otherParameters.length; idx++) {
				otherParameters[idx] = dis.readByte();
			} // end of array unmarshaling
			entityLinearAcceleration.unmarshal(dis);
			entityAngularVelocity.unmarshal(dis);
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
		deadReckoningAlgorithm = (short) (buff.get() & 0xFF);
		for (int idx = 0; idx < otherParameters.length; idx++) {
			otherParameters[idx] = buff.get();
		} // end of array unmarshaling
		entityLinearAcceleration.unmarshal(buff);
		entityAngularVelocity.unmarshal(buff);
	} // end of unmarshal method
} // end of class
