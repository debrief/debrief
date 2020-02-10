package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Record sets, used in transfer control request PDU
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RecordSet extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** record ID */
	protected long recordID;

	/** record set serial number */
	protected long recordSetSerialNumber;

	/** record length */
	protected int recordLength;

	/** record count */
	protected int recordCount;

	/** ^^^This is wrong--variable sized data records */
	protected int recordValues;

	/** ^^^This is wrong--variable sized padding */
	protected short pad4;

	/** Constructor */
	public RecordSet() {
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

		if (!(obj instanceof RecordSet))
			return false;

		final RecordSet rhs = (RecordSet) obj;

		if (!(recordID == rhs.recordID))
			ivarsEqual = false;
		if (!(recordSetSerialNumber == rhs.recordSetSerialNumber))
			ivarsEqual = false;
		if (!(recordLength == rhs.recordLength))
			ivarsEqual = false;
		if (!(recordCount == rhs.recordCount))
			ivarsEqual = false;
		if (!(recordValues == rhs.recordValues))
			ivarsEqual = false;
		if (!(pad4 == rhs.pad4))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // recordID
		marshalSize = marshalSize + 4; // recordSetSerialNumber
		marshalSize = marshalSize + 2; // recordLength
		marshalSize = marshalSize + 2; // recordCount
		marshalSize = marshalSize + 2; // recordValues
		marshalSize = marshalSize + 1; // pad4

		return marshalSize;
	}

	public short getPad4() {
		return pad4;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public long getRecordID() {
		return recordID;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public long getRecordSetSerialNumber() {
		return recordSetSerialNumber;
	}

	public int getRecordValues() {
		return recordValues;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) recordID);
			dos.writeInt((int) recordSetSerialNumber);
			dos.writeShort((short) recordLength);
			dos.writeShort((short) recordCount);
			dos.writeShort((short) recordValues);
			dos.writeByte((byte) pad4);
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
		buff.putInt((int) recordID);
		buff.putInt((int) recordSetSerialNumber);
		buff.putShort((short) recordLength);
		buff.putShort((short) recordCount);
		buff.putShort((short) recordValues);
		buff.put((byte) pad4);
	} // end of marshal method

	public void setPad4(final short pPad4) {
		pad4 = pPad4;
	}

	public void setRecordCount(final int pRecordCount) {
		recordCount = pRecordCount;
	}

	public void setRecordID(final long pRecordID) {
		recordID = pRecordID;
	}

	public void setRecordLength(final int pRecordLength) {
		recordLength = pRecordLength;
	}

	public void setRecordSetSerialNumber(final long pRecordSetSerialNumber) {
		recordSetSerialNumber = pRecordSetSerialNumber;
	}

	public void setRecordValues(final int pRecordValues) {
		recordValues = pRecordValues;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordID = dis.readInt();
			recordSetSerialNumber = dis.readInt();
			recordLength = dis.readUnsignedShort();
			recordCount = dis.readUnsignedShort();
			recordValues = dis.readUnsignedShort();
			pad4 = (short) dis.readUnsignedByte();
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
		recordID = buff.getInt();
		recordSetSerialNumber = buff.getInt();
		recordLength = buff.getShort() & 0xFFFF;
		recordCount = buff.getShort() & 0xFFFF;
		recordValues = buff.getShort() & 0xFFFF;
		pad4 = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
