package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * The identification of the additional information layer number, layer-specific
 * information, and the length of the layer. Section 6.2.51
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

	protected short layerNumber;

	/**
	 * field shall specify layer-specific information that varies by System Type
	 * (see 6.2.86) and Layer Number.
	 */
	protected short layerSpecificInformation;

	/**
	 * This field shall specify the length in octets of the layer, including the
	 * Layer Header record
	 */
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
		if (!(layerSpecificInformation == rhs.layerSpecificInformation))
			ivarsEqual = false;
		if (!(length == rhs.length))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getLayerNumber() {
		return layerNumber;
	}

	public short getLayerSpecificInformation() {
		return layerSpecificInformation;
	}

	public int getLength() {
		return length;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // layerNumber
		marshalSize = marshalSize + 1; // layerSpecificInformation
		marshalSize = marshalSize + 2; // length

		return marshalSize;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) layerNumber);
			dos.writeByte((byte) layerSpecificInformation);
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
		buff.put((byte) layerSpecificInformation);
		buff.putShort((short) length);
	} // end of marshal method

	public void setLayerNumber(final short pLayerNumber) {
		layerNumber = pLayerNumber;
	}

	public void setLayerSpecificInformation(final short pLayerSpecificInformation) {
		layerSpecificInformation = pLayerSpecificInformation;
	}

	public void setLength(final int pLength) {
		length = pLength;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			layerNumber = (short) dis.readUnsignedByte();
			layerSpecificInformation = (short) dis.readUnsignedByte();
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
		layerSpecificInformation = (short) (buff.get() & 0xFF);
		length = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
