package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.11.4: Information abut the addition or modification of a
 * synthecic enviroment object that is anchored to the terrain with a single
 * point and has size or orientation. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class LinearObjectStatePdu extends SyntheticEnvironmentFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Object in synthetic environment */
	protected EntityID objectID = new EntityID();

	/** Object with which this point object is associated */
	protected EntityID referencedObjectID = new EntityID();

	/** unique update number of each state transition of an object */
	protected int updateNumber;

	/** force ID */
	protected short forceID;

	/** number of linear segment parameters */
	protected short numberOfSegments;

	/** requesterID */
	protected SimulationAddress requesterID = new SimulationAddress();

	/** receiver ID */
	protected SimulationAddress receivingID = new SimulationAddress();

	/** Object type */
	protected ObjectType objectType = new ObjectType();

	/** Linear segment parameters */
	protected List<LinearSegmentParameter> linearSegmentParameters = new ArrayList<LinearSegmentParameter>();

	/** Constructor */
	public LinearObjectStatePdu() {
		setPduType((short) 44);
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

		if (!(obj instanceof LinearObjectStatePdu))
			return false;

		final LinearObjectStatePdu rhs = (LinearObjectStatePdu) obj;

		if (!(objectID.equals(rhs.objectID)))
			ivarsEqual = false;
		if (!(referencedObjectID.equals(rhs.referencedObjectID)))
			ivarsEqual = false;
		if (!(updateNumber == rhs.updateNumber))
			ivarsEqual = false;
		if (!(forceID == rhs.forceID))
			ivarsEqual = false;
		if (!(numberOfSegments == rhs.numberOfSegments))
			ivarsEqual = false;
		if (!(requesterID.equals(rhs.requesterID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;
		if (!(objectType.equals(rhs.objectType)))
			ivarsEqual = false;

		for (int idx = 0; idx < linearSegmentParameters.size(); idx++) {
			if (!(linearSegmentParameters.get(idx).equals(rhs.linearSegmentParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public short getForceID() {
		return forceID;
	}

	public List<LinearSegmentParameter> getLinearSegmentParameters() {
		return linearSegmentParameters;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + objectID.getMarshalledSize(); // objectID
		marshalSize = marshalSize + referencedObjectID.getMarshalledSize(); // referencedObjectID
		marshalSize = marshalSize + 2; // updateNumber
		marshalSize = marshalSize + 1; // forceID
		marshalSize = marshalSize + 1; // numberOfSegments
		marshalSize = marshalSize + requesterID.getMarshalledSize(); // requesterID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID
		marshalSize = marshalSize + objectType.getMarshalledSize(); // objectType
		for (int idx = 0; idx < linearSegmentParameters.size(); idx++) {
			final LinearSegmentParameter listElement = linearSegmentParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfSegments() {
		return (short) linearSegmentParameters.size();
	}

	public EntityID getObjectID() {
		return objectID;
	}

	public ObjectType getObjectType() {
		return objectType;
	}

	public SimulationAddress getReceivingID() {
		return receivingID;
	}

	public EntityID getReferencedObjectID() {
		return referencedObjectID;
	}

	public SimulationAddress getRequesterID() {
		return requesterID;
	}

	public int getUpdateNumber() {
		return updateNumber;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			objectID.marshal(dos);
			referencedObjectID.marshal(dos);
			dos.writeShort((short) updateNumber);
			dos.writeByte((byte) forceID);
			dos.writeByte((byte) linearSegmentParameters.size());
			requesterID.marshal(dos);
			receivingID.marshal(dos);
			objectType.marshal(dos);

			for (int idx = 0; idx < linearSegmentParameters.size(); idx++) {
				final LinearSegmentParameter aLinearSegmentParameter = linearSegmentParameters.get(idx);
				aLinearSegmentParameter.marshal(dos);
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
		objectID.marshal(buff);
		referencedObjectID.marshal(buff);
		buff.putShort((short) updateNumber);
		buff.put((byte) forceID);
		buff.put((byte) linearSegmentParameters.size());
		requesterID.marshal(buff);
		receivingID.marshal(buff);
		objectType.marshal(buff);

		for (int idx = 0; idx < linearSegmentParameters.size(); idx++) {
			final LinearSegmentParameter aLinearSegmentParameter = linearSegmentParameters.get(idx);
			aLinearSegmentParameter.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setForceID(final short pForceID) {
		forceID = pForceID;
	}

	public void setLinearSegmentParameters(final List<LinearSegmentParameter> pLinearSegmentParameters) {
		linearSegmentParameters = pLinearSegmentParameters;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfSegments
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfSegments(final short pNumberOfSegments) {
		numberOfSegments = pNumberOfSegments;
	}

	public void setObjectID(final EntityID pObjectID) {
		objectID = pObjectID;
	}

	public void setObjectType(final ObjectType pObjectType) {
		objectType = pObjectType;
	}

	public void setReceivingID(final SimulationAddress pReceivingID) {
		receivingID = pReceivingID;
	}

	public void setReferencedObjectID(final EntityID pReferencedObjectID) {
		referencedObjectID = pReferencedObjectID;
	}

	public void setRequesterID(final SimulationAddress pRequesterID) {
		requesterID = pRequesterID;
	}

	public void setUpdateNumber(final int pUpdateNumber) {
		updateNumber = pUpdateNumber;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			objectID.unmarshal(dis);
			referencedObjectID.unmarshal(dis);
			updateNumber = dis.readUnsignedShort();
			forceID = (short) dis.readUnsignedByte();
			numberOfSegments = (short) dis.readUnsignedByte();
			requesterID.unmarshal(dis);
			receivingID.unmarshal(dis);
			objectType.unmarshal(dis);
			for (int idx = 0; idx < numberOfSegments; idx++) {
				final LinearSegmentParameter anX = new LinearSegmentParameter();
				anX.unmarshal(dis);
				linearSegmentParameters.add(anX);
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

		objectID.unmarshal(buff);
		referencedObjectID.unmarshal(buff);
		updateNumber = buff.getShort() & 0xFFFF;
		forceID = (short) (buff.get() & 0xFF);
		numberOfSegments = (short) (buff.get() & 0xFF);
		requesterID.unmarshal(buff);
		receivingID.unmarshal(buff);
		objectType.unmarshal(buff);
		for (int idx = 0; idx < numberOfSegments; idx++) {
			final LinearSegmentParameter anX = new LinearSegmentParameter();
			anX.unmarshal(buff);
			linearSegmentParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
