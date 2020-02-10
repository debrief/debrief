package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.9.3 Information initiating the dyanic allocation and control of
 * simulation entities between two simulation applications. Requires manual
 * cleanup. The padding between record sets is variable. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class TransferControlRequestPdu extends EntityManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of entity originating request */
	protected EntityID orginatingEntityID = new EntityID();

	/** ID of entity receiving request */
	protected EntityID recevingEntityID = new EntityID();

	/** ID ofrequest */
	protected long requestID;

	/** required level of reliabliity service. */
	protected short requiredReliabilityService;

	/** type of transfer desired */
	protected short tranferType;

	/** The entity for which control is being requested to transfer */
	protected EntityID transferEntityID = new EntityID();

	/** number of record sets to transfer */
	protected short numberOfRecordSets;

	/** ^^^This is wrong--the RecordSet class needs more work */
	protected List<RecordSet> recordSets = new ArrayList<RecordSet>();

	/** Constructor */
	public TransferControlRequestPdu() {
		setPduType((short) 35);
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

		if (!(obj instanceof TransferControlRequestPdu))
			return false;

		final TransferControlRequestPdu rhs = (TransferControlRequestPdu) obj;

		if (!(orginatingEntityID.equals(rhs.orginatingEntityID)))
			ivarsEqual = false;
		if (!(recevingEntityID.equals(rhs.recevingEntityID)))
			ivarsEqual = false;
		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(requiredReliabilityService == rhs.requiredReliabilityService))
			ivarsEqual = false;
		if (!(tranferType == rhs.tranferType))
			ivarsEqual = false;
		if (!(transferEntityID.equals(rhs.transferEntityID)))
			ivarsEqual = false;
		if (!(numberOfRecordSets == rhs.numberOfRecordSets))
			ivarsEqual = false;

		for (int idx = 0; idx < recordSets.size(); idx++) {
			if (!(recordSets.get(idx).equals(rhs.recordSets.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + orginatingEntityID.getMarshalledSize(); // orginatingEntityID
		marshalSize = marshalSize + recevingEntityID.getMarshalledSize(); // recevingEntityID
		marshalSize = marshalSize + 4; // requestID
		marshalSize = marshalSize + 1; // requiredReliabilityService
		marshalSize = marshalSize + 1; // tranferType
		marshalSize = marshalSize + transferEntityID.getMarshalledSize(); // transferEntityID
		marshalSize = marshalSize + 1; // numberOfRecordSets
		for (int idx = 0; idx < recordSets.size(); idx++) {
			final RecordSet listElement = recordSets.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfRecordSets() {
		return (short) recordSets.size();
	}

	public EntityID getOrginatingEntityID() {
		return orginatingEntityID;
	}

	public EntityID getRecevingEntityID() {
		return recevingEntityID;
	}

	public List<RecordSet> getRecordSets() {
		return recordSets;
	}

	public long getRequestID() {
		return requestID;
	}

	public short getRequiredReliabilityService() {
		return requiredReliabilityService;
	}

	public short getTranferType() {
		return tranferType;
	}

	public EntityID getTransferEntityID() {
		return transferEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			orginatingEntityID.marshal(dos);
			recevingEntityID.marshal(dos);
			dos.writeInt((int) requestID);
			dos.writeByte((byte) requiredReliabilityService);
			dos.writeByte((byte) tranferType);
			transferEntityID.marshal(dos);
			dos.writeByte((byte) recordSets.size());

			for (int idx = 0; idx < recordSets.size(); idx++) {
				final RecordSet aRecordSet = recordSets.get(idx);
				aRecordSet.marshal(dos);
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
		orginatingEntityID.marshal(buff);
		recevingEntityID.marshal(buff);
		buff.putInt((int) requestID);
		buff.put((byte) requiredReliabilityService);
		buff.put((byte) tranferType);
		transferEntityID.marshal(buff);
		buff.put((byte) recordSets.size());

		for (int idx = 0; idx < recordSets.size(); idx++) {
			final RecordSet aRecordSet = recordSets.get(idx);
			aRecordSet.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfRecordSets method will also be based on the actual list length
	 * rather than this value. The method is simply here for java bean completeness.
	 */
	public void setNumberOfRecordSets(final short pNumberOfRecordSets) {
		numberOfRecordSets = pNumberOfRecordSets;
	}

	public void setOrginatingEntityID(final EntityID pOrginatingEntityID) {
		orginatingEntityID = pOrginatingEntityID;
	}

	public void setRecevingEntityID(final EntityID pRecevingEntityID) {
		recevingEntityID = pRecevingEntityID;
	}

	public void setRecordSets(final List<RecordSet> pRecordSets) {
		recordSets = pRecordSets;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setRequiredReliabilityService(final short pRequiredReliabilityService) {
		requiredReliabilityService = pRequiredReliabilityService;
	}

	public void setTranferType(final short pTranferType) {
		tranferType = pTranferType;
	}

	public void setTransferEntityID(final EntityID pTransferEntityID) {
		transferEntityID = pTransferEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			orginatingEntityID.unmarshal(dis);
			recevingEntityID.unmarshal(dis);
			requestID = dis.readInt();
			requiredReliabilityService = (short) dis.readUnsignedByte();
			tranferType = (short) dis.readUnsignedByte();
			transferEntityID.unmarshal(dis);
			numberOfRecordSets = (short) dis.readUnsignedByte();
			for (int idx = 0; idx < numberOfRecordSets; idx++) {
				final RecordSet anX = new RecordSet();
				anX.unmarshal(dis);
				recordSets.add(anX);
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

		orginatingEntityID.unmarshal(buff);
		recevingEntityID.unmarshal(buff);
		requestID = buff.getInt();
		requiredReliabilityService = (short) (buff.get() & 0xFF);
		tranferType = (short) (buff.get() & 0xFF);
		transferEntityID.unmarshal(buff);
		numberOfRecordSets = (short) (buff.get() & 0xFF);
		for (int idx = 0; idx < numberOfRecordSets; idx++) {
			final RecordSet anX = new RecordSet();
			anX.unmarshal(buff);
			recordSets.add(anX);
		}

	} // end of unmarshal method
} // end of class
