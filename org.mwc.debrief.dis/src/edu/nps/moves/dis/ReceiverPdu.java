package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.8.3. Communication of a receiver state. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ReceiverPdu extends RadioCommunicationsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ID of the entity that is the source of the communication, ie contains the
	 * radio
	 */
	protected EntityID entityId = new EntityID();

	/** particular radio within an entity */
	protected int radioId;

	/** encoding scheme used, and enumeration */
	protected int receiverState;

	/** padding */
	protected int padding1;

	/** received power */
	protected float receivedPower;

	/** ID of transmitter */
	protected EntityID transmitterEntityId = new EntityID();

	/** ID of transmitting radio */
	protected int transmitterRadioId;

	/** Constructor */
	public ReceiverPdu() {
		setPduType((short) 27);
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

		if (!(obj instanceof ReceiverPdu))
			return false;

		final ReceiverPdu rhs = (ReceiverPdu) obj;

		if (!(entityId.equals(rhs.entityId)))
			ivarsEqual = false;
		if (!(radioId == rhs.radioId))
			ivarsEqual = false;
		if (!(receiverState == rhs.receiverState))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(receivedPower == rhs.receivedPower))
			ivarsEqual = false;
		if (!(transmitterEntityId.equals(rhs.transmitterEntityId)))
			ivarsEqual = false;
		if (!(transmitterRadioId == rhs.transmitterRadioId))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getEntityId() {
		return entityId;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + entityId.getMarshalledSize(); // entityId
		marshalSize = marshalSize + 2; // radioId
		marshalSize = marshalSize + 2; // receiverState
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 4; // receivedPower
		marshalSize = marshalSize + transmitterEntityId.getMarshalledSize(); // transmitterEntityId
		marshalSize = marshalSize + 2; // transmitterRadioId

		return marshalSize;
	}

	public int getPadding1() {
		return padding1;
	}

	public int getRadioId() {
		return radioId;
	}

	public float getReceivedPower() {
		return receivedPower;
	}

	public int getReceiverState() {
		return receiverState;
	}

	public EntityID getTransmitterEntityId() {
		return transmitterEntityId;
	}

	public int getTransmitterRadioId() {
		return transmitterRadioId;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			entityId.marshal(dos);
			dos.writeShort((short) radioId);
			dos.writeShort((short) receiverState);
			dos.writeShort((short) padding1);
			dos.writeFloat(receivedPower);
			transmitterEntityId.marshal(dos);
			dos.writeShort((short) transmitterRadioId);
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
		entityId.marshal(buff);
		buff.putShort((short) radioId);
		buff.putShort((short) receiverState);
		buff.putShort((short) padding1);
		buff.putFloat(receivedPower);
		transmitterEntityId.marshal(buff);
		buff.putShort((short) transmitterRadioId);
	} // end of marshal method

	public void setEntityId(final EntityID pEntityId) {
		entityId = pEntityId;
	}

	public void setPadding1(final int pPadding1) {
		padding1 = pPadding1;
	}

	public void setRadioId(final int pRadioId) {
		radioId = pRadioId;
	}

	public void setReceivedPower(final float pReceivedPower) {
		receivedPower = pReceivedPower;
	}

	public void setReceiverState(final int pReceiverState) {
		receiverState = pReceiverState;
	}

	public void setTransmitterEntityId(final EntityID pTransmitterEntityId) {
		transmitterEntityId = pTransmitterEntityId;
	}

	public void setTransmitterRadioId(final int pTransmitterRadioId) {
		transmitterRadioId = pTransmitterRadioId;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			entityId.unmarshal(dis);
			radioId = dis.readUnsignedShort();
			receiverState = dis.readUnsignedShort();
			padding1 = dis.readUnsignedShort();
			receivedPower = dis.readFloat();
			transmitterEntityId.unmarshal(dis);
			transmitterRadioId = dis.readUnsignedShort();
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

		entityId.unmarshal(buff);
		radioId = buff.getShort() & 0xFFFF;
		receiverState = buff.getShort() & 0xFFFF;
		padding1 = buff.getShort() & 0xFFFF;
		receivedPower = buff.getFloat();
		transmitterEntityId.unmarshal(buff);
		transmitterRadioId = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
