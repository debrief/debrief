package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * An entity's expendable (chaff, flares, etc) information. Section 6.2.36
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Expendable extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Type of expendable */
	protected EntityType expendable = new EntityType();

	protected long station;

	protected int quantity;

	protected short expendableStatus;

	protected short padding = (short) 0;

	/** Constructor */
	public Expendable() {
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

		if (!(obj instanceof Expendable))
			return false;

		final Expendable rhs = (Expendable) obj;

		if (!(expendable.equals(rhs.expendable)))
			ivarsEqual = false;
		if (!(station == rhs.station))
			ivarsEqual = false;
		if (!(quantity == rhs.quantity))
			ivarsEqual = false;
		if (!(expendableStatus == rhs.expendableStatus))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public EntityType getExpendable() {
		return expendable;
	}

	public short getExpendableStatus() {
		return expendableStatus;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + expendable.getMarshalledSize(); // expendable
		marshalSize = marshalSize + 4; // station
		marshalSize = marshalSize + 2; // quantity
		marshalSize = marshalSize + 1; // expendableStatus
		marshalSize = marshalSize + 1; // padding

		return marshalSize;
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
			expendable.marshal(dos);
			dos.writeInt((int) station);
			dos.writeShort((short) quantity);
			dos.writeByte((byte) expendableStatus);
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
		expendable.marshal(buff);
		buff.putInt((int) station);
		buff.putShort((short) quantity);
		buff.put((byte) expendableStatus);
		buff.put((byte) padding);
	} // end of marshal method

	public void setExpendable(final EntityType pExpendable) {
		expendable = pExpendable;
	}

	public void setExpendableStatus(final short pExpendableStatus) {
		expendableStatus = pExpendableStatus;
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
			expendable.unmarshal(dis);
			station = dis.readInt();
			quantity = dis.readUnsignedShort();
			expendableStatus = (short) dis.readUnsignedByte();
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
		expendable.unmarshal(buff);
		station = buff.getInt();
		quantity = buff.getShort() & 0xFFFF;
		expendableStatus = (short) (buff.get() & 0xFF);
		padding = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
