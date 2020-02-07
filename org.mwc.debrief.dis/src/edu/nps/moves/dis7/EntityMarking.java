package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Specifies the character set used inthe first byte, followed by 11 characters
 * of text data. Section 6.29
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EntityMarking extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The character set */
	protected short characterSet;

	/** The characters */
	protected byte characters;

	/** Constructor */
	public EntityMarking() {
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

		if (!(obj instanceof EntityMarking))
			return false;

		final EntityMarking rhs = (EntityMarking) obj;

		if (!(characterSet == rhs.characterSet))
			ivarsEqual = false;
		if (!(characters == rhs.characters))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public byte getCharacters() {
		return characters;
	}

	public short getCharacterSet() {
		return characterSet;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // characterSet
		marshalSize = marshalSize + 1; // characters

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) characterSet);
			dos.writeByte(characters);
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
		buff.put((byte) characterSet);
		buff.put(characters);
	} // end of marshal method

	public void setCharacters(final byte pCharacters) {
		characters = pCharacters;
	}

	public void setCharacterSet(final short pCharacterSet) {
		characterSet = pCharacterSet;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			characterSet = (short) dis.readUnsignedByte();
			characters = dis.readByte();
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
		characterSet = (short) (buff.get() & 0xFF);
		characters = buff.get();
	} // end of unmarshal method
} // end of class
