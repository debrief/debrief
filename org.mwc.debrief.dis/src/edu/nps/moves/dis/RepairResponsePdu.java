package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.5.6. Sent after repair complete PDU. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RepairResponsePdu extends LogisticsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Entity that is receiving service */
	protected EntityID receivingEntityID = new EntityID();

	/** Entity that is supplying */
	protected EntityID repairingEntityID = new EntityID();

	/** Result of repair operation */
	protected short repairResult;

	/** padding */
	protected short padding1 = (short) 0;

	/** padding */
	protected byte padding2 = (byte) 0;

	/** Constructor */
	public RepairResponsePdu() {
		setPduType((short) 10);
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

		if (!(obj instanceof RepairResponsePdu))
			return false;

		final RepairResponsePdu rhs = (RepairResponsePdu) obj;

		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;
		if (!(repairingEntityID.equals(rhs.repairingEntityID)))
			ivarsEqual = false;
		if (!(repairResult == rhs.repairResult))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID
		marshalSize = marshalSize + repairingEntityID.getMarshalledSize(); // repairingEntityID
		marshalSize = marshalSize + 1; // repairResult
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 1; // padding2

		return marshalSize;
	}

	public short getPadding1() {
		return padding1;
	}

	public byte getPadding2() {
		return padding2;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	public EntityID getRepairingEntityID() {
		return repairingEntityID;
	}

	public short getRepairResult() {
		return repairResult;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			receivingEntityID.marshal(dos);
			repairingEntityID.marshal(dos);
			dos.writeByte((byte) repairResult);
			dos.writeShort(padding1);
			dos.writeByte(padding2);
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
		receivingEntityID.marshal(buff);
		repairingEntityID.marshal(buff);
		buff.put((byte) repairResult);
		buff.putShort(padding1);
		buff.put(padding2);
	} // end of marshal method

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final byte pPadding2) {
		padding2 = pPadding2;
	}

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	public void setRepairingEntityID(final EntityID pRepairingEntityID) {
		repairingEntityID = pRepairingEntityID;
	}

	public void setRepairResult(final short pRepairResult) {
		repairResult = pRepairResult;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			receivingEntityID.unmarshal(dis);
			repairingEntityID.unmarshal(dis);
			repairResult = (short) dis.readUnsignedByte();
			padding1 = dis.readShort();
			padding2 = dis.readByte();
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

		receivingEntityID.unmarshal(buff);
		repairingEntityID.unmarshal(buff);
		repairResult = (short) (buff.get() & 0xFF);
		padding1 = buff.getShort();
		padding2 = buff.get();
	} // end of unmarshal method
} // end of class
