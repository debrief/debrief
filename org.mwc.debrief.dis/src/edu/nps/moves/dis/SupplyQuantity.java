package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.30. A supply, and the amount of that supply. Similar to an entity
 * kind but with the addition of a quantity.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SupplyQuantity extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Type of supply */
	protected EntityType supplyType = new EntityType();

	/** quantity to be supplied */
	protected short quantity;

	/** Constructor */
	public SupplyQuantity() {
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

		if (!(obj instanceof SupplyQuantity))
			return false;

		final SupplyQuantity rhs = (SupplyQuantity) obj;

		if (!(supplyType.equals(rhs.supplyType)))
			ivarsEqual = false;
		if (!(quantity == rhs.quantity))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + supplyType.getMarshalledSize(); // supplyType
		marshalSize = marshalSize + 1; // quantity

		return marshalSize;
	}

	public short getQuantity() {
		return quantity;
	}

	public EntityType getSupplyType() {
		return supplyType;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			supplyType.marshal(dos);
			dos.writeByte((byte) quantity);
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
		supplyType.marshal(buff);
		buff.put((byte) quantity);
	} // end of marshal method

	public void setQuantity(final short pQuantity) {
		quantity = pQuantity;
	}

	public void setSupplyType(final EntityType pSupplyType) {
		supplyType = pSupplyType;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			supplyType.unmarshal(dis);
			quantity = (short) dis.readUnsignedByte();
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
		supplyType.unmarshal(buff);
		quantity = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
