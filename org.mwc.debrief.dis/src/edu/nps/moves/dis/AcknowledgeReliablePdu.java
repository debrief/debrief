package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.12.5: Ack receipt of a start-resume, stop-freeze, create-entity
 * or remove enitty (reliable) pdus. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AcknowledgeReliablePdu extends SimulationManagementWithReliabilityFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ack flags */
	protected int acknowledgeFlag;

	/** response flags */
	protected int responseFlag;

	/** Request ID */
	protected long requestID;

	/** Constructor */
	public AcknowledgeReliablePdu() {
		setPduType((short) 55);
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

		if (!(obj instanceof AcknowledgeReliablePdu))
			return false;

		final AcknowledgeReliablePdu rhs = (AcknowledgeReliablePdu) obj;

		if (!(acknowledgeFlag == rhs.acknowledgeFlag))
			ivarsEqual = false;
		if (!(responseFlag == rhs.responseFlag))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public int getAcknowledgeFlag() {
		return acknowledgeFlag;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // acknowledgeFlag
		marshalSize = marshalSize + 2; // responseFlag
		marshalSize = marshalSize + 4; // requestID

		return marshalSize;
	}

	public long getRequestID() {
		return requestID;
	}

	public int getResponseFlag() {
		return responseFlag;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeShort((short) acknowledgeFlag);
			dos.writeShort((short) responseFlag);
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
		buff.putShort((short) acknowledgeFlag);
		buff.putShort((short) responseFlag);
		buff.putInt((int) requestID);
	} // end of marshal method

	public void setAcknowledgeFlag(final int pAcknowledgeFlag) {
		acknowledgeFlag = pAcknowledgeFlag;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setResponseFlag(final int pResponseFlag) {
		responseFlag = pResponseFlag;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			acknowledgeFlag = dis.readUnsignedShort();
			responseFlag = dis.readUnsignedShort();
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

		acknowledgeFlag = buff.getShort() & 0xFFFF;
		responseFlag = buff.getShort() & 0xFFFF;
		requestID = buff.getInt();
	} // end of unmarshal method
} // end of class
