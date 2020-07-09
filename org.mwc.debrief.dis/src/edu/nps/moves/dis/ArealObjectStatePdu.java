package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.11.5: Information about the addition/modification of an oobject
 * that is geometrically achored to the terrain with a set of three or more
 * points that come to a closure. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ArealObjectStatePdu extends SyntheticEnvironmentFamilyPdu implements Serializable {
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

	/** modifications enumeration */
	protected short modifications;

	/** Object type */
	protected EntityType objectType = new EntityType();

	/** Object appearance */
	protected SixByteChunk objectAppearance = new SixByteChunk();

	/** Number of points */
	protected int numberOfPoints;

	/** requesterID */
	protected SimulationAddress requesterID = new SimulationAddress();

	/** receiver ID */
	protected SimulationAddress receivingID = new SimulationAddress();

	/** location of object */
	protected List<Vector3Double> objectLocation = new ArrayList<Vector3Double>();

	/** Constructor */
	public ArealObjectStatePdu() {
		setPduType((short) 45);
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

		if (!(obj instanceof ArealObjectStatePdu))
			return false;

		final ArealObjectStatePdu rhs = (ArealObjectStatePdu) obj;

		if (!(objectID.equals(rhs.objectID)))
			ivarsEqual = false;
		if (!(referencedObjectID.equals(rhs.referencedObjectID)))
			ivarsEqual = false;
		if (!(updateNumber == rhs.updateNumber))
			ivarsEqual = false;
		if (!(forceID == rhs.forceID))
			ivarsEqual = false;
		if (!(modifications == rhs.modifications))
			ivarsEqual = false;
		if (!(objectType.equals(rhs.objectType)))
			ivarsEqual = false;
		if (!(objectAppearance.equals(rhs.objectAppearance)))
			ivarsEqual = false;
		if (!(numberOfPoints == rhs.numberOfPoints))
			ivarsEqual = false;
		if (!(requesterID.equals(rhs.requesterID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;

		for (int idx = 0; idx < objectLocation.size(); idx++) {
			if (!(objectLocation.get(idx).equals(rhs.objectLocation.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public short getForceID() {
		return forceID;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + objectID.getMarshalledSize(); // objectID
		marshalSize = marshalSize + referencedObjectID.getMarshalledSize(); // referencedObjectID
		marshalSize = marshalSize + 2; // updateNumber
		marshalSize = marshalSize + 1; // forceID
		marshalSize = marshalSize + 1; // modifications
		marshalSize = marshalSize + objectType.getMarshalledSize(); // objectType
		marshalSize = marshalSize + objectAppearance.getMarshalledSize(); // objectAppearance
		marshalSize = marshalSize + 2; // numberOfPoints
		marshalSize = marshalSize + requesterID.getMarshalledSize(); // requesterID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID
		for (int idx = 0; idx < objectLocation.size(); idx++) {
			final Vector3Double listElement = objectLocation.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getModifications() {
		return modifications;
	}

	public int getNumberOfPoints() {
		return objectLocation.size();
	}

	public SixByteChunk getObjectAppearance() {
		return objectAppearance;
	}

	public EntityID getObjectID() {
		return objectID;
	}

	public List<Vector3Double> getObjectLocation() {
		return objectLocation;
	}

	public EntityType getObjectType() {
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
			dos.writeByte((byte) modifications);
			objectType.marshal(dos);
			objectAppearance.marshal(dos);
			dos.writeShort((short) objectLocation.size());
			requesterID.marshal(dos);
			receivingID.marshal(dos);

			for (int idx = 0; idx < objectLocation.size(); idx++) {
				final Vector3Double aVector3Double = objectLocation.get(idx);
				aVector3Double.marshal(dos);
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
		buff.put((byte) modifications);
		objectType.marshal(buff);
		objectAppearance.marshal(buff);
		buff.putShort((short) objectLocation.size());
		requesterID.marshal(buff);
		receivingID.marshal(buff);

		for (int idx = 0; idx < objectLocation.size(); idx++) {
			final Vector3Double aVector3Double = objectLocation.get(idx);
			aVector3Double.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setForceID(final short pForceID) {
		forceID = pForceID;
	}

	public void setModifications(final short pModifications) {
		modifications = pModifications;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfPoints
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfPoints(final int pNumberOfPoints) {
		numberOfPoints = pNumberOfPoints;
	}

	public void setObjectAppearance(final SixByteChunk pObjectAppearance) {
		objectAppearance = pObjectAppearance;
	}

	public void setObjectID(final EntityID pObjectID) {
		objectID = pObjectID;
	}

	public void setObjectLocation(final List<Vector3Double> pObjectLocation) {
		objectLocation = pObjectLocation;
	}

	public void setObjectType(final EntityType pObjectType) {
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
			modifications = (short) dis.readUnsignedByte();
			objectType.unmarshal(dis);
			objectAppearance.unmarshal(dis);
			numberOfPoints = dis.readUnsignedShort();
			requesterID.unmarshal(dis);
			receivingID.unmarshal(dis);
			for (int idx = 0; idx < numberOfPoints; idx++) {
				final Vector3Double anX = new Vector3Double();
				anX.unmarshal(dis);
				objectLocation.add(anX);
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
		modifications = (short) (buff.get() & 0xFF);
		objectType.unmarshal(buff);
		objectAppearance.unmarshal(buff);
		numberOfPoints = buff.getShort() & 0xFFFF;
		requesterID.unmarshal(buff);
		receivingID.unmarshal(buff);
		for (int idx = 0; idx < numberOfPoints; idx++) {
			final Vector3Double anX = new Vector3Double();
			anX.unmarshal(buff);
			objectLocation.add(anX);
		}

	} // end of unmarshal method
} // end of class
