package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.3.4. Stop or freeze an exercise. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class StopFreezePdu extends SimulationManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** UTC time at which the simulation shall stop or freeze */
	protected ClockTime realWorldTime = new ClockTime();

	/** Reason the simulation was stopped or frozen */
	protected short reason;

	/**
	 * Internal behavior of the simulation and its appearance while frozento the
	 * other participants
	 */
	protected short frozenBehavior;

	/** padding */
	protected short padding1 = (short) 0;

	/** Request ID that is unique */
	protected long requestID;

	/** Constructor */
	public StopFreezePdu() {
		setPduType((short) 14);
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

		if (!(obj instanceof StopFreezePdu))
			return false;

		final StopFreezePdu rhs = (StopFreezePdu) obj;

		if (!(realWorldTime.equals(rhs.realWorldTime)))
			ivarsEqual = false;
		if (!(reason == rhs.reason))
			ivarsEqual = false;
		if (!(frozenBehavior == rhs.frozenBehavior))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public short getFrozenBehavior() {
		return frozenBehavior;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + realWorldTime.getMarshalledSize(); // realWorldTime
		marshalSize = marshalSize + 1; // reason
		marshalSize = marshalSize + 1; // frozenBehavior
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 4; // requestID

		return marshalSize;
	}

	public short getPadding1() {
		return padding1;
	}

	public ClockTime getRealWorldTime() {
		return realWorldTime;
	}

	public short getReason() {
		return reason;
	}

	public long getRequestID() {
		return requestID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			realWorldTime.marshal(dos);
			dos.writeByte((byte) reason);
			dos.writeByte((byte) frozenBehavior);
			dos.writeShort(padding1);
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
		realWorldTime.marshal(buff);
		buff.put((byte) reason);
		buff.put((byte) frozenBehavior);
		buff.putShort(padding1);
		buff.putInt((int) requestID);
	} // end of marshal method

	public void setFrozenBehavior(final short pFrozenBehavior) {
		frozenBehavior = pFrozenBehavior;
	}

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setRealWorldTime(final ClockTime pRealWorldTime) {
		realWorldTime = pRealWorldTime;
	}

	public void setReason(final short pReason) {
		reason = pReason;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			realWorldTime.unmarshal(dis);
			reason = (short) dis.readUnsignedByte();
			frozenBehavior = (short) dis.readUnsignedByte();
			padding1 = dis.readShort();
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

		realWorldTime.unmarshal(buff);
		reason = (short) (buff.get() & 0xFF);
		frozenBehavior = (short) (buff.get() & 0xFF);
		padding1 = buff.getShort();
		requestID = buff.getInt();
	} // end of unmarshal method
} // end of class
