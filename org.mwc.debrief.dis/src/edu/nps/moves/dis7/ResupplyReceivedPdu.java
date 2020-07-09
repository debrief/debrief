package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 7.4.4. Receipt of supplies is communicated by issuing Resupply
 * Received PDU. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ResupplyReceivedPdu extends LogisticsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Entity that is receiving service. Shall be represented by Entity Identifier
	 * record (see 6.2.28)
	 */
	protected EntityID receivingEntityID = new EntityID();

	/**
	 * Entity that is supplying. Shall be represented by Entity Identifier record
	 * (see 6.2.28)
	 */
	protected EntityID supplyingEntityID = new EntityID();

	/** How many supplies are taken by receiving entity */
	protected short numberOfSupplyTypes;

	/** padding */
	protected short padding1 = (short) 0;

	/** padding */
	protected byte padding2 = (byte) 0;

	/**
	 * Type and amount of supplies for each specified supply type. See 6.2.85 for
	 * supply quantity record.
	 */
	protected List<SupplyQuantity> supplies = new ArrayList<SupplyQuantity>();

	/** Constructor */
	public ResupplyReceivedPdu() {
		setPduType((short) 7);
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

		if (!(obj instanceof ResupplyReceivedPdu))
			return false;

		final ResupplyReceivedPdu rhs = (ResupplyReceivedPdu) obj;

		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;
		if (!(supplyingEntityID.equals(rhs.supplyingEntityID)))
			ivarsEqual = false;
		if (!(numberOfSupplyTypes == rhs.numberOfSupplyTypes))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;

		for (int idx = 0; idx < supplies.size(); idx++) {
			if (!(supplies.get(idx).equals(rhs.supplies.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID
		marshalSize = marshalSize + supplyingEntityID.getMarshalledSize(); // supplyingEntityID
		marshalSize = marshalSize + 1; // numberOfSupplyTypes
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 1; // padding2
		for (int idx = 0; idx < supplies.size(); idx++) {
			final SupplyQuantity listElement = supplies.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfSupplyTypes() {
		return (short) supplies.size();
	}

	public short getPadding1() {
		return padding1;
	}

	public byte getPadding2() {
		return padding2;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	public List<SupplyQuantity> getSupplies() {
		return supplies;
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
			dos.writeByte((byte) supplies.size());
			dos.writeShort(padding1);
			dos.writeByte(padding2);

			for (int idx = 0; idx < supplies.size(); idx++) {
				final SupplyQuantity aSupplyQuantity = supplies.get(idx);
				aSupplyQuantity.marshal(dos);
			} // end of list marshalling

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
		buff.put((byte) supplies.size());
		buff.putShort(padding1);
		buff.put(padding2);

		for (int idx = 0; idx < supplies.size(); idx++) {
			final SupplyQuantity aSupplyQuantity = supplies.get(idx);
			aSupplyQuantity.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfSupplyTypes method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfSupplyTypes(final short pNumberOfSupplyTypes) {
		numberOfSupplyTypes = pNumberOfSupplyTypes;
	}

	public void setPadding1(final short pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final byte pPadding2) {
		padding2 = pPadding2;
	}

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	public void setSupplies(final List<SupplyQuantity> pSupplies) {
		supplies = pSupplies;
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
			numberOfSupplyTypes = (short) dis.readUnsignedByte();
			padding1 = dis.readShort();
			padding2 = dis.readByte();
			for (int idx = 0; idx < numberOfSupplyTypes; idx++) {
				final SupplyQuantity anX = new SupplyQuantity();
				anX.unmarshal(dis);
				supplies.add(anX);
			}

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
		numberOfSupplyTypes = (short) (buff.get() & 0xFF);
		padding1 = buff.getShort();
		padding2 = buff.get();
		for (int idx = 0; idx < numberOfSupplyTypes; idx++) {
			final SupplyQuantity anX = new SupplyQuantity();
			anX.unmarshal(buff);
			supplies.add(anX);
		}

	} // end of unmarshal method
} // end of class
