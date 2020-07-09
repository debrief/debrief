package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * An entity's munition (e.g., bomb, missile) information shall be represented
 * by one or more Munition records. For each type or location of munition, this
 * record shall specify the type, location, quantity and status of munitions
 * that an entity contains. Section 6.2.60
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Munition extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This field shall identify the entity type of the munition. See section
	 * 6.2.30.
	 */
	protected EntityType munitionType = new EntityType();

	/** the station or launcher to which the munition is assigned. See Annex I */
	protected long station;

	/** the quantity remaining of this munition. */
	protected int quantity;

	/**
	 * the status of the munition. It shall be represented by an 8-bit enumeration.
	 */
	protected short munitionStatus;

	/** padding */
	protected short padding = (short) 0;

	/** Constructor */
	public Munition() {
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

		if (!(obj instanceof Munition))
			return false;

		final Munition rhs = (Munition) obj;

		if (!(munitionType.equals(rhs.munitionType)))
			ivarsEqual = false;
		if (!(station == rhs.station))
			ivarsEqual = false;
		if (!(quantity == rhs.quantity))
			ivarsEqual = false;
		if (!(munitionStatus == rhs.munitionStatus))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + munitionType.getMarshalledSize(); // munitionType
		marshalSize = marshalSize + 4; // station
		marshalSize = marshalSize + 2; // quantity
		marshalSize = marshalSize + 1; // munitionStatus
		marshalSize = marshalSize + 1; // padding

		return marshalSize;
	}

	public short getMunitionStatus() {
		return munitionStatus;
	}

	public EntityType getMunitionType() {
		return munitionType;
	}

	public short getPadding() {
		return padding;
	}

	public int getQuantity() {
		return quantity;
	}

	public long getStation() {
		return station;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			munitionType.marshal(dos);
			dos.writeInt((int) station);
			dos.writeShort((short) quantity);
			dos.writeByte((byte) munitionStatus);
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
		munitionType.marshal(buff);
		buff.putInt((int) station);
		buff.putShort((short) quantity);
		buff.put((byte) munitionStatus);
		buff.put((byte) padding);
	} // end of marshal method

	public void setMunitionStatus(final short pMunitionStatus) {
		munitionStatus = pMunitionStatus;
	}

	public void setMunitionType(final EntityType pMunitionType) {
		munitionType = pMunitionType;
	}

	public void setPadding(final short pPadding) {
		padding = pPadding;
	}

	public void setQuantity(final int pQuantity) {
		quantity = pQuantity;
	}

	public void setStation(final long pStation) {
		station = pStation;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			munitionType.unmarshal(dis);
			station = dis.readInt();
			quantity = dis.readUnsignedShort();
			munitionStatus = (short) dis.readUnsignedByte();
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
		munitionType.unmarshal(buff);
		station = buff.getInt();
		quantity = buff.getShort() & 0xFFFF;
		munitionStatus = (short) (buff.get() & 0xFF);
		padding = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
