package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * First part of a simulation management (SIMAN) PDU and SIMAN-Reliability
 * (SIMAN-R) PDU. Sectionn 6.2.81
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SimulationManagementPduHeader extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Conventional PDU header */
	protected PduHeader pduHeader = new PduHeader();

	/**
	 * IDs the simulation or entity, etiehr a simulation or an entity. Either 6.2.80
	 * or 6.2.28
	 */
	protected SimulationIdentifier originatingID = new SimulationIdentifier();

	/**
	 * simulation, all simulations, a special ID, or an entity. See 5.6.5 and 5.12.4
	 */
	protected SimulationIdentifier receivingID = new SimulationIdentifier();

	/** Constructor */
	public SimulationManagementPduHeader() {
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

		if (!(obj instanceof SimulationManagementPduHeader))
			return false;

		final SimulationManagementPduHeader rhs = (SimulationManagementPduHeader) obj;

		if (!(pduHeader.equals(rhs.pduHeader)))
			ivarsEqual = false;
		if (!(originatingID.equals(rhs.originatingID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + pduHeader.getMarshalledSize(); // pduHeader
		marshalSize = marshalSize + originatingID.getMarshalledSize(); // originatingID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID

		return marshalSize;
	}

	public SimulationIdentifier getOriginatingID() {
		return originatingID;
	}

	public PduHeader getPduHeader() {
		return pduHeader;
	}

	public SimulationIdentifier getReceivingID() {
		return receivingID;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			pduHeader.marshal(dos);
			originatingID.marshal(dos);
			receivingID.marshal(dos);
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
		pduHeader.marshal(buff);
		originatingID.marshal(buff);
		receivingID.marshal(buff);
	} // end of marshal method

	public void setOriginatingID(final SimulationIdentifier pOriginatingID) {
		originatingID = pOriginatingID;
	}

	public void setPduHeader(final PduHeader pPduHeader) {
		pduHeader = pPduHeader;
	}

	public void setReceivingID(final SimulationIdentifier pReceivingID) {
		receivingID = pReceivingID;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			pduHeader.unmarshal(dis);
			originatingID.unmarshal(dis);
			receivingID.unmarshal(dis);
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
		pduHeader.unmarshal(buff);
		originatingID.unmarshal(buff);
		receivingID.unmarshal(buff);
	} // end of unmarshal method
} // end of class
