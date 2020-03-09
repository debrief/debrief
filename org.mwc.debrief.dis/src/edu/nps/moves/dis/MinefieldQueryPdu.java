package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.10.2 Query a minefield for information about individual mines.
 * Requires manual clean up to get the padding right. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MinefieldQueryPdu extends MinefieldFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Minefield ID */
	protected EntityID minefieldID = new EntityID();

	/** EID of entity making the request */
	protected EntityID requestingEntityID = new EntityID();

	/** request ID */
	protected short requestID;

	/** Number of perimeter points for the minefield */
	protected short numberOfPerimeterPoints;

	/** Padding */
	protected short pad2;

	/** Number of sensor types */
	protected short numberOfSensorTypes;

	/** data filter, 32 boolean fields */
	protected long dataFilter;

	/** Entity type of mine being requested */
	protected EntityType requestedMineType = new EntityType();

	/** perimeter points of request */
	protected List<Point> requestedPerimeterPoints = new ArrayList<Point>();
	/** Sensor types, each 16 bits long */
	protected List<TwoByteChunk> sensorTypes = new ArrayList<TwoByteChunk>();

	/** Constructor */
	public MinefieldQueryPdu() {
		setPduType((short) 38);
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

		if (!(obj instanceof MinefieldQueryPdu))
			return false;

		final MinefieldQueryPdu rhs = (MinefieldQueryPdu) obj;

		if (!(minefieldID.equals(rhs.minefieldID)))
			ivarsEqual = false;
		if (!(requestingEntityID.equals(rhs.requestingEntityID)))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(numberOfPerimeterPoints == rhs.numberOfPerimeterPoints))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(numberOfSensorTypes == rhs.numberOfSensorTypes))
			ivarsEqual = false;
		if (!(dataFilter == rhs.dataFilter))
			ivarsEqual = false;
		if (!(requestedMineType.equals(rhs.requestedMineType)))
			ivarsEqual = false;

		for (int idx = 0; idx < requestedPerimeterPoints.size(); idx++) {
			if (!(requestedPerimeterPoints.get(idx).equals(rhs.requestedPerimeterPoints.get(idx))))
				ivarsEqual = false;
		}

		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			if (!(sensorTypes.get(idx).equals(rhs.sensorTypes.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public long getDataFilter() {
		return dataFilter;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + minefieldID.getMarshalledSize(); // minefieldID
		marshalSize = marshalSize + requestingEntityID.getMarshalledSize(); // requestingEntityID
		marshalSize = marshalSize + 1; // requestID
		marshalSize = marshalSize + 1; // numberOfPerimeterPoints
		marshalSize = marshalSize + 1; // pad2
		marshalSize = marshalSize + 1; // numberOfSensorTypes
		marshalSize = marshalSize + 4; // dataFilter
		marshalSize = marshalSize + requestedMineType.getMarshalledSize(); // requestedMineType
		for (int idx = 0; idx < requestedPerimeterPoints.size(); idx++) {
			final Point listElement = requestedPerimeterPoints.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			final TwoByteChunk listElement = sensorTypes.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityID getMinefieldID() {
		return minefieldID;
	}

	public short getNumberOfPerimeterPoints() {
		return (short) requestedPerimeterPoints.size();
	}

	public short getNumberOfSensorTypes() {
		return (short) sensorTypes.size();
	}

	public short getPad2() {
		return pad2;
	}

	public EntityType getRequestedMineType() {
		return requestedMineType;
	}

	public List<Point> getRequestedPerimeterPoints() {
		return requestedPerimeterPoints;
	}

	public short getRequestID() {
		return requestID;
	}

	public EntityID getRequestingEntityID() {
		return requestingEntityID;
	}

	public List<TwoByteChunk> getSensorTypes() {
		return sensorTypes;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			minefieldID.marshal(dos);
			requestingEntityID.marshal(dos);
			dos.writeByte((byte) requestID);
			dos.writeByte((byte) requestedPerimeterPoints.size());
			dos.writeByte((byte) pad2);
			dos.writeByte((byte) sensorTypes.size());
			dos.writeInt((int) dataFilter);
			requestedMineType.marshal(dos);

			for (int idx = 0; idx < requestedPerimeterPoints.size(); idx++) {
				final Point aPoint = requestedPerimeterPoints.get(idx);
				aPoint.marshal(dos);
			} // end of list marshalling

			for (int idx = 0; idx < sensorTypes.size(); idx++) {
				final TwoByteChunk aTwoByteChunk = sensorTypes.get(idx);
				aTwoByteChunk.marshal(dos);
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
		requestingEntityID.marshal(buff);
		buff.put((byte) requestID);
		buff.put((byte) requestedPerimeterPoints.size());
		buff.put((byte) pad2);
		buff.put((byte) sensorTypes.size());
		buff.putInt((int) dataFilter);
		requestedMineType.marshal(buff);

		for (int idx = 0; idx < requestedPerimeterPoints.size(); idx++) {
			final Point aPoint = requestedPerimeterPoints.get(idx);
			aPoint.marshal(buff);
		} // end of list marshalling

		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			final TwoByteChunk aTwoByteChunk = sensorTypes.get(idx);
			aTwoByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setDataFilter(final long pDataFilter) {
		dataFilter = pDataFilter;
	}

	public void setMinefieldID(final EntityID pMinefieldID) {
		minefieldID = pMinefieldID;
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

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfSensorTypes method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfSensorTypes(final short pNumberOfSensorTypes) {
		numberOfSensorTypes = pNumberOfSensorTypes;
	}

	public void setPad2(final short pPad2) {
		pad2 = pPad2;
	}

	public void setRequestedMineType(final EntityType pRequestedMineType) {
		requestedMineType = pRequestedMineType;
	}

	public void setRequestedPerimeterPoints(final List<Point> pRequestedPerimeterPoints) {
		requestedPerimeterPoints = pRequestedPerimeterPoints;
	}

	public void setRequestID(final short pRequestID) {
		requestID = pRequestID;
	}

	public void setRequestingEntityID(final EntityID pRequestingEntityID) {
		requestingEntityID = pRequestingEntityID;
	}

	public void setSensorTypes(final List<TwoByteChunk> pSensorTypes) {
		sensorTypes = pSensorTypes;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			minefieldID.unmarshal(dis);
			requestingEntityID.unmarshal(dis);
			requestID = (short) dis.readUnsignedByte();
			numberOfPerimeterPoints = (short) dis.readUnsignedByte();
			pad2 = (short) dis.readUnsignedByte();
			numberOfSensorTypes = (short) dis.readUnsignedByte();
			dataFilter = dis.readInt();
			requestedMineType.unmarshal(dis);
			for (int idx = 0; idx < numberOfPerimeterPoints; idx++) {
				final Point anX = new Point();
				anX.unmarshal(dis);
				requestedPerimeterPoints.add(anX);
			}

			for (int idx = 0; idx < numberOfSensorTypes; idx++) {
				final TwoByteChunk anX = new TwoByteChunk();
				anX.unmarshal(dis);
				sensorTypes.add(anX);
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
		requestingEntityID.unmarshal(buff);
		requestID = (short) (buff.get() & 0xFF);
		numberOfPerimeterPoints = (short) (buff.get() & 0xFF);
		pad2 = (short) (buff.get() & 0xFF);
		numberOfSensorTypes = (short) (buff.get() & 0xFF);
		dataFilter = buff.getInt();
		requestedMineType.unmarshal(buff);
		for (int idx = 0; idx < numberOfPerimeterPoints; idx++) {
			final Point anX = new Point();
			anX.unmarshal(buff);
			requestedPerimeterPoints.add(anX);
		}

		for (int idx = 0; idx < numberOfSensorTypes; idx++) {
			final TwoByteChunk anX = new TwoByteChunk();
			anX.unmarshal(buff);
			sensorTypes.add(anX);
		}

	} // end of unmarshal method
} // end of class
