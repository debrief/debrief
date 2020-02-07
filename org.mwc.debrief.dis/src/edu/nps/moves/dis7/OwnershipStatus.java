package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * used to convey entity and conflict status information associated with
 * transferring ownership of an entity. Section 6.2.65
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class OwnershipStatus extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** EntityID */
	protected EntityID entityId = new EntityID();

	/**
	 * The ownership and/or ownership conflict status of the entity represented by
	 * the Entity ID field.
	 */
	protected short ownershipStatus;

	/** padding */
	protected short padding = (short) 0;

	/** Constructor */
	public OwnershipStatus() {
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

		if (!(obj instanceof OwnershipStatus))
			return false;

		final OwnershipStatus rhs = (OwnershipStatus) obj;

		if (!(entityId.equals(rhs.entityId)))
			ivarsEqual = false;
		if (!(ownershipStatus == rhs.ownershipStatus))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public EntityID getEntityId() {
		return entityId;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + entityId.getMarshalledSize(); // entityId
		marshalSize = marshalSize + 1; // ownershipStatus
		marshalSize = marshalSize + 1; // padding

		return marshalSize;
	}

	public short getOwnershipStatus() {
		return ownershipStatus;
	}

	public short getPadding() {
		return padding;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			entityId.marshal(dos);
			dos.writeByte((byte) ownershipStatus);
			dos.writeByte((byte) padding);
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
		entityId.marshal(buff);
		buff.put((byte) ownershipStatus);
		buff.put((byte) padding);
	} // end of marshal method

	public void setEntityId(final EntityID pEntityId) {
		entityId = pEntityId;
	}

	public void setOwnershipStatus(final short pOwnershipStatus) {
		ownershipStatus = pOwnershipStatus;
	}

	public void setPadding(final short pPadding) {
		padding = pPadding;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			entityId.unmarshal(dis);
			ownershipStatus = (short) dis.readUnsignedByte();
			padding = (short) dis.readUnsignedByte();
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
		entityId.unmarshal(buff);
		ownershipStatus = (short) (buff.get() & 0xFF);
		padding = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
