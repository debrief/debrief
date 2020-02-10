package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 7.4.6. Service Request PDU is received and repair is complete.
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RepairCompletePdu extends LogisticsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Entity that is receiving service. See 6.2.28 */
	protected EntityID receivingEntityID = new EntityID();

	/** Entity that is supplying. See 6.2.28 */
	protected EntityID repairingEntityID = new EntityID();

	/** Enumeration for type of repair. See 6.2.74 */
	protected int repair;

	/** padding, number prevents conflict with superclass ivar name */
	protected short padding4 = (short) 0;

	/** Constructor */
	public RepairCompletePdu() {
		setPduType((short) 9);
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

		if (!(obj instanceof RepairCompletePdu))
			return false;

		final RepairCompletePdu rhs = (RepairCompletePdu) obj;

		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;
		if (!(repairingEntityID.equals(rhs.repairingEntityID)))
			ivarsEqual = false;
		if (!(repair == rhs.repair))
			ivarsEqual = false;
		if (!(padding4 == rhs.padding4))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID
		marshalSize = marshalSize + repairingEntityID.getMarshalledSize(); // repairingEntityID
		marshalSize = marshalSize + 2; // repair
		marshalSize = marshalSize + 2; // padding4

		return marshalSize;
	}

	public short getPadding4() {
		return padding4;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	public int getRepair() {
		return repair;
	}

	public EntityID getRepairingEntityID() {
		return repairingEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			receivingEntityID.marshal(dos);
			repairingEntityID.marshal(dos);
			dos.writeShort((short) repair);
			dos.writeShort(padding4);
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
		buff.putShort((short) repair);
		buff.putShort(padding4);
	} // end of marshal method

	public void setPadding4(final short pPadding4) {
		padding4 = pPadding4;
	}

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	public void setRepair(final int pRepair) {
		repair = pRepair;
	}

	public void setRepairingEntityID(final EntityID pRepairingEntityID) {
		repairingEntityID = pRepairingEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			receivingEntityID.unmarshal(dis);
			repairingEntityID.unmarshal(dis);
			repair = dis.readUnsignedShort();
			padding4 = dis.readShort();
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
		repair = buff.getShort() & 0xFFFF;
		padding4 = buff.getShort();
	} // end of unmarshal method
} // end of class
