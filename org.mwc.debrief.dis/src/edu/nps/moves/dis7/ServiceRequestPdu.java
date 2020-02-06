package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Request PDU shall be used to communicate information associated with
 * one entity requesting a service from another). Section 7.4.2 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ServiceRequestPdu extends LogisticsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Entity that is requesting service (see 6.2.28), Section 7.4.2 */
	protected EntityID requestingEntityID = new EntityID();

	/** Entity that is providing the service (see 6.2.28), Section 7.4.2 */
	protected EntityID servicingEntityID = new EntityID();

	/** Type of service requested, Section 7.4.2 */
	protected short serviceTypeRequested;

	/** How many requested, Section 7.4.2 */
	protected short numberOfSupplyTypes;

	/** padding */
	protected short serviceRequestPadding = (short) 0;

	/**
	 * Field shall specify the type of supply and the amount of that supply for the
	 * number specified in the numberOfSupplyTypes (see 6.2.85), Section 7.4.2
	 */
	protected List<SupplyQuantity> supplies = new ArrayList<SupplyQuantity>();

	/** Constructor */
	public ServiceRequestPdu() {
		setPduType((short) 5);
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

		if (!(obj instanceof ServiceRequestPdu))
			return false;

		final ServiceRequestPdu rhs = (ServiceRequestPdu) obj;

		if (!(requestingEntityID.equals(rhs.requestingEntityID)))
			ivarsEqual = false;
		if (!(servicingEntityID.equals(rhs.servicingEntityID)))
			ivarsEqual = false;
		if (!(serviceTypeRequested == rhs.serviceTypeRequested))
			ivarsEqual = false;
		if (!(numberOfSupplyTypes == rhs.numberOfSupplyTypes))
			ivarsEqual = false;
		if (!(serviceRequestPadding == rhs.serviceRequestPadding))
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
		marshalSize = marshalSize + requestingEntityID.getMarshalledSize(); // requestingEntityID
		marshalSize = marshalSize + servicingEntityID.getMarshalledSize(); // servicingEntityID
		marshalSize = marshalSize + 1; // serviceTypeRequested
		marshalSize = marshalSize + 1; // numberOfSupplyTypes
		marshalSize = marshalSize + 2; // serviceRequestPadding
		for (int idx = 0; idx < supplies.size(); idx++) {
			final SupplyQuantity listElement = supplies.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfSupplyTypes() {
		return (short) supplies.size();
	}

	public EntityID getRequestingEntityID() {
		return requestingEntityID;
	}

	public short getServiceRequestPadding() {
		return serviceRequestPadding;
	}

	public short getServiceTypeRequested() {
		return serviceTypeRequested;
	}

	public EntityID getServicingEntityID() {
		return servicingEntityID;
	}

	public List<SupplyQuantity> getSupplies() {
		return supplies;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			requestingEntityID.marshal(dos);
			servicingEntityID.marshal(dos);
			dos.writeByte((byte) serviceTypeRequested);
			dos.writeByte((byte) supplies.size());
			dos.writeShort(serviceRequestPadding);

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
		requestingEntityID.marshal(buff);
		servicingEntityID.marshal(buff);
		buff.put((byte) serviceTypeRequested);
		buff.put((byte) supplies.size());
		buff.putShort(serviceRequestPadding);

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

	public void setRequestingEntityID(final EntityID pRequestingEntityID) {
		requestingEntityID = pRequestingEntityID;
	}

	public void setServiceRequestPadding(final short pServiceRequestPadding) {
		serviceRequestPadding = pServiceRequestPadding;
	}

	public void setServiceTypeRequested(final short pServiceTypeRequested) {
		serviceTypeRequested = pServiceTypeRequested;
	}

	public void setServicingEntityID(final EntityID pServicingEntityID) {
		servicingEntityID = pServicingEntityID;
	}

	public void setSupplies(final List<SupplyQuantity> pSupplies) {
		supplies = pSupplies;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			requestingEntityID.unmarshal(dis);
			servicingEntityID.unmarshal(dis);
			serviceTypeRequested = (short) dis.readUnsignedByte();
			numberOfSupplyTypes = (short) dis.readUnsignedByte();
			serviceRequestPadding = dis.readShort();
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

		requestingEntityID.unmarshal(buff);
		servicingEntityID.unmarshal(buff);
		serviceTypeRequested = (short) (buff.get() & 0xFF);
		numberOfSupplyTypes = (short) (buff.get() & 0xFF);
		serviceRequestPadding = buff.getShort();
		for (int idx = 0; idx < numberOfSupplyTypes; idx++) {
			final SupplyQuantity anX = new SupplyQuantity();
			anX.unmarshal(buff);
			supplies.add(anX);
		}

	} // end of unmarshal method
} // end of class
