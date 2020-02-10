package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Communication of a receiver state. Section 7.7.4 COMPLETE
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

	/** encoding scheme used, and enumeration */
	protected int receiverState;

	/** padding */
	protected int padding1;

	/** received power */
	protected float receivedPoser;

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

		if (!(receiverState == rhs.receiverState))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(receivedPoser == rhs.receivedPoser))
			ivarsEqual = false;
		if (!(transmitterEntityId.equals(rhs.transmitterEntityId)))
			ivarsEqual = false;
		if (!(transmitterRadioId == rhs.transmitterRadioId))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // receiverState
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 4; // receivedPoser
		marshalSize = marshalSize + transmitterEntityId.getMarshalledSize(); // transmitterEntityId
		marshalSize = marshalSize + 2; // transmitterRadioId

		return marshalSize;
	}

	public int getPadding1() {
		return padding1;
	}

	public float getReceivedPoser() {
		return receivedPoser;
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
			dos.writeShort((short) receiverState);
			dos.writeShort((short) padding1);
			dos.writeFloat(receivedPoser);
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
		buff.putShort((short) receiverState);
		buff.putShort((short) padding1);
		buff.putFloat(receivedPoser);
		transmitterEntityId.marshal(buff);
		buff.putShort((short) transmitterRadioId);
	} // end of marshal method

	public void setPadding1(final int pPadding1) {
		padding1 = pPadding1;
	}

	public void setReceivedPoser(final float pReceivedPoser) {
		receivedPoser = pReceivedPoser;
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
			receiverState = dis.readUnsignedShort();
			padding1 = dis.readUnsignedShort();
			receivedPoser = dis.readFloat();
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

		receiverState = buff.getShort() & 0xFFFF;
		padding1 = buff.getShort() & 0xFFFF;
		receivedPoser = buff.getFloat();
		transmitterEntityId.unmarshal(buff);
		transmitterRadioId = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
