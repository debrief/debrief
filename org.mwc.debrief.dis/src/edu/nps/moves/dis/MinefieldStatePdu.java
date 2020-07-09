package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.10.1 Abstract superclass for PDUs relating to minefields.
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MinefieldStatePdu extends MinefieldFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Minefield ID */
	protected EntityID minefieldID = new EntityID();

	/** Minefield sequence */
	protected int minefieldSequence;

	/** force ID */
	protected short forceID;

	/** Number of permieter points */
	protected short numberOfPerimeterPoints;

	/** type of minefield */
	protected EntityType minefieldType = new EntityType();

	/** how many mine types */
	protected int numberOfMineTypes;

	/** location of minefield in world coords */
	protected Vector3Double minefieldLocation = new Vector3Double();

	/** orientation of minefield */
	protected Orientation minefieldOrientation = new Orientation();

	/** appearance bitflags */
	protected int appearance;

	/** protocolMode */
	protected int protocolMode;

	/** perimeter points for the minefield */
	protected List<Point> perimeterPoints = new ArrayList<Point>();
	/** Type of mines */
	protected List<EntityType> mineType = new ArrayList<EntityType>();

	/** Constructor */
	public MinefieldStatePdu() {
		setPduType((short) 37);
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

		if (!(obj instanceof MinefieldStatePdu))
			return false;

		final MinefieldStatePdu rhs = (MinefieldStatePdu) obj;

		if (!(minefieldID.equals(rhs.minefieldID)))
			ivarsEqual = false;
		if (!(minefieldSequence == rhs.minefieldSequence))
			ivarsEqual = false;
		if (!(forceID == rhs.forceID))
			ivarsEqual = false;
		if (!(numberOfPerimeterPoints == rhs.numberOfPerimeterPoints))
			ivarsEqual = false;
		if (!(minefieldType.equals(rhs.minefieldType)))
			ivarsEqual = false;
		if (!(numberOfMineTypes == rhs.numberOfMineTypes))
			ivarsEqual = false;
		if (!(minefieldLocation.equals(rhs.minefieldLocation)))
			ivarsEqual = false;
		if (!(minefieldOrientation.equals(rhs.minefieldOrientation)))
			ivarsEqual = false;
		if (!(appearance == rhs.appearance))
			ivarsEqual = false;
		if (!(protocolMode == rhs.protocolMode))
			ivarsEqual = false;

		for (int idx = 0; idx < perimeterPoints.size(); idx++) {
			if (!(perimeterPoints.get(idx).equals(rhs.perimeterPoints.get(idx))))
				ivarsEqual = false;
		}

		for (int idx = 0; idx < mineType.size(); idx++) {
			if (!(mineType.get(idx).equals(rhs.mineType.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public int getAppearance() {
		return appearance;
	}

	public short getForceID() {
		return forceID;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + minefieldID.getMarshalledSize(); // minefieldID
		marshalSize = marshalSize + 2; // minefieldSequence
		marshalSize = marshalSize + 1; // forceID
		marshalSize = marshalSize + 1; // numberOfPerimeterPoints
		marshalSize = marshalSize + minefieldType.getMarshalledSize(); // minefieldType
		marshalSize = marshalSize + 2; // numberOfMineTypes
		marshalSize = marshalSize + minefieldLocation.getMarshalledSize(); // minefieldLocation
		marshalSize = marshalSize + minefieldOrientation.getMarshalledSize(); // minefieldOrientation
		marshalSize = marshalSize + 2; // appearance
		marshalSize = marshalSize + 2; // protocolMode
		for (int idx = 0; idx < perimeterPoints.size(); idx++) {
			final Point listElement = perimeterPoints.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		for (int idx = 0; idx < mineType.size(); idx++) {
			final EntityType listElement = mineType.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityID getMinefieldID() {
		return minefieldID;
	}

	public Vector3Double getMinefieldLocation() {
		return minefieldLocation;
	}

	public Orientation getMinefieldOrientation() {
		return minefieldOrientation;
	}

	public int getMinefieldSequence() {
		return minefieldSequence;
	}

	public EntityType getMinefieldType() {
		return minefieldType;
	}

	public List<EntityType> getMineType() {
		return mineType;
	}

	public int getNumberOfMineTypes() {
		return mineType.size();
	}

	public short getNumberOfPerimeterPoints() {
		return (short) perimeterPoints.size();
	}

	public List<Point> getPerimeterPoints() {
		return perimeterPoints;
	}

	public int getProtocolMode() {
		return protocolMode;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			minefieldID.marshal(dos);
			dos.writeShort((short) minefieldSequence);
			dos.writeByte((byte) forceID);
			dos.writeByte((byte) perimeterPoints.size());
			minefieldType.marshal(dos);
			dos.writeShort((short) mineType.size());
			minefieldLocation.marshal(dos);
			minefieldOrientation.marshal(dos);
			dos.writeShort((short) appearance);
			dos.writeShort((short) protocolMode);

			for (int idx = 0; idx < perimeterPoints.size(); idx++) {
				final Point aPoint = perimeterPoints.get(idx);
				aPoint.marshal(dos);
			} // end of list marshalling

			for (int idx = 0; idx < mineType.size(); idx++) {
				final EntityType aEntityType = mineType.get(idx);
				aEntityType.marshal(dos);
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
		minefieldID.marshal(buff);
		buff.putShort((short) minefieldSequence);
		buff.put((byte) forceID);
		buff.put((byte) perimeterPoints.size());
		minefieldType.marshal(buff);
		buff.putShort((short) mineType.size());
		minefieldLocation.marshal(buff);
		minefieldOrientation.marshal(buff);
		buff.putShort((short) appearance);
		buff.putShort((short) protocolMode);

		for (int idx = 0; idx < perimeterPoints.size(); idx++) {
			final Point aPoint = perimeterPoints.get(idx);
			aPoint.marshal(buff);
		} // end of list marshalling

		for (int idx = 0; idx < mineType.size(); idx++) {
			final EntityType aEntityType = mineType.get(idx);
			aEntityType.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setAppearance(final int pAppearance) {
		appearance = pAppearance;
	}

	public void setForceID(final short pForceID) {
		forceID = pForceID;
	}

	public void setMinefieldID(final EntityID pMinefieldID) {
		minefieldID = pMinefieldID;
	}

	public void setMinefieldLocation(final Vector3Double pMinefieldLocation) {
		minefieldLocation = pMinefieldLocation;
	}

	public void setMinefieldOrientation(final Orientation pMinefieldOrientation) {
		minefieldOrientation = pMinefieldOrientation;
	}

	public void setMinefieldSequence(final int pMinefieldSequence) {
		minefieldSequence = pMinefieldSequence;
	}

	public void setMinefieldType(final EntityType pMinefieldType) {
		minefieldType = pMinefieldType;
	}

	public void setMineType(final List<EntityType> pMineType) {
		mineType = pMineType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfMineTypes method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfMineTypes(final int pNumberOfMineTypes) {
		numberOfMineTypes = pNumberOfMineTypes;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfPerimeterPoints method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfPerimeterPoints(final short pNumberOfPerimeterPoints) {
		numberOfPerimeterPoints = pNumberOfPerimeterPoints;
	}

	public void setPerimeterPoints(final List<Point> pPerimeterPoints) {
		perimeterPoints = pPerimeterPoints;
	}

	public void setProtocolMode(final int pProtocolMode) {
		protocolMode = pProtocolMode;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			minefieldID.unmarshal(dis);
			minefieldSequence = dis.readUnsignedShort();
			forceID = (short) dis.readUnsignedByte();
			numberOfPerimeterPoints = (short) dis.readUnsignedByte();
			minefieldType.unmarshal(dis);
			numberOfMineTypes = dis.readUnsignedShort();
			minefieldLocation.unmarshal(dis);
			minefieldOrientation.unmarshal(dis);
			appearance = dis.readUnsignedShort();
			protocolMode = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfPerimeterPoints; idx++) {
				final Point anX = new Point();
				anX.unmarshal(dis);
				perimeterPoints.add(anX);
			}

			for (int idx = 0; idx < numberOfMineTypes; idx++) {
				final EntityType anX = new EntityType();
				anX.unmarshal(dis);
				mineType.add(anX);
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

		minefieldID.unmarshal(buff);
		minefieldSequence = buff.getShort() & 0xFFFF;
		forceID = (short) (buff.get() & 0xFF);
		numberOfPerimeterPoints = (short) (buff.get() & 0xFF);
		minefieldType.unmarshal(buff);
		numberOfMineTypes = buff.getShort() & 0xFFFF;
		minefieldLocation.unmarshal(buff);
		minefieldOrientation.unmarshal(buff);
		appearance = buff.getShort() & 0xFFFF;
		protocolMode = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfPerimeterPoints; idx++) {
			final Point anX = new Point();
			anX.unmarshal(buff);
			perimeterPoints.add(anX);
		}

		for (int idx = 0; idx < numberOfMineTypes; idx++) {
			final EntityType anX = new EntityType();
			anX.unmarshal(buff);
			mineType.add(anX);
		}

	} // end of unmarshal method
} // end of class
