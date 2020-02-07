package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * The joining of two or more simulation entities is communicated by this PDU.
 * Section 7.8.5 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IsPartOfPdu extends EntityManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of entity originating PDU */
	protected EntityID orginatingEntityID = new EntityID();

	/** ID of entity receiving PDU */
	protected EntityID receivingEntityID = new EntityID();

	/** relationship of joined parts */
	protected Relationship relationship = new Relationship();

	/**
	 * location of part; centroid of part in host's coordinate system. x=range,
	 * y=bearing, z=0
	 */
	protected Vector3Float partLocation = new Vector3Float();

	/** named location */
	protected NamedLocationIdentification namedLocationID = new NamedLocationIdentification();

	/** entity type */
	protected EntityType partEntityType = new EntityType();

	/** Constructor */
	public IsPartOfPdu() {
		setPduType((short) 36);
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

		if (!(obj instanceof IsPartOfPdu))
			return false;

		final IsPartOfPdu rhs = (IsPartOfPdu) obj;

		if (!(orginatingEntityID.equals(rhs.orginatingEntityID)))
			ivarsEqual = false;
		if (!(receivingEntityID.equals(rhs.receivingEntityID)))
			ivarsEqual = false;
		if (!(relationship.equals(rhs.relationship)))
			ivarsEqual = false;
		if (!(partLocation.equals(rhs.partLocation)))
			ivarsEqual = false;
		if (!(namedLocationID.equals(rhs.namedLocationID)))
			ivarsEqual = false;
		if (!(partEntityType.equals(rhs.partEntityType)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + orginatingEntityID.getMarshalledSize(); // orginatingEntityID
		marshalSize = marshalSize + receivingEntityID.getMarshalledSize(); // receivingEntityID
		marshalSize = marshalSize + relationship.getMarshalledSize(); // relationship
		marshalSize = marshalSize + partLocation.getMarshalledSize(); // partLocation
		marshalSize = marshalSize + namedLocationID.getMarshalledSize(); // namedLocationID
		marshalSize = marshalSize + partEntityType.getMarshalledSize(); // partEntityType

		return marshalSize;
	}

	public NamedLocationIdentification getNamedLocationID() {
		return namedLocationID;
	}

	public EntityID getOrginatingEntityID() {
		return orginatingEntityID;
	}

	public EntityType getPartEntityType() {
		return partEntityType;
	}

	public Vector3Float getPartLocation() {
		return partLocation;
	}

	public EntityID getReceivingEntityID() {
		return receivingEntityID;
	}

	public Relationship getRelationship() {
		return relationship;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			orginatingEntityID.marshal(dos);
			receivingEntityID.marshal(dos);
			relationship.marshal(dos);
			partLocation.marshal(dos);
			namedLocationID.marshal(dos);
			partEntityType.marshal(dos);
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
		orginatingEntityID.marshal(buff);
		receivingEntityID.marshal(buff);
		relationship.marshal(buff);
		partLocation.marshal(buff);
		namedLocationID.marshal(buff);
		partEntityType.marshal(buff);
	} // end of marshal method

	public void setNamedLocationID(final NamedLocationIdentification pNamedLocationID) {
		namedLocationID = pNamedLocationID;
	}

	public void setOrginatingEntityID(final EntityID pOrginatingEntityID) {
		orginatingEntityID = pOrginatingEntityID;
	}

	public void setPartEntityType(final EntityType pPartEntityType) {
		partEntityType = pPartEntityType;
	}

	public void setPartLocation(final Vector3Float pPartLocation) {
		partLocation = pPartLocation;
	}

	public void setReceivingEntityID(final EntityID pReceivingEntityID) {
		receivingEntityID = pReceivingEntityID;
	}

	public void setRelationship(final Relationship pRelationship) {
		relationship = pRelationship;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			orginatingEntityID.unmarshal(dis);
			receivingEntityID.unmarshal(dis);
			relationship.unmarshal(dis);
			partLocation.unmarshal(dis);
			namedLocationID.unmarshal(dis);
			partEntityType.unmarshal(dis);
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

		orginatingEntityID.unmarshal(buff);
		receivingEntityID.unmarshal(buff);
		relationship.unmarshal(buff);
		partLocation.unmarshal(buff);
		namedLocationID.unmarshal(buff);
		partEntityType.unmarshal(buff);
	} // end of unmarshal method
} // end of class
