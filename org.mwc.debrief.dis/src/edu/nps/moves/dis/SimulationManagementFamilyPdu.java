package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.6. Abstract superclass for PDUs relating to the simulation
 * itself. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SimulationManagementFamilyPdu extends Pdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Entity that is sending message */
	protected EntityID originatingEntityID = new EntityID();

	/** Entity that is intended to receive message */
	protected EntityID receivingEntityID = new EntityID();

	/** Constructor */
	public SimulationManagementFamilyPdu() {
		setProtocolFamily((short) 5);
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

		if (!(obj instanceof SimulationManagementFamilyPdu))
			return false;

		final SimulationManagementFamilyPdu rhs = (SimulationManagementFamilyPdu) obj;

		if (!(originatingEntityID.equals(rhs.originatingEntityID)))
			ivarsEqual = false;
		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + originatingEntityID.getMarshalledSize(); // originatingEntityID
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID

		return marshalSize;
	}

	public EntityID getOriginatingEntityID() {
		return originatingEntityID;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			originatingEntityID.marshal(dos);
			receivingEntityID.marshal(dos);
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
		originatingEntityID.marshal(buff);
		receivingEntityID.marshal(buff);
	} // end of marshal method

	public void setOriginatingEntityID(final EntityID pOriginatingEntityID) {
		originatingEntityID = pOriginatingEntityID;
	}

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			originatingEntityID.unmarshal(dis);
			receivingEntityID.unmarshal(dis);
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

		originatingEntityID.unmarshal(buff);
		receivingEntityID.unmarshal(buff);
	} // end of unmarshal method
} // end of class
