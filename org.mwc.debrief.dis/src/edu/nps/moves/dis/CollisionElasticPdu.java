package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.3.3.3. Information about elastic collisions in a DIS exercise shall be
 * communicated using a Collision-Elastic PDU. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class CollisionElasticPdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the entity that issued the collision PDU */
	protected EntityID issuingEntityID = new EntityID();

	/** ID of entity that has collided with the issuing entity ID */
	protected EntityID collidingEntityID = new EntityID();

	/** ID of event */
	protected EventID collisionEventID = new EventID();

	/** some padding */
	protected short pad = (short) 0;

	/** velocity at collision */
	protected Vector3Float contactVelocity = new Vector3Float();

	/** mass of issuing entity */
	protected float mass;

	/** Location with respect to entity the issuing entity collided with */
	protected Vector3Float location = new Vector3Float();

	/** tensor values */
	protected float collisionResultXX;

	/** tensor values */
	protected float collisionResultXY;

	/** tensor values */
	protected float collisionResultXZ;

	/** tensor values */
	protected float collisionResultYY;

	/** tensor values */
	protected float collisionResultYZ;

	/** tensor values */
	protected float collisionResultZZ;

	/**
	 * This record shall represent the normal vector to the surface at the point of
	 * collision detection. The surface normal shall be represented in world
	 * coordinates.
	 */
	protected Vector3Float unitSurfaceNormal = new Vector3Float();

	/**
	 * This field shall represent the degree to which energy is conserved in a
	 * collision
	 */
	protected float coefficientOfRestitution;

	/** Constructor */
	public CollisionElasticPdu() {
		setPduType((short) 66);
		setProtocolFamily((short) 1);
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

		if (!(obj instanceof CollisionElasticPdu))
			return false;

		final CollisionElasticPdu rhs = (CollisionElasticPdu) obj;

		if (!(issuingEntityID.equals(rhs.issuingEntityID)))
			ivarsEqual = false;
		if (!(collidingEntityID.equals(rhs.collidingEntityID)))
			ivarsEqual = false;
		if (!(collisionEventID.equals(rhs.collisionEventID)))
			ivarsEqual = false;
		if (!(pad == rhs.pad))
			ivarsEqual = false;
		if (!(contactVelocity.equals(rhs.contactVelocity)))
			ivarsEqual = false;
		if (!(mass == rhs.mass))
			ivarsEqual = false;
		if (!(location.equals(rhs.location)))
			ivarsEqual = false;
		if (!(collisionResultXX == rhs.collisionResultXX))
			ivarsEqual = false;
		if (!(collisionResultXY == rhs.collisionResultXY))
			ivarsEqual = false;
		if (!(collisionResultXZ == rhs.collisionResultXZ))
			ivarsEqual = false;
		if (!(collisionResultYY == rhs.collisionResultYY))
			ivarsEqual = false;
		if (!(collisionResultYZ == rhs.collisionResultYZ))
			ivarsEqual = false;
		if (!(collisionResultZZ == rhs.collisionResultZZ))
			ivarsEqual = false;
		if (!(unitSurfaceNormal.equals(rhs.unitSurfaceNormal)))
			ivarsEqual = false;
		if (!(coefficientOfRestitution == rhs.coefficientOfRestitution))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public float getCoefficientOfRestitution() {
		return coefficientOfRestitution;
	}

	public EntityID getCollidingEntityID() {
		return collidingEntityID;
	}

	public EventID getCollisionEventID() {
		return collisionEventID;
	}

	public float getCollisionResultXX() {
		return collisionResultXX;
	}

	public float getCollisionResultXY() {
		return collisionResultXY;
	}

	public float getCollisionResultXZ() {
		return collisionResultXZ;
	}

	public float getCollisionResultYY() {
		return collisionResultYY;
	}

	public float getCollisionResultYZ() {
		return collisionResultYZ;
	}

	public float getCollisionResultZZ() {
		return collisionResultZZ;
	}

	public Vector3Float getContactVelocity() {
		return contactVelocity;
	}

	public EntityID getIssuingEntityID() {
		return issuingEntityID;
	}

	public Vector3Float getLocation() {
		return location;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + issuingEntityID.getMarshalledSize(); // issuingEntityID
		marshalSize = marshalSize + collidingEntityID.getMarshalledSize(); // collidingEntityID
		marshalSize = marshalSize + collisionEventID.getMarshalledSize(); // collisionEventID
		marshalSize = marshalSize + 2; // pad
		marshalSize = marshalSize + contactVelocity.getMarshalledSize(); // contactVelocity
		marshalSize = marshalSize + 4; // mass
		marshalSize = marshalSize + location.getMarshalledSize(); // location
		marshalSize = marshalSize + 4; // collisionResultXX
		marshalSize = marshalSize + 4; // collisionResultXY
		marshalSize = marshalSize + 4; // collisionResultXZ
		marshalSize = marshalSize + 4; // collisionResultYY
		marshalSize = marshalSize + 4; // collisionResultYZ
		marshalSize = marshalSize + 4; // collisionResultZZ
		marshalSize = marshalSize + unitSurfaceNormal.getMarshalledSize(); // unitSurfaceNormal
		marshalSize = marshalSize + 4; // coefficientOfRestitution

		return marshalSize;
	}

	public float getMass() {
		return mass;
	}

	public short getPad() {
		return pad;
	}

	public Vector3Float getUnitSurfaceNormal() {
		return unitSurfaceNormal;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			issuingEntityID.marshal(dos);
			collidingEntityID.marshal(dos);
			collisionEventID.marshal(dos);
			dos.writeShort(pad);
			contactVelocity.marshal(dos);
			dos.writeFloat(mass);
			location.marshal(dos);
			dos.writeFloat(collisionResultXX);
			dos.writeFloat(collisionResultXY);
			dos.writeFloat(collisionResultXZ);
			dos.writeFloat(collisionResultYY);
			dos.writeFloat(collisionResultYZ);
			dos.writeFloat(collisionResultZZ);
			unitSurfaceNormal.marshal(dos);
			dos.writeFloat(coefficientOfRestitution);
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
		issuingEntityID.marshal(buff);
		collidingEntityID.marshal(buff);
		collisionEventID.marshal(buff);
		buff.putShort(pad);
		contactVelocity.marshal(buff);
		buff.putFloat(mass);
		location.marshal(buff);
		buff.putFloat(collisionResultXX);
		buff.putFloat(collisionResultXY);
		buff.putFloat(collisionResultXZ);
		buff.putFloat(collisionResultYY);
		buff.putFloat(collisionResultYZ);
		buff.putFloat(collisionResultZZ);
		unitSurfaceNormal.marshal(buff);
		buff.putFloat(coefficientOfRestitution);
	} // end of marshal method

	public void setCoefficientOfRestitution(final float pCoefficientOfRestitution) {
		coefficientOfRestitution = pCoefficientOfRestitution;
	}

	public void setCollidingEntityID(final EntityID pCollidingEntityID) {
		collidingEntityID = pCollidingEntityID;
	}

	public void setCollisionEventID(final EventID pCollisionEventID) {
		collisionEventID = pCollisionEventID;
	}

	public void setCollisionResultXX(final float pCollisionResultXX) {
		collisionResultXX = pCollisionResultXX;
	}

	public void setCollisionResultXY(final float pCollisionResultXY) {
		collisionResultXY = pCollisionResultXY;
	}

	public void setCollisionResultXZ(final float pCollisionResultXZ) {
		collisionResultXZ = pCollisionResultXZ;
	}

	public void setCollisionResultYY(final float pCollisionResultYY) {
		collisionResultYY = pCollisionResultYY;
	}

	public void setCollisionResultYZ(final float pCollisionResultYZ) {
		collisionResultYZ = pCollisionResultYZ;
	}

	public void setCollisionResultZZ(final float pCollisionResultZZ) {
		collisionResultZZ = pCollisionResultZZ;
	}

	public void setContactVelocity(final Vector3Float pContactVelocity) {
		contactVelocity = pContactVelocity;
	}

	public void setIssuingEntityID(final EntityID pIssuingEntityID) {
		issuingEntityID = pIssuingEntityID;
	}

	public void setLocation(final Vector3Float pLocation) {
		location = pLocation;
	}

	public void setMass(final float pMass) {
		mass = pMass;
	}

	public void setPad(final short pPad) {
		pad = pPad;
	}

	public void setUnitSurfaceNormal(final Vector3Float pUnitSurfaceNormal) {
		unitSurfaceNormal = pUnitSurfaceNormal;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			issuingEntityID.unmarshal(dis);
			collidingEntityID.unmarshal(dis);
			collisionEventID.unmarshal(dis);
			pad = dis.readShort();
			contactVelocity.unmarshal(dis);
			mass = dis.readFloat();
			location.unmarshal(dis);
			collisionResultXX = dis.readFloat();
			collisionResultXY = dis.readFloat();
			collisionResultXZ = dis.readFloat();
			collisionResultYY = dis.readFloat();
			collisionResultYZ = dis.readFloat();
			collisionResultZZ = dis.readFloat();
			unitSurfaceNormal.unmarshal(dis);
			coefficientOfRestitution = dis.readFloat();
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

		issuingEntityID.unmarshal(buff);
		collidingEntityID.unmarshal(buff);
		collisionEventID.unmarshal(buff);
		pad = buff.getShort();
		contactVelocity.unmarshal(buff);
		mass = buff.getFloat();
		location.unmarshal(buff);
		collisionResultXX = buff.getFloat();
		collisionResultXY = buff.getFloat();
		collisionResultXZ = buff.getFloat();
		collisionResultYY = buff.getFloat();
		collisionResultYZ = buff.getFloat();
		collisionResultZZ = buff.getFloat();
		unitSurfaceNormal.unmarshal(buff);
		coefficientOfRestitution = buff.getFloat();
	} // end of unmarshal method
} // end of class
