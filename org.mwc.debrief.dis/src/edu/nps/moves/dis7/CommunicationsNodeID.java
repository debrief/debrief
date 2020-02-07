package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Identity of a communications node. Section 6.2.48.4
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class CommunicationsNodeID extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected EntityID entityID = new EntityID();

	protected int elementID;

	/** Constructor */
	public CommunicationsNodeID() {
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

		if (!(obj instanceof CommunicationsNodeID))
			return false;

		final CommunicationsNodeID rhs = (CommunicationsNodeID) obj;

		if (!(entityID.equals(rhs.entityID)))
			ivarsEqual = false;
		if (!(elementID == rhs.elementID))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getElementID() {
		return elementID;
	}

	public EntityID getEntityID() {
		return entityID;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + entityID.getMarshalledSize(); // entityID
		marshalSize = marshalSize + 2; // elementID

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			entityID.marshal(dos);
			dos.writeShort((short) elementID);
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
		entityID.marshal(buff);
		buff.putShort((short) elementID);
	} // end of marshal method

	public void setElementID(final int pElementID) {
		elementID = pElementID;
	}

	public void setEntityID(final EntityID pEntityID) {
		entityID = pEntityID;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			entityID.unmarshal(dis);
			elementID = dis.readUnsignedShort();
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
		entityID.unmarshal(buff);
		elementID = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
