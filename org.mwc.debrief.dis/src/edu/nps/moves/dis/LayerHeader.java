package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.2.47. Layer header.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class LayerHeader extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Layer number */
	protected short layerNumber;

	/** Layer speccific information enumeration */
	protected short layerSpecificInformaiton;

	/** information length */
	protected int length;

	/** Constructor */
	public LayerHeader() {
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

		if (!(obj instanceof LayerHeader))
			return false;

		final LayerHeader rhs = (LayerHeader) obj;

		if (!(layerNumber == rhs.layerNumber))
			ivarsEqual = false;
		if (!(layerSpecificInformaiton == rhs.layerSpecificInformaiton))
			ivarsEqual = false;
		if (!(length == rhs.length))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getLayerNumber() {
		return layerNumber;
	}

	public short getLayerSpecificInformaiton() {
		return layerSpecificInformaiton;
	}

	public int getLength() {
		return length;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // layerNumber
		marshalSize = marshalSize + 1; // layerSpecificInformaiton
		marshalSize = marshalSize + 2; // length

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) layerNumber);
			dos.writeByte((byte) layerSpecificInformaiton);
			dos.writeShort((short) length);
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
		buff.put((byte) layerNumber);
		buff.put((byte) layerSpecificInformaiton);
		buff.putShort((short) length);
	} // end of marshal method

	public void setLayerNumber(final short pLayerNumber) {
		layerNumber = pLayerNumber;
	}

	public void setLayerSpecificInformaiton(final short pLayerSpecificInformaiton) {
		layerSpecificInformaiton = pLayerSpecificInformaiton;
	}

	public void setLength(final int pLength) {
		length = pLength;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			layerNumber = (short) dis.readUnsignedByte();
			layerSpecificInformaiton = (short) dis.readUnsignedByte();
			length = dis.readUnsignedShort();
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
		layerNumber = (short) (buff.get() & 0xFF);
		layerSpecificInformaiton = (short) (buff.get() & 0xFF);
		length = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
