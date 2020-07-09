package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * information abou an enitity not producing espdus. Section 6.2.79
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SilentEntitySystem extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** number of the type specified by the entity type field */
	protected int numberOfEntities;

	/** number of entity appearance records that follow */
	protected int numberOfAppearanceRecords;

	/** Entity type */
	protected EntityType entityType = new EntityType();

	/** Variable length list of appearance records */
	protected List<FourByteChunk> appearanceRecordList = new ArrayList<FourByteChunk>();

	/** Constructor */
	public SilentEntitySystem() {
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

	/**
	 * Compare all fields that contribute to the state, ignoring transient and
	 * static fields, for <code>this</code> and the supplied object
	 *
	 * @param obj the object to compare to
	 * @return true if the objects are equal, false otherwise.
	 */
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof SilentEntitySystem))
			return false;

		final SilentEntitySystem rhs = (SilentEntitySystem) obj;

		if (!(numberOfEntities == rhs.numberOfEntities))
			ivarsEqual = false;
		if (!(numberOfAppearanceRecords == rhs.numberOfAppearanceRecords))
			ivarsEqual = false;
		if (!(entityType.equals(rhs.entityType)))
			ivarsEqual = false;

		for (int idx = 0; idx < appearanceRecordList.size(); idx++) {
			if (!(appearanceRecordList.get(idx).equals(rhs.appearanceRecordList.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public List<FourByteChunk> getAppearanceRecordList() {
		return appearanceRecordList;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // numberOfEntities
		marshalSize = marshalSize + 2; // numberOfAppearanceRecords
		marshalSize = marshalSize + entityType.getMarshalledSize(); // entityType
		for (int idx = 0; idx < appearanceRecordList.size(); idx++) {
			final FourByteChunk listElement = appearanceRecordList.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public int getNumberOfAppearanceRecords() {
		return appearanceRecordList.size();
	}

	public int getNumberOfEntities() {
		return numberOfEntities;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) numberOfEntities);
			dos.writeShort((short) appearanceRecordList.size());
			entityType.marshal(dos);

			for (int idx = 0; idx < appearanceRecordList.size(); idx++) {
				final FourByteChunk aFourByteChunk = appearanceRecordList.get(idx);
				aFourByteChunk.marshal(dos);
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
	public void marshal(final java.nio.ByteBuffer buff) {
		buff.putShort((short) numberOfEntities);
		buff.putShort((short) appearanceRecordList.size());
		entityType.marshal(buff);

		for (int idx = 0; idx < appearanceRecordList.size(); idx++) {
			final FourByteChunk aFourByteChunk = appearanceRecordList.get(idx);
			aFourByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setAppearanceRecordList(final List<FourByteChunk> pAppearanceRecordList) {
		appearanceRecordList = pAppearanceRecordList;
	}

	public void setEntityType(final EntityType pEntityType) {
		entityType = pEntityType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfAppearanceRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfAppearanceRecords(final int pNumberOfAppearanceRecords) {
		numberOfAppearanceRecords = pNumberOfAppearanceRecords;
	}

	public void setNumberOfEntities(final int pNumberOfEntities) {
		numberOfEntities = pNumberOfEntities;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			numberOfEntities = dis.readUnsignedShort();
			numberOfAppearanceRecords = dis.readUnsignedShort();
			entityType.unmarshal(dis);
			for (int idx = 0; idx < numberOfAppearanceRecords; idx++) {
				final FourByteChunk anX = new FourByteChunk();
				anX.unmarshal(dis);
				appearanceRecordList.add(anX);
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
	public void unmarshal(final java.nio.ByteBuffer buff) {
		numberOfEntities = buff.getShort() & 0xFFFF;
		numberOfAppearanceRecords = buff.getShort() & 0xFFFF;
		entityType.unmarshal(buff);
		for (int idx = 0; idx < numberOfAppearanceRecords; idx++) {
			final FourByteChunk anX = new FourByteChunk();
			anX.unmarshal(buff);
			appearanceRecordList.add(anX);
		}

	} // end of unmarshal method
} // end of class
