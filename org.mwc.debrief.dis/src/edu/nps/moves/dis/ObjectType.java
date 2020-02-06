package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Identifies type of object. This is a shorter version of EntityType that omits
 * the specific and extra fields.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ObjectType extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Kind of entity */
	protected short entityKind;

	/** Domain of entity (air, surface, subsurface, space, etc) */
	protected short domain;

	/** country to which the design of the entity is attributed */
	protected int country;

	/** category of entity */
	protected short category;

	/** subcategory of entity */
	protected short subcategory;

	/** Constructor */
	public ObjectType() {
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

		if (!(obj instanceof ObjectType))
			return false;

		final ObjectType rhs = (ObjectType) obj;

		if (!(entityKind == rhs.entityKind))
			ivarsEqual = false;
		if (!(domain == rhs.domain))
			ivarsEqual = false;
		if (!(country == rhs.country))
			ivarsEqual = false;
		if (!(category == rhs.category))
			ivarsEqual = false;
		if (!(subcategory == rhs.subcategory))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getCategory() {
		return category;
	}

	public int getCountry() {
		return country;
	}

	public short getDomain() {
		return domain;
	}

	public short getEntityKind() {
		return entityKind;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // entityKind
		marshalSize = marshalSize + 1; // domain
		marshalSize = marshalSize + 2; // country
		marshalSize = marshalSize + 1; // category
		marshalSize = marshalSize + 1; // subcategory

		return marshalSize;
	}

	public short getSubcategory() {
		return subcategory;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) entityKind);
			dos.writeByte((byte) domain);
			dos.writeShort((short) country);
			dos.writeByte((byte) category);
			dos.writeByte((byte) subcategory);
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
		buff.put((byte) entityKind);
		buff.put((byte) domain);
		buff.putShort((short) country);
		buff.put((byte) category);
		buff.put((byte) subcategory);
	} // end of marshal method

	public void setCategory(final short pCategory) {
		category = pCategory;
	}

	public void setCountry(final int pCountry) {
		country = pCountry;
	}

	public void setDomain(final short pDomain) {
		domain = pDomain;
	}

	public void setEntityKind(final short pEntityKind) {
		entityKind = pEntityKind;
	}

	public void setSubcategory(final short pSubcategory) {
		subcategory = pSubcategory;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			entityKind = (short) dis.readUnsignedByte();
			domain = (short) dis.readUnsignedByte();
			country = dis.readUnsignedShort();
			category = (short) dis.readUnsignedByte();
			subcategory = (short) dis.readUnsignedByte();
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
		entityKind = (short) (buff.get() & 0xFF);
		domain = (short) (buff.get() & 0xFF);
		country = buff.getShort() & 0xFFFF;
		category = (short) (buff.get() & 0xFF);
		subcategory = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
