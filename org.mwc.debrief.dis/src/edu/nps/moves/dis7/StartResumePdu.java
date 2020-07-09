package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 7.5.4. Start or resume an exercise. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class StartResumePdu extends SimulationManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Identifier for originating entity(or simulation) */
	protected EntityID originatingID = new EntityID();

	/** Identifier for the receiving entity(or simulation) */
	protected EntityID receivingID = new EntityID();

	/**
	 * This field shall specify the real-world time (UTC) at which the entity is to
	 * start/resume in the exercise. This information shall be used by the
	 * participating simulation applications to start/resume an exercise
	 * synchronously. This field shall be represented by a Clock Time record (see
	 * 6.2.16).
	 */
	protected ClockTime realWorldTime = new ClockTime();

	/**
	 * The reference time within a simulation exercise. This time is established
	 * ahead of time by simulation management and is common to all participants in a
	 * particular exercise. Simulation time may be either Absolute Time or Relative
	 * Time. This field shall be represented by a Clock Time record (see 6.2.16)
	 */
	protected ClockTime simulationTime = new ClockTime();

	/** Identifier for the specific and unique start/resume request */
	protected long requestID;

	/** Constructor */
	public StartResumePdu() {
		setPduType((short) 13);
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

		if (!(obj instanceof StartResumePdu))
			return false;

		final StartResumePdu rhs = (StartResumePdu) obj;

		if (!(originatingID.equals(rhs.originatingID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;
		if (!(realWorldTime.equals(rhs.realWorldTime)))
			ivarsEqual = false;
		if (!(simulationTime.equals(rhs.simulationTime)))
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
		marshalSize = marshalSize + realWorldTime.getMarshalledSize(); // realWorldTime
		marshalSize = marshalSize + simulationTime.getMarshalledSize(); // simulationTime
		marshalSize = marshalSize + 4; // requestID

		return marshalSize;
	}

	public EntityID getOriginatingID() {
		return originatingID;
	}

	public ClockTime getRealWorldTime() {
		return realWorldTime;
	}

	public EntityID getReceivingID() {
		return receivingID;
	}

	public long getRequestID() {
		return requestID;
	}

	public ClockTime getSimulationTime() {
		return simulationTime;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			originatingID.marshal(dos);
			receivingID.marshal(dos);
			realWorldTime.marshal(dos);
			simulationTime.marshal(dos);
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
		realWorldTime.marshal(buff);
		simulationTime.marshal(buff);
		buff.putInt((int) requestID);
	} // end of marshal method

	public void setOriginatingID(final EntityID pOriginatingID) {
		originatingID = pOriginatingID;
	}

	public void setRealWorldTime(final ClockTime pRealWorldTime) {
		realWorldTime = pRealWorldTime;
	}

	public void setReceivingID(final EntityID pReceivingID) {
		receivingID = pReceivingID;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setSimulationTime(final ClockTime pSimulationTime) {
		simulationTime = pSimulationTime;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			originatingID.unmarshal(dis);
			receivingID.unmarshal(dis);
			realWorldTime.unmarshal(dis);
			simulationTime.unmarshal(dis);
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
		realWorldTime.unmarshal(buff);
		simulationTime.unmarshal(buff);
		requestID = buff.getInt();
	} // end of unmarshal method
} // end of class
