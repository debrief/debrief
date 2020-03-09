package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Actual transmission of intercome voice data. Section 7.7.5. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IntercomSignalPdu extends RadioCommunicationsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** entity ID */
	protected EntityID entityID = new EntityID();

	/** ID of communications device */
	protected int communicationsDeviceID;

	/** encoding scheme */
	protected int encodingScheme;

	/** tactical data link type */
	protected int tdlType;

	/** sample rate */
	protected long sampleRate;

	/** data length */
	protected int dataLength;

	/** samples */
	protected int samples;

	/** data bytes */
	protected List<OneByteChunk> data = new ArrayList<OneByteChunk>();

	/** Constructor */
	public IntercomSignalPdu() {
		setPduType((short) 31);
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

		if (!(obj instanceof IntercomSignalPdu))
			return false;

		final IntercomSignalPdu rhs = (IntercomSignalPdu) obj;

		if (!(entityID.equals(rhs.entityID)))
			ivarsEqual = false;
		if (!(communicationsDeviceID == rhs.communicationsDeviceID))
			ivarsEqual = false;
		if (!(encodingScheme == rhs.encodingScheme))
			ivarsEqual = false;
		if (!(tdlType == rhs.tdlType))
			ivarsEqual = false;
		if (!(sampleRate == rhs.sampleRate))
			ivarsEqual = false;
		if (!(dataLength == rhs.dataLength))
			ivarsEqual = false;
		if (!(samples == rhs.samples))
			ivarsEqual = false;

		for (int idx = 0; idx < data.size(); idx++) {
			if (!(data.get(idx).equals(rhs.data.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public int getCommunicationsDeviceID() {
		return communicationsDeviceID;
	}

	public List<OneByteChunk> getData() {
		return data;
	}

	public int getDataLength() {
		return data.size();
	}

	public int getEncodingScheme() {
		return encodingScheme;
	}

	public EntityID getEntityID() {
		return entityID;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + entityID.getMarshalledSize(); // entityID
		marshalSize = marshalSize + 2; // communicationsDeviceID
		marshalSize = marshalSize + 2; // encodingScheme
		marshalSize = marshalSize + 2; // tdlType
		marshalSize = marshalSize + 4; // sampleRate
		marshalSize = marshalSize + 2; // dataLength
		marshalSize = marshalSize + 2; // samples
		for (int idx = 0; idx < data.size(); idx++) {
			final OneByteChunk listElement = data.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getSampleRate() {
		return sampleRate;
	}

	public int getSamples() {
		return samples;
	}

	public int getTdlType() {
		return tdlType;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			entityID.marshal(dos);
			dos.writeShort((short) communicationsDeviceID);
			dos.writeShort((short) encodingScheme);
			dos.writeShort((short) tdlType);
			dos.writeInt((int) sampleRate);
			dos.writeShort((short) data.size());
			dos.writeShort((short) samples);

			for (int idx = 0; idx < data.size(); idx++) {
				final OneByteChunk aOneByteChunk = data.get(idx);
				aOneByteChunk.marshal(dos);
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
		entityID.marshal(buff);
		buff.putShort((short) communicationsDeviceID);
		buff.putShort((short) encodingScheme);
		buff.putShort((short) tdlType);
		buff.putInt((int) sampleRate);
		buff.putShort((short) data.size());
		buff.putShort((short) samples);

		for (int idx = 0; idx < data.size(); idx++) {
			final OneByteChunk aOneByteChunk = data.get(idx);
			aOneByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setCommunicationsDeviceID(final int pCommunicationsDeviceID) {
		communicationsDeviceID = pCommunicationsDeviceID;
	}

	public void setData(final List<OneByteChunk> pData) {
		data = pData;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getdataLength
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setDataLength(final int pDataLength) {
		dataLength = pDataLength;
	}

	public void setEncodingScheme(final int pEncodingScheme) {
		encodingScheme = pEncodingScheme;
	}

	public void setEntityID(final EntityID pEntityID) {
		entityID = pEntityID;
	}

	public void setSampleRate(final long pSampleRate) {
		sampleRate = pSampleRate;
	}

	public void setSamples(final int pSamples) {
		samples = pSamples;
	}

	public void setTdlType(final int pTdlType) {
		tdlType = pTdlType;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			entityID.unmarshal(dis);
			communicationsDeviceID = dis.readUnsignedShort();
			encodingScheme = dis.readUnsignedShort();
			tdlType = dis.readUnsignedShort();
			sampleRate = dis.readInt();
			dataLength = dis.readUnsignedShort();
			samples = dis.readUnsignedShort();
			for (int idx = 0; idx < dataLength; idx++) {
				final OneByteChunk anX = new OneByteChunk();
				anX.unmarshal(dis);
				data.add(anX);
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

		entityID.unmarshal(buff);
		communicationsDeviceID = buff.getShort() & 0xFFFF;
		encodingScheme = buff.getShort() & 0xFFFF;
		tdlType = buff.getShort() & 0xFFFF;
		sampleRate = buff.getInt();
		dataLength = buff.getShort() & 0xFFFF;
		samples = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < dataLength; idx++) {
			final OneByteChunk anX = new OneByteChunk();
			anX.unmarshal(buff);
			data.add(anX);
		}

	} // end of unmarshal method
} // end of class
