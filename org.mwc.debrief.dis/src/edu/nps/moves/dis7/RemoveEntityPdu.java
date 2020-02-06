package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 7.5.3 The removal of an entity from an exercise shall be communicated
 * with a Remove Entity PDU. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RemoveEntityPdu extends SimulationManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Identifier for originating entity(or simulation) */
	protected EntityID originatingID = new EntityID();

	/** Identifier for the receiving entity(or simulation) */
	protected EntityID receivingID = new EntityID();

	/**
	 * This field shall identify the specific and unique start/resume request being
	 * made by the SM
	 */
	protected long requestID;

	/** Constructor */
	public RemoveEntityPdu() {
		setPduType((short) 12);
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

		if (!(obj instanceof RemoveEntityPdu))
			return false;

		final RemoveEntityPdu rhs = (RemoveEntityPdu) obj;

		if (!(originatingID.equals(rhs.originatingID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + originatingID.getMarshalledSize(); // originatingID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID
		marshalSize = marshalSize + 4; // requestID

		return marshalSize;
	}

	public EntityID getOriginatingID() {
		return originatingID;
	}

	public EntityID getReceivingID() {
		return receivingID;
	}

	public long getRequestID() {
		return requestID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			originatingID.marshal(dos);
			receivingID.marshal(dos);
			dos.writeInt((int) requestID);
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
		originatingID.marshal(buff);
		receivingID.marshal(buff);
		buff.putInt((int) requestID);
	} // end of marshal method

	public void setOriginatingID(final EntityID pOriginatingID) {
		originatingID = pOriginatingID;
	}

	public void setReceivingID(final EntityID pReceivingID) {
		receivingID = pReceivingID;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			originatingID.unmarshal(dis);
			receivingID.unmarshal(dis);
			requestID = dis.readInt();
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

		originatingID.unmarshal(buff);
		receivingID.unmarshal(buff);
		requestID = buff.getInt();
	} // end of unmarshal method
} // end of class
