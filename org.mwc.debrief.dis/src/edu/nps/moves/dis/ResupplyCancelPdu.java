package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.5.4. Cancel of resupply by either the receiving or supplying
 * entity. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ResupplyCancelPdu extends LogisticsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Entity that is receiving service */
	protected EntityID receivingEntityID = new EntityID();

	/** Entity that is supplying */
	protected EntityID supplyingEntityID = new EntityID();

	/** Constructor */
	public ResupplyCancelPdu() {
		setPduType((short) 8);
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

		if (!(obj instanceof ResupplyCancelPdu))
			return false;

		final ResupplyCancelPdu rhs = (ResupplyCancelPdu) obj;

		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;
		if (!(supplyingEntityID.equals(rhs.supplyingEntityID)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID
		marshalSize = marshalSize + supplyingEntityID.getMarshalledSize(); // supplyingEntityID

		return marshalSize;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	public EntityID getSupplyingEntityID() {
		return supplyingEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			receivingEntityID.marshal(dos);
			supplyingEntityID.marshal(dos);
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
		supplyingEntityID.marshal(buff);
	} // end of marshal method

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	public void setSupplyingEntityID(final EntityID pSupplyingEntityID) {
		supplyingEntityID = pSupplyingEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			receivingEntityID.unmarshal(dis);
			supplyingEntityID.unmarshal(dis);
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
		supplyingEntityID.unmarshal(buff);
	} // end of unmarshal method
} // end of class
