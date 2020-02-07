package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Sectioin 5.3.4.1. Information about someone firing something. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class FirePdu extends WarfareFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the munition that is being shot */
	protected EntityID munitionID = new EntityID();

	/** ID of event */
	protected EventID eventID = new EventID();

	protected int fireMissionIndex;

	/** location of the firing event */
	protected Vector3Double locationInWorldCoordinates = new Vector3Double();

	/** Describes munitions used in the firing event */
	protected BurstDescriptor burstDescriptor = new BurstDescriptor();

	/** Velocity of the ammunition */
	protected Vector3Float velocity = new Vector3Float();

	/** range to the target. Note the word range is a SQL reserved word. */
	protected float rangeToTarget;

	/** Constructor */
	public FirePdu() {
		setPduType((short) 2);
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

		if (!(obj instanceof FirePdu))
			return false;

		final FirePdu rhs = (FirePdu) obj;

		if (!(munitionID.equals(rhs.munitionID)))
			ivarsEqual = false;
		if (!(eventID.equals(rhs.eventID)))
			ivarsEqual = false;
		if (!(fireMissionIndex == rhs.fireMissionIndex))
			ivarsEqual = false;
		if (!(locationInWorldCoordinates.equals(rhs.locationInWorldCoordinates)))
			ivarsEqual = false;
		if (!(burstDescriptor.equals(rhs.burstDescriptor)))
			ivarsEqual = false;
		if (!(velocity.equals(rhs.velocity)))
			ivarsEqual = false;
		if (!(rangeToTarget == rhs.rangeToTarget))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public BurstDescriptor getBurstDescriptor() {
		return burstDescriptor;
	}

	public EventID getEventID() {
		return eventID;
	}

	public int getFireMissionIndex() {
		return fireMissionIndex;
	}

	public Vector3Double getLocationInWorldCoordinates() {
		return locationInWorldCoordinates;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + munitionID.getMarshalledSize(); // munitionID
		marshalSize = marshalSize + eventID.getMarshalledSize(); // eventID
		marshalSize = marshalSize + 4; // fireMissionIndex
		marshalSize = marshalSize + locationInWorldCoordinates.getMarshalledSize(); // locationInWorldCoordinates
		marshalSize = marshalSize + burstDescriptor.getMarshalledSize(); // burstDescriptor
		marshalSize = marshalSize + velocity.getMarshalledSize(); // velocity
		marshalSize = marshalSize + 4; // rangeToTarget

		return marshalSize;
	}

	public EntityID getMunitionID() {
		return munitionID;
	}

	public float getRangeToTarget() {
		return rangeToTarget;
	}

	public Vector3Float getVelocity() {
		return velocity;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			munitionID.marshal(dos);
			eventID.marshal(dos);
			dos.writeInt(fireMissionIndex);
			locationInWorldCoordinates.marshal(dos);
			burstDescriptor.marshal(dos);
			velocity.marshal(dos);
			dos.writeFloat(rangeToTarget);
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
		munitionID.marshal(buff);
		eventID.marshal(buff);
		buff.putInt(fireMissionIndex);
		locationInWorldCoordinates.marshal(buff);
		burstDescriptor.marshal(buff);
		velocity.marshal(buff);
		buff.putFloat(rangeToTarget);
	} // end of marshal method

	public void setBurstDescriptor(final BurstDescriptor pBurstDescriptor) {
		burstDescriptor = pBurstDescriptor;
	}

	public void setEventID(final EventID pEventID) {
		eventID = pEventID;
	}

	public void setFireMissionIndex(final int pFireMissionIndex) {
		fireMissionIndex = pFireMissionIndex;
	}

	public void setLocationInWorldCoordinates(final Vector3Double pLocationInWorldCoordinates) {
		locationInWorldCoordinates = pLocationInWorldCoordinates;
	}

	public void setMunitionID(final EntityID pMunitionID) {
		munitionID = pMunitionID;
	}

	public void setRangeToTarget(final float pRangeToTarget) {
		rangeToTarget = pRangeToTarget;
	}

	public void setVelocity(final Vector3Float pVelocity) {
		velocity = pVelocity;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			munitionID.unmarshal(dis);
			eventID.unmarshal(dis);
			fireMissionIndex = dis.readInt();
			locationInWorldCoordinates.unmarshal(dis);
			burstDescriptor.unmarshal(dis);
			velocity.unmarshal(dis);
			rangeToTarget = dis.readFloat();
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

		munitionID.unmarshal(buff);
		eventID.unmarshal(buff);
		fireMissionIndex = buff.getInt();
		locationInWorldCoordinates.unmarshal(buff);
		burstDescriptor.unmarshal(buff);
		velocity.unmarshal(buff);
		rangeToTarget = buff.getFloat();
	} // end of unmarshal method
} // end of class
