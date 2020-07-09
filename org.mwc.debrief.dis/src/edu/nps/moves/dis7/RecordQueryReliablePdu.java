package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.12.13: A request for one or more records of data from an entity.
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RecordQueryReliablePdu extends SimulationManagementWithReliabilityFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** request ID */
	protected long requestID;

	/** level of reliability service used for this transaction */
	protected short requiredReliabilityService;

	/** padding. The spec is unclear and contradictory here. */
	protected int pad1;

	/** padding */
	protected short pad2;

	/** event type */
	protected int eventType;

	/** time */
	protected long time;

	/** numberOfRecords */
	protected long numberOfRecords;

	/** record IDs */
	protected List<FourByteChunk> recordIDs = new ArrayList<FourByteChunk>();

	/** Constructor */
	public RecordQueryReliablePdu() {
		setPduType((short) 63);
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

		if (!(obj instanceof RecordQueryReliablePdu))
			return false;

		final RecordQueryReliablePdu rhs = (RecordQueryReliablePdu) obj;

		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(requiredReliabilityService == rhs.requiredReliabilityService))
			ivarsEqual = false;
		if (!(pad1 == rhs.pad1))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
			ivarsEqual = false;
		if (!(eventType == rhs.eventType))
			ivarsEqual = false;
		if (!(time == rhs.time))
			ivarsEqual = false;
		if (!(numberOfRecords == rhs.numberOfRecords))
			ivarsEqual = false;

		for (int idx = 0; idx < recordIDs.size(); idx++) {
			if (!(recordIDs.get(idx).equals(rhs.recordIDs.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public int getEventType() {
		return eventType;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 4; // requestID
		marshalSize = marshalSize + 1; // requiredReliabilityService
		marshalSize = marshalSize + 2; // pad1
		marshalSize = marshalSize + 1; // pad2
		marshalSize = marshalSize + 2; // eventType
		marshalSize = marshalSize + 4; // time
		marshalSize = marshalSize + 4; // numberOfRecords
		for (int idx = 0; idx < recordIDs.size(); idx++) {
			final FourByteChunk listElement = recordIDs.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfRecords() {
		return recordIDs.size();
	}

	public int getPad1() {
		return pad1;
	}

	public short getPad2() {
		return pad2;
	}

	public List<FourByteChunk> getRecordIDs() {
		return recordIDs;
	}

	public long getRequestID() {
		return requestID;
	}

	public short getRequiredReliabilityService() {
		return requiredReliabilityService;
	}

	public long getTime() {
		return time;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeInt((int) requestID);
			dos.writeByte((byte) requiredReliabilityService);
			dos.writeShort((short) pad1);
			dos.writeByte((byte) pad2);
			dos.writeShort((short) eventType);
			dos.writeInt((int) time);
			dos.writeInt(recordIDs.size());

			for (int idx = 0; idx < recordIDs.size(); idx++) {
				final FourByteChunk aFourByteChunk = recordIDs.get(idx);
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
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		buff.putInt((int) requestID);
		buff.put((byte) requiredReliabilityService);
		buff.putShort((short) pad1);
		buff.put((byte) pad2);
		buff.putShort((short) eventType);
		buff.putInt((int) time);
		buff.putInt(recordIDs.size());

		for (int idx = 0; idx < recordIDs.size(); idx++) {
			final FourByteChunk aFourByteChunk = recordIDs.get(idx);
			aFourByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setEventType(final int pEventType) {
		eventType = pEventType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfRecords
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfRecords(final long pNumberOfRecords) {
		numberOfRecords = pNumberOfRecords;
	}

	public void setPad1(final int pPad1) {
		pad1 = pPad1;
	}

	public void setPad2(final short pPad2) {
		pad2 = pPad2;
	}

	public void setRecordIDs(final List<FourByteChunk> pRecordIDs) {
		recordIDs = pRecordIDs;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setRequiredReliabilityService(final short pRequiredReliabilityService) {
		requiredReliabilityService = pRequiredReliabilityService;
	}

	public void setTime(final long pTime) {
		time = pTime;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			requestID = dis.readInt();
			requiredReliabilityService = (short) dis.readUnsignedByte();
			pad1 = dis.readUnsignedShort();
			pad2 = (short) dis.readUnsignedByte();
			eventType = dis.readUnsignedShort();
			time = dis.readInt();
			numberOfRecords = dis.readInt();
			for (int idx = 0; idx < numberOfRecords; idx++) {
				final FourByteChunk anX = new FourByteChunk();
				anX.unmarshal(dis);
				recordIDs.add(anX);
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

		requestID = buff.getInt();
		requiredReliabilityService = (short) (buff.get() & 0xFF);
		pad1 = buff.getShort() & 0xFFFF;
		pad2 = (short) (buff.get() & 0xFF);
		eventType = buff.getShort() & 0xFFFF;
		time = buff.getInt();
		numberOfRecords = buff.getInt();
		for (int idx = 0; idx < numberOfRecords; idx++) {
			final FourByteChunk anX = new FourByteChunk();
			anX.unmarshal(buff);
			recordIDs.add(anX);
		}

	} // end of unmarshal method
} // end of class
