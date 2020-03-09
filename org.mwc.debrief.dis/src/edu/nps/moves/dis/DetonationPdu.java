package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.4.2. Information about stuff exploding. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DetonationPdu extends WarfareFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of muntion that was fired */
	protected EntityID munitionID = new EntityID();

	/** ID firing event */
	protected EventID eventID = new EventID();

	/** ID firing event */
	protected Vector3Float velocity = new Vector3Float();

	/** where the detonation is, in world coordinates */
	protected Vector3Double locationInWorldCoordinates = new Vector3Double();

	/** Describes munition used */
	protected BurstDescriptor burstDescriptor = new BurstDescriptor();

	/**
	 * location of the detonation or impact in the target entity's coordinate
	 * system. This information should be used for damage assessment.
	 */
	protected Vector3Float locationInEntityCoordinates = new Vector3Float();

	/** result of the explosion */
	protected short detonationResult;

	/** How many articulation parameters we have */
	protected short numberOfArticulationParameters;

	/** padding */
	protected short pad = (short) 0;

	protected List<ArticulationParameter> articulationParameters = new ArrayList<ArticulationParameter>();

	/** Constructor */
	public DetonationPdu() {
		setPduType((short) 3);
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

		if (!(obj instanceof DetonationPdu))
			return false;

		final DetonationPdu rhs = (DetonationPdu) obj;

		if (!(munitionID.equals(rhs.munitionID)))
			ivarsEqual = false;
		if (!(eventID.equals(rhs.eventID)))
			ivarsEqual = false;
		if (!(velocity.equals(rhs.velocity)))
			ivarsEqual = false;
		if (!(locationInWorldCoordinates.equals(rhs.locationInWorldCoordinates)))
			ivarsEqual = false;
		if (!(burstDescriptor.equals(rhs.burstDescriptor)))
			ivarsEqual = false;
		if (!(locationInEntityCoordinates.equals(rhs.locationInEntityCoordinates)))
			ivarsEqual = false;
		if (!(detonationResult == rhs.detonationResult))
			ivarsEqual = false;
		if (!(numberOfArticulationParameters == rhs.numberOfArticulationParameters))
			ivarsEqual = false;
		if (!(pad == rhs.pad))
			ivarsEqual = false;

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			if (!(articulationParameters.get(idx).equals(rhs.articulationParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<ArticulationParameter> getArticulationParameters() {
		return articulationParameters;
	}

	public BurstDescriptor getBurstDescriptor() {
		return burstDescriptor;
	}

	public short getDetonationResult() {
		return detonationResult;
	}

	public EventID getEventID() {
		return eventID;
	}

	public Vector3Float getLocationInEntityCoordinates() {
		return locationInEntityCoordinates;
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
		marshalSize = marshalSize + velocity.getMarshalledSize(); // velocity
		marshalSize = marshalSize + locationInWorldCoordinates.getMarshalledSize(); // locationInWorldCoordinates
		marshalSize = marshalSize + burstDescriptor.getMarshalledSize(); // burstDescriptor
		marshalSize = marshalSize + locationInEntityCoordinates.getMarshalledSize(); // locationInEntityCoordinates
		marshalSize = marshalSize + 1; // detonationResult
		marshalSize = marshalSize + 1; // numberOfArticulationParameters
		marshalSize = marshalSize + 2; // pad
		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter listElement = articulationParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityID getMunitionID() {
		return munitionID;
	}

	public short getNumberOfArticulationParameters() {
		return (short) articulationParameters.size();
	}

	public short getPad() {
		return pad;
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
			velocity.marshal(dos);
			locationInWorldCoordinates.marshal(dos);
			burstDescriptor.marshal(dos);
			locationInEntityCoordinates.marshal(dos);
			dos.writeByte((byte) detonationResult);
			dos.writeByte((byte) articulationParameters.size());
			dos.writeShort(pad);

			for (int idx = 0; idx < articulationParameters.size(); idx++) {
				final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
				aArticulationParameter.marshal(dos);
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
		munitionID.marshal(buff);
		eventID.marshal(buff);
		velocity.marshal(buff);
		locationInWorldCoordinates.marshal(buff);
		burstDescriptor.marshal(buff);
		locationInEntityCoordinates.marshal(buff);
		buff.put((byte) detonationResult);
		buff.put((byte) articulationParameters.size());
		buff.putShort(pad);

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
			aArticulationParameter.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setArticulationParameters(final List<ArticulationParameter> pArticulationParameters) {
		articulationParameters = pArticulationParameters;
	}

	public void setBurstDescriptor(final BurstDescriptor pBurstDescriptor) {
		burstDescriptor = pBurstDescriptor;
	}

	public void setDetonationResult(final short pDetonationResult) {
		detonationResult = pDetonationResult;
	}

	public void setEventID(final EventID pEventID) {
		eventID = pEventID;
	}

	public void setLocationInEntityCoordinates(final Vector3Float pLocationInEntityCoordinates) {
		locationInEntityCoordinates = pLocationInEntityCoordinates;
	}

	public void setLocationInWorldCoordinates(final Vector3Double pLocationInWorldCoordinates) {
		locationInWorldCoordinates = pLocationInWorldCoordinates;
	}

	public void setMunitionID(final EntityID pMunitionID) {
		munitionID = pMunitionID;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfArticulationParameters method will also be based on the actual
	 * list length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfArticulationParameters(final short pNumberOfArticulationParameters) {
		numberOfArticulationParameters = pNumberOfArticulationParameters;
	}

	public void setPad(final short pPad) {
		pad = pPad;
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
			velocity.unmarshal(dis);
			locationInWorldCoordinates.unmarshal(dis);
			burstDescriptor.unmarshal(dis);
			locationInEntityCoordinates.unmarshal(dis);
			detonationResult = (short) dis.readUnsignedByte();
			numberOfArticulationParameters = (short) dis.readUnsignedByte();
			pad = dis.readShort();
			for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
				final ArticulationParameter anX = new ArticulationParameter();
				anX.unmarshal(dis);
				articulationParameters.add(anX);
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

		munitionID.unmarshal(buff);
		eventID.unmarshal(buff);
		velocity.unmarshal(buff);
		locationInWorldCoordinates.unmarshal(buff);
		burstDescriptor.unmarshal(buff);
		locationInEntityCoordinates.unmarshal(buff);
		detonationResult = (short) (buff.get() & 0xFF);
		numberOfArticulationParameters = (short) (buff.get() & 0xFF);
		pad = buff.getShort();
		for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
			final ArticulationParameter anX = new ArticulationParameter();
			anX.unmarshal(buff);
			articulationParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
