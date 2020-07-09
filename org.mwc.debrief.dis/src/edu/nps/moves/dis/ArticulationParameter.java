package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.5. Articulation parameters for movable parts and attached parts
 * of an entity. Specifes wether or not a change has occured, the part
 * identifcation of the articulated part to which it is attached, and the type
 * and value of each parameter.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ArticulationParameter extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected short parameterTypeDesignator;

	protected short changeIndicator;

	protected int partAttachedTo;

	protected int parameterType;

	protected double parameterValue;

	/** Constructor */
	public ArticulationParameter() {
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

		if (!(obj instanceof ArticulationParameter))
			return false;

		final ArticulationParameter rhs = (ArticulationParameter) obj;

		if (!(parameterTypeDesignator == rhs.parameterTypeDesignator))
			ivarsEqual = false;
		if (!(changeIndicator == rhs.changeIndicator))
			ivarsEqual = false;
		if (!(partAttachedTo == rhs.partAttachedTo))
			ivarsEqual = false;
		if (!(parameterType == rhs.parameterType))
			ivarsEqual = false;
		if (!(parameterValue == rhs.parameterValue))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getChangeIndicator() {
		return changeIndicator;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // parameterTypeDesignator
		marshalSize = marshalSize + 1; // changeIndicator
		marshalSize = marshalSize + 2; // partAttachedTo
		marshalSize = marshalSize + 4; // parameterType
		marshalSize = marshalSize + 8; // parameterValue

		return marshalSize;
	}

	public int getParameterType() {
		return parameterType;
	}

	public short getParameterTypeDesignator() {
		return parameterTypeDesignator;
	}

	public double getParameterValue() {
		return parameterValue;
	}

	public int getPartAttachedTo() {
		return partAttachedTo;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) parameterTypeDesignator);
			dos.writeByte((byte) changeIndicator);
			dos.writeShort((short) partAttachedTo);
			dos.writeInt(parameterType);
			dos.writeDouble(parameterValue);
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
		buff.put((byte) parameterTypeDesignator);
		buff.put((byte) changeIndicator);
		buff.putShort((short) partAttachedTo);
		buff.putInt(parameterType);
		buff.putDouble(parameterValue);
	} // end of marshal method

	public void setChangeIndicator(final short pChangeIndicator) {
		changeIndicator = pChangeIndicator;
	}

	public void setParameterType(final int pParameterType) {
		parameterType = pParameterType;
	}

	public void setParameterTypeDesignator(final short pParameterTypeDesignator) {
		parameterTypeDesignator = pParameterTypeDesignator;
	}

	public void setParameterValue(final double pParameterValue) {
		parameterValue = pParameterValue;
	}

	public void setPartAttachedTo(final int pPartAttachedTo) {
		partAttachedTo = pPartAttachedTo;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			parameterTypeDesignator = (short) dis.readUnsignedByte();
			changeIndicator = (short) dis.readUnsignedByte();
			partAttachedTo = dis.readUnsignedShort();
			parameterType = dis.readInt();
			parameterValue = dis.readDouble();
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
		parameterTypeDesignator = (short) (buff.get() & 0xFF);
		changeIndicator = (short) (buff.get() & 0xFF);
		partAttachedTo = buff.getShort() & 0xFFFF;
		parameterType = buff.getInt();
		parameterValue = buff.getDouble();
	} // end of unmarshal method
} // end of class
