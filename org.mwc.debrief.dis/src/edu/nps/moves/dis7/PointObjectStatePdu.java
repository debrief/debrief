package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * : Inormation abut the addition or modification of a synthecic enviroment
 * object that is anchored to the terrain with a single point. Section 7.10.4
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class PointObjectStatePdu extends SyntheticEnvironmentFamilyPdu implements Serializable {
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

	/** modifications */
	protected short modifications;

	/** Object type */
	protected ObjectType objectType = new ObjectType();

	/** Object location */
	protected Vector3Double objectLocation = new Vector3Double();

	/** Object orientation */
	protected EulerAngles objectOrientation = new EulerAngles();

	/** Object apperance */
	protected double objectAppearance;

	/** requesterID */
	protected SimulationAddress requesterID = new SimulationAddress();

	/** receiver ID */
	protected SimulationAddress receivingID = new SimulationAddress();

	/** padding */
	protected long pad2;

	/** Constructor */
	public PointObjectStatePdu() {
		setPduType((short) 43);
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

		if (!(obj instanceof PointObjectStatePdu))
			return false;

		final PointObjectStatePdu rhs = (PointObjectStatePdu) obj;

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
		if (!(objectLocation.equals(rhs.objectLocation)))
			ivarsEqual = false;
		if (!(objectOrientation.equals(rhs.objectOrientation)))
			ivarsEqual = false;
		if (!(objectAppearance == rhs.objectAppearance))
			ivarsEqual = false;
		if (!(requesterID.equals(rhs.requesterID)))
			ivarsEqual = false;
		if (!(receivingID.equals(rhs.receivingID)))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;

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
		marshalSize = marshalSize + objectLocation.getMarshalledSize(); // objectLocation
		marshalSize = marshalSize + objectOrientation.getMarshalledSize(); // objectOrientation
		marshalSize = marshalSize + 8; // objectAppearance
		marshalSize = marshalSize + requesterID.getMarshalledSize(); // requesterID
		marshalSize = marshalSize + receivingID.getMarshalledSize(); // receivingID
		marshalSize = marshalSize + 4; // pad2

		return marshalSize;
	}

	public short getModifications() {
		return modifications;
	}

	public double getObjectAppearance() {
		return objectAppearance;
	}

	public EntityID getObjectID() {
		return objectID;
	}

	public Vector3Double getObjectLocation() {
		return objectLocation;
	}

	public EulerAngles getObjectOrientation() {
		return objectOrientation;
	}

	public ObjectType getObjectType() {
		return objectType;
	}

	public long getPad2() {
		return pad2;
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
			objectLocation.marshal(dos);
			objectOrientation.marshal(dos);
			dos.writeDouble(objectAppearance);
			requesterID.marshal(dos);
			receivingID.marshal(dos);
			dos.writeInt((int) pad2);
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
		objectLocation.marshal(buff);
		objectOrientation.marshal(buff);
		buff.putDouble(objectAppearance);
		requesterID.marshal(buff);
		receivingID.marshal(buff);
		buff.putInt((int) pad2);
	} // end of marshal method

	public void setForceID(final short pForceID) {
		forceID = pForceID;
	}

	public void setModifications(final short pModifications) {
		modifications = pModifications;
	}

	public void setObjectAppearance(final double pObjectAppearance) {
		objectAppearance = pObjectAppearance;
	}

	public void setObjectID(final EntityID pObjectID) {
		objectID = pObjectID;
	}

	public void setObjectLocation(final Vector3Double pObjectLocation) {
		objectLocation = pObjectLocation;
	}

	public void setObjectOrientation(final EulerAngles pObjectOrientation) {
		objectOrientation = pObjectOrientation;
	}

	public void setObjectType(final ObjectType pObjectType) {
		objectType = pObjectType;
	}

	public void setPad2(final long pPad2) {
		pad2 = pPad2;
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
			objectLocation.unmarshal(dis);
			objectOrientation.unmarshal(dis);
			objectAppearance = dis.readDouble();
			requesterID.unmarshal(dis);
			receivingID.unmarshal(dis);
			pad2 = dis.readInt();
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
		objectLocation.unmarshal(buff);
		objectOrientation.unmarshal(buff);
		objectAppearance = buff.getDouble();
		requesterID.unmarshal(buff);
		receivingID.unmarshal(buff);
		pad2 = buff.getInt();
	} // end of unmarshal method
} // end of class
