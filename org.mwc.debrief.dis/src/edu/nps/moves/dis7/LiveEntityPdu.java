package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * The live entity PDUs have a header with some different field names, but the
 * same length. Section 9.3.2
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class LiveEntityPdu extends PduSuperclass implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Subprotocol used to decode the PDU. Section 13 of EBV. */
	protected int subprotocolNumber;

	/** zero-filled array of padding */
	protected short padding = (short) 0;

	/** Constructor */
	public LiveEntityPdu() {
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

	@Override
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof LiveEntityPdu))
			return false;

		final LiveEntityPdu rhs = (LiveEntityPdu) obj;

		if (!(subprotocolNumber == rhs.subprotocolNumber))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // subprotocolNumber
		marshalSize = marshalSize + 1; // padding

		return marshalSize;
	}

	public short getPadding() {
		return padding;
	}

	public int getSubprotocolNumber() {
		return subprotocolNumber;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeShort((short) subprotocolNumber);
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
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		buff.putShort((short) subprotocolNumber);
		buff.put((byte) padding);
	} // end of marshal method

	public void setPadding(final short pPadding) {
		padding = pPadding;
	}

	public void setSubprotocolNumber(final int pSubprotocolNumber) {
		subprotocolNumber = pSubprotocolNumber;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			subprotocolNumber = dis.readUnsignedShort();
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
	@Override
	public void unmarshal(final java.nio.ByteBuffer buff) {
		super.unmarshal(buff);

		subprotocolNumber = buff.getShort() & 0xFFFF;
		padding = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
