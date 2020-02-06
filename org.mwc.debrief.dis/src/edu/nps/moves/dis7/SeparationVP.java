package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Physical separation of an entity from another entity. Section 6.2.94.6
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SeparationVP extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** the identification of the Variable Parameter record. Enumeration from EBV */
	protected short recordType = (short) 2;

	/** Reason for separation. EBV */
	protected short reasonForSeparation;

	/** Whether the entity existed prior to separation EBV */
	protected short preEntityIndicator;

	/** padding */
	protected short padding1 = (short) 0;

	/** ID of parent */
	protected EntityID parentEntityID = new EntityID();

	/** padding */
	protected int padding2 = 0;

	/** Station separated from */
	protected long stationLocation = 0;

	/** Constructor */
	public SeparationVP() {
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

		if (!(obj instanceof SeparationVP))
			return false;

		final SeparationVP rhs = (SeparationVP) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(reasonForSeparation == rhs.reasonForSeparation))
			ivarsEqual = false;
		if (!(preEntityIndicator == rhs.preEntityIndicator))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(parentEntityID.equals(rhs.parentEntityID)))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(stationLocation == rhs.stationLocation))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // recordType
		marshalSize = marshalSize + 1; // reasonForSeparation
		marshalSize = marshalSize + 1; // preEntityIndicator
		marshalSize = marshalSize + 1; // padding1
		marshalSize = marshalSize + parentEntityID.getMarshalledSize(); // parentEntityID
		marshalSize = marshalSize + 2; // padding2
		marshalSize = marshalSize + 4; // stationLocation

		return marshalSize;
	}

	public short getPadding1() {
		return padding1;
	}

	public int getPadding2() {
		return padding2;
	}

	public EntityID getParentEntityID() {
		return parentEntityID;
	}

	public short getPreEntityIndicator() {
		return preEntityIndicator;
	}

	public short getReasonForSeparation() {
		return reasonForSeparation;
	}

	public short getRecordType() {
		return recordType;
	}

	public long getStationLocation() {
		return stationLocation;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) recordType);
			dos.writeByte((byte) reasonForSeparation);
			dos.writeByte((byte) preEntityIndicator);
			dos.writeByte((byte) padding1);
			parentEntityID.marshal(dos);
			dos.writeShort((short) padding2);
			dos.writeInt((int) stationLocation);
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
		buff.put((byte) recordType);
		buff.put((byte) reasonForSeparation);
		buff.put((byte) preEntityIndicator);
		buff.put((byte) padding1);
		parentEntityID.marshal(buff);
		buff.putShort((short) padding2);
		buff.putInt((int) stationLocation);
	} // end of marshal method

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final int pPadding2) {
		padding2 = pPadding2;
	}

	public void setParentEntityID(final EntityID pParentEntityID) {
		parentEntityID = pParentEntityID;
	}

	public void setPreEntityIndicator(final short pPreEntityIndicator) {
		preEntityIndicator = pPreEntityIndicator;
	}

	public void setReasonForSeparation(final short pReasonForSeparation) {
		reasonForSeparation = pReasonForSeparation;
	}

	public void setRecordType(final short pRecordType) {
		recordType = pRecordType;
	}

	public void setStationLocation(final long pStationLocation) {
		stationLocation = pStationLocation;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = (short) dis.readUnsignedByte();
			reasonForSeparation = (short) dis.readUnsignedByte();
			preEntityIndicator = (short) dis.readUnsignedByte();
			padding1 = (short) dis.readUnsignedByte();
			parentEntityID.unmarshal(dis);
			padding2 = dis.readUnsignedShort();
			stationLocation = dis.readInt();
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
		recordType = (short) (buff.get() & 0xFF);
		reasonForSeparation = (short) (buff.get() & 0xFF);
		preEntityIndicator = (short) (buff.get() & 0xFF);
		padding1 = (short) (buff.get() & 0xFF);
		parentEntityID.unmarshal(buff);
		padding2 = buff.getShort() & 0xFFFF;
		stationLocation = buff.getInt();
	} // end of unmarshal method
} // end of class
