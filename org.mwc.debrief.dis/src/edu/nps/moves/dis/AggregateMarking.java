package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.37. Specifies the character set used inthe first byte, followed
 * by up to 31 characters of text data.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AggregateMarking extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The character set */
	protected short characterSet;

	/** The characters */
	protected byte[] characters = new byte[31];

	/** Constructor */
	public AggregateMarking() {
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

		if (!(obj instanceof AggregateMarking))
			return false;

		final AggregateMarking rhs = (AggregateMarking) obj;

		if (!(characterSet == rhs.characterSet))
			ivarsEqual = false;

		for (int idx = 0; idx < 31; idx++) {
			if (!(characters[idx] == rhs.characters[idx]))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public byte[] getCharacters() {
		return characters;
	}

	public short getCharacterSet() {
		return characterSet;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // characterSet
		marshalSize = marshalSize + 31 * 1; // characters

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) characterSet);

			for (int idx = 0; idx < characters.length; idx++) {
				dos.writeByte(characters[idx]);
			} // end of array marshaling

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

		for (int idx = 0; idx < characters.length; idx++) {
			buff.put(characters[idx]);
		} // end of array marshaling

	} // end of marshal method

	public void setCharacters(final byte[] pCharacters) {
		characters = pCharacters;
	}

	public void setCharacterSet(final short pCharacterSet) {
		characterSet = pCharacterSet;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			characterSet = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < characters.length; idx++) {
				characters[idx] = dis.readByte();
			} // end of array unmarshaling
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
		for (int idx = 0; idx < characters.length; idx++) {
			characters[idx] = buff.get();
		} // end of array unmarshaling
	} // end of unmarshal method
} // end of class
