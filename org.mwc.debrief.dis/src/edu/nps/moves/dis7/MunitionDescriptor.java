package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Represents the firing or detonation of a munition. Section 6.2.19.2
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MunitionDescriptor extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** What munition was used in the burst */
	protected EntityType munitionType = new EntityType();

	/** type of warhead enumeration */
	protected int warhead;

	/** type of fuse used enumeration */
	protected int fuse;

	/** how many of the munition were fired */
	protected int quantity;

	/** rate at which the munition was fired */
	protected int rate;

	/** Constructor */
	public MunitionDescriptor() {
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

		if (!(obj instanceof MunitionDescriptor))
			return false;

		final MunitionDescriptor rhs = (MunitionDescriptor) obj;

		if (!(munitionType.equals(rhs.munitionType)))
			ivarsEqual = false;
		if (!(warhead == rhs.warhead))
			ivarsEqual = false;
		if (!(fuse == rhs.fuse))
			ivarsEqual = false;
		if (!(quantity == rhs.quantity))
			ivarsEqual = false;
		if (!(rate == rhs.rate))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getFuse() {
		return fuse;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + munitionType.getMarshalledSize(); // munitionType
		marshalSize = marshalSize + 2; // warhead
		marshalSize = marshalSize + 2; // fuse
		marshalSize = marshalSize + 2; // quantity
		marshalSize = marshalSize + 2; // rate

		return marshalSize;
	}

	public EntityType getMunitionType() {
		return munitionType;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getRate() {
		return rate;
	}

	public int getWarhead() {
		return warhead;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			munitionType.marshal(dos);
			dos.writeShort((short) warhead);
			dos.writeShort((short) fuse);
			dos.writeShort((short) quantity);
			dos.writeShort((short) rate);
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
		buff.putShort((short) warhead);
		buff.putShort((short) fuse);
		buff.putShort((short) quantity);
		buff.putShort((short) rate);
	} // end of marshal method

	public void setFuse(final int pFuse) {
		fuse = pFuse;
	}

	public void setMunitionType(final EntityType pMunitionType) {
		munitionType = pMunitionType;
	}

	public void setQuantity(final int pQuantity) {
		quantity = pQuantity;
	}

	public void setRate(final int pRate) {
		rate = pRate;
	}

	public void setWarhead(final int pWarhead) {
		warhead = pWarhead;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			munitionType.unmarshal(dis);
			warhead = dis.readUnsignedShort();
			fuse = dis.readUnsignedShort();
			quantity = dis.readUnsignedShort();
			rate = dis.readUnsignedShort();
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
		warhead = buff.getShort() & 0xFFFF;
		fuse = buff.getShort() & 0xFFFF;
		quantity = buff.getShort() & 0xFFFF;
		rate = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
