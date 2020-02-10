package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.10.3 Information about individual mines within a minefield. This
 * is very, very wrong. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MinefieldDataPdu extends MinefieldFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Minefield ID */
	protected EntityID minefieldID = new EntityID();

	/** ID of entity making request */
	protected EntityID requestingEntityID = new EntityID();

	/** Minefield sequence number */
	protected int minefieldSequenceNumbeer;

	/** request ID */
	protected short requestID;

	/** pdu sequence number */
	protected short pduSequenceNumber;

	/** number of pdus in response */
	protected short numberOfPdus;

	/** how many mines are in this PDU */
	protected short numberOfMinesInThisPdu;

	/** how many sensor type are in this PDU */
	protected short numberOfSensorTypes;

	/** padding */
	protected short pad2 = (short) 0;

	/** 32 boolean fields */
	protected long dataFilter;

	/** Mine type */
	protected EntityType mineType = new EntityType();

	/** Sensor types, each 16 bits long */
	protected List<TwoByteChunk> sensorTypes = new ArrayList<TwoByteChunk>();
	/**
	 * Padding to get things 32-bit aligned. ^^^this is wrong--dyanmically sized
	 * padding needed
	 */
	protected short pad3;

	/** Mine locations */
	protected List<Vector3Float> mineLocation = new ArrayList<Vector3Float>();

	/** Constructor */
	public MinefieldDataPdu() {
		setPduType((short) 39);
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

		if (!(obj instanceof MinefieldDataPdu))
			return false;

		final MinefieldDataPdu rhs = (MinefieldDataPdu) obj;

		if (!(minefieldID.equals(rhs.minefieldID)))
			ivarsEqual = false;
		if (!(requestingEntityID.equals(rhs.requestingEntityID)))
			ivarsEqual = false;
		if (!(minefieldSequenceNumbeer == rhs.minefieldSequenceNumbeer))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(pduSequenceNumber == rhs.pduSequenceNumber))
			ivarsEqual = false;
		if (!(numberOfPdus == rhs.numberOfPdus))
			ivarsEqual = false;
		if (!(numberOfMinesInThisPdu == rhs.numberOfMinesInThisPdu))
			ivarsEqual = false;
		if (!(numberOfSensorTypes == rhs.numberOfSensorTypes))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(dataFilter == rhs.dataFilter))
			ivarsEqual = false;
		if (!(mineType.equals(rhs.mineType)))
			ivarsEqual = false;

		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			if (!(sensorTypes.get(idx).equals(rhs.sensorTypes.get(idx))))
				ivarsEqual = false;
		}

		if (!(pad3 == rhs.pad3))
			ivarsEqual = false;

		for (int idx = 0; idx < mineLocation.size(); idx++) {
			if (!(mineLocation.get(idx).equals(rhs.mineLocation.get(idx))))
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
		marshalSize = marshalSize + 2; // minefieldSequenceNumbeer
		marshalSize = marshalSize + 1; // requestID
		marshalSize = marshalSize + 1; // pduSequenceNumber
		marshalSize = marshalSize + 1; // numberOfPdus
		marshalSize = marshalSize + 1; // numberOfMinesInThisPdu
		marshalSize = marshalSize + 1; // numberOfSensorTypes
		marshalSize = marshalSize + 1; // pad2
		marshalSize = marshalSize + 4; // dataFilter
		marshalSize = marshalSize + mineType.getMarshalledSize(); // mineType
		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			final TwoByteChunk listElement = sensorTypes.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		marshalSize = marshalSize + 1; // pad3
		for (int idx = 0; idx < mineLocation.size(); idx++) {
			final Vector3Float listElement = mineLocation.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public EntityID getMinefieldID() {
		return minefieldID;
	}

	public int getMinefieldSequenceNumbeer() {
		return minefieldSequenceNumbeer;
	}

	public List<Vector3Float> getMineLocation() {
		return mineLocation;
	}

	public EntityType getMineType() {
		return mineType;
	}

	public short getNumberOfMinesInThisPdu() {
		return (short) mineLocation.size();
	}

	public short getNumberOfPdus() {
		return numberOfPdus;
	}

	public short getNumberOfSensorTypes() {
		return (short) sensorTypes.size();
	}

	public short getPad2() {
		return pad2;
	}

	public short getPad3() {
		return pad3;
	}

	public short getPduSequenceNumber() {
		return pduSequenceNumber;
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
			dos.writeShort((short) minefieldSequenceNumbeer);
			dos.writeByte((byte) requestID);
			dos.writeByte((byte) pduSequenceNumber);
			dos.writeByte((byte) numberOfPdus);
			dos.writeByte((byte) mineLocation.size());
			dos.writeByte((byte) sensorTypes.size());
			dos.writeByte((byte) pad2);
			dos.writeInt((int) dataFilter);
			mineType.marshal(dos);

			for (int idx = 0; idx < sensorTypes.size(); idx++) {
				final TwoByteChunk aTwoByteChunk = sensorTypes.get(idx);
				aTwoByteChunk.marshal(dos);
			} // end of list marshalling

			dos.writeByte((byte) pad3);

			for (int idx = 0; idx < mineLocation.size(); idx++) {
				final Vector3Float aVector3Float = mineLocation.get(idx);
				aVector3Float.marshal(dos);
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
		buff.putShort((short) minefieldSequenceNumbeer);
		buff.put((byte) requestID);
		buff.put((byte) pduSequenceNumber);
		buff.put((byte) numberOfPdus);
		buff.put((byte) mineLocation.size());
		buff.put((byte) sensorTypes.size());
		buff.put((byte) pad2);
		buff.putInt((int) dataFilter);
		mineType.marshal(buff);

		for (int idx = 0; idx < sensorTypes.size(); idx++) {
			final TwoByteChunk aTwoByteChunk = sensorTypes.get(idx);
			aTwoByteChunk.marshal(buff);
		} // end of list marshalling

		buff.put((byte) pad3);

		for (int idx = 0; idx < mineLocation.size(); idx++) {
			final Vector3Float aVector3Float = mineLocation.get(idx);
			aVector3Float.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setDataFilter(final long pDataFilter) {
		dataFilter = pDataFilter;
	}

	public void setMinefieldID(final EntityID pMinefieldID) {
		minefieldID = pMinefieldID;
	}

	public void setMinefieldSequenceNumbeer(final int pMinefieldSequenceNumbeer) {
		minefieldSequenceNumbeer = pMinefieldSequenceNumbeer;
	}

	public void setMineLocation(final List<Vector3Float> pMineLocation) {
		mineLocation = pMineLocation;
	}

	public void setMineType(final EntityType pMineType) {
		mineType = pMineType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfMinesInThisPdu method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfMinesInThisPdu(final short pNumberOfMinesInThisPdu) {
		numberOfMinesInThisPdu = pNumberOfMinesInThisPdu;
	}

	public void setNumberOfPdus(final short pNumberOfPdus) {
		numberOfPdus = pNumberOfPdus;
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

	public void setPad3(final short pPad3) {
		pad3 = pPad3;
	}

	public void setPduSequenceNumber(final short pPduSequenceNumber) {
		pduSequenceNumber = pPduSequenceNumber;
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
			minefieldSequenceNumbeer = dis.readUnsignedShort();
			requestID = (short) dis.readUnsignedByte();
			pduSequenceNumber = (short) dis.readUnsignedByte();
			numberOfPdus = (short) dis.readUnsignedByte();
			numberOfMinesInThisPdu = (short) dis.readUnsignedByte();
			numberOfSensorTypes = (short) dis.readUnsignedByte();
			pad2 = (short) dis.readUnsignedByte();
			dataFilter = dis.readInt();
			mineType.unmarshal(dis);
			for (int idx = 0; idx < numberOfSensorTypes; idx++) {
				final TwoByteChunk anX = new TwoByteChunk();
				anX.unmarshal(dis);
				sensorTypes.add(anX);
			}

			pad3 = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < numberOfMinesInThisPdu; idx++) {
				final Vector3Float anX = new Vector3Float();
				anX.unmarshal(dis);
				mineLocation.add(anX);
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
		minefieldSequenceNumbeer = buff.getShort() & 0xFFFF;
		requestID = (short) (buff.get() & 0xFF);
		pduSequenceNumber = (short) (buff.get() & 0xFF);
		numberOfPdus = (short) (buff.get() & 0xFF);
		numberOfMinesInThisPdu = (short) (buff.get() & 0xFF);
		numberOfSensorTypes = (short) (buff.get() & 0xFF);
		pad2 = (short) (buff.get() & 0xFF);
		dataFilter = buff.getInt();
		mineType.unmarshal(buff);
		for (int idx = 0; idx < numberOfSensorTypes; idx++) {
			final TwoByteChunk anX = new TwoByteChunk();
			anX.unmarshal(buff);
			sensorTypes.add(anX);
		}

		pad3 = (short) (buff.get() & 0xFF);
		for (int idx = 0; idx < numberOfMinesInThisPdu; idx++) {
			final Vector3Float anX = new Vector3Float();
			anX.unmarshal(buff);
			mineLocation.add(anX);
		}

	} // end of unmarshal method
} // end of class
