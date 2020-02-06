package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.2.44: Grid data record, a common abstract superclass for several subtypes
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class GridAxisRecord extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** type of environmental sample */
	protected int sampleType;

	/** value that describes data representation */
	protected int dataRepresentation;

	/** Constructor */
	public GridAxisRecord() {
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

		if (!(obj instanceof GridAxisRecord))
			return false;

		final GridAxisRecord rhs = (GridAxisRecord) obj;

		if (!(sampleType == rhs.sampleType))
			ivarsEqual = false;
		if (!(dataRepresentation == rhs.dataRepresentation))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getDataRepresentation() {
		return dataRepresentation;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // sampleType
		marshalSize = marshalSize + 2; // dataRepresentation

		return marshalSize;
	}

	public int getSampleType() {
		return sampleType;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) sampleType);
			dos.writeShort((short) dataRepresentation);
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
		buff.putShort((short) sampleType);
		buff.putShort((short) dataRepresentation);
	} // end of marshal method

	public void setDataRepresentation(final int pDataRepresentation) {
		dataRepresentation = pDataRepresentation;
	}

	public void setSampleType(final int pSampleType) {
		sampleType = pSampleType;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			sampleType = dis.readUnsignedShort();
			dataRepresentation = dis.readUnsignedShort();
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
		sampleType = buff.getShort() & 0xFFFF;
		dataRepresentation = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
