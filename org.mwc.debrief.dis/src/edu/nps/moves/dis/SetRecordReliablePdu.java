package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.12.14: Initializing or changing internal parameter info. Needs
 * manual intervention to fix padding in recrod set PDUs. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SetRecordReliablePdu extends SimulationManagementWithReliabilityFamilyPdu implements Serializable {
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

	/** Number of record sets in list */
	protected long numberOfRecordSets;

	/** record sets */
	protected List<RecordSet> recordSets = new ArrayList<RecordSet>();

	/** Constructor */
	public SetRecordReliablePdu() {
		setPduType((short) 64);
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

		if (!(obj instanceof SetRecordReliablePdu))
			return false;

		final SetRecordReliablePdu rhs = (SetRecordReliablePdu) obj;

		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(requiredReliabilityService == rhs.requiredReliabilityService))
			ivarsEqual = false;
		if (!(pad1 == rhs.pad1))
			ivarsEqual = false;
		if (!(pad2 == rhs.pad2))
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
		marshalSize = marshalSize + 4; // requestID
		marshalSize = marshalSize + 1; // requiredReliabilityService
		marshalSize = marshalSize + 2; // pad1
		marshalSize = marshalSize + 1; // pad2
		marshalSize = marshalSize + 4; // numberOfRecordSets
		for (int idx = 0; idx < recordSets.size(); idx++) {
			final RecordSet listElement = recordSets.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfRecordSets() {
		return recordSets.size();
	}

	public int getPad1() {
		return pad1;
	}

	public short getPad2() {
		return pad2;
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

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeInt((int) requestID);
			dos.writeByte((byte) requiredReliabilityService);
			dos.writeShort((short) pad1);
			dos.writeByte((byte) pad2);
			dos.writeInt(recordSets.size());

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
		buff.putInt((int) requestID);
		buff.put((byte) requiredReliabilityService);
		buff.putShort((short) pad1);
		buff.put((byte) pad2);
		buff.putInt(recordSets.size());

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
	public void setNumberOfRecordSets(final long pNumberOfRecordSets) {
		numberOfRecordSets = pNumberOfRecordSets;
	}

	public void setPad1(final int pPad1) {
		pad1 = pPad1;
	}

	public void setPad2(final short pPad2) {
		pad2 = pPad2;
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

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			requestID = dis.readInt();
			requiredReliabilityService = (short) dis.readUnsignedByte();
			pad1 = dis.readUnsignedShort();
			pad2 = (short) dis.readUnsignedByte();
			numberOfRecordSets = dis.readInt();
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

		requestID = buff.getInt();
		requiredReliabilityService = (short) (buff.get() & 0xFF);
		pad1 = buff.getShort() & 0xFFFF;
		pad2 = (short) (buff.get() & 0xFF);
		numberOfRecordSets = buff.getInt();
		for (int idx = 0; idx < numberOfRecordSets; idx++) {
			final RecordSet anX = new RecordSet();
			anX.unmarshal(buff);
			recordSets.add(anX);
		}

	} // end of unmarshal method
} // end of class
