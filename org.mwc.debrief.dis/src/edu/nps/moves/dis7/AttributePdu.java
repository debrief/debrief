package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Information about individual attributes for a particular entity, other
 * object, or event may be communicated using an Attribute PDU. The Attribute
 * PDU shall not be used to exchange data available in any other PDU except
 * where explicitly mentioned in the PDU issuance instructions within this
 * standard. See 5.3.6 for the information requirements and issuance and receipt
 * rules for this PDU. Section 7.2.6. INCOMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AttributePdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This field shall identify the simulation issuing the Attribute PDU. It shall
	 * be represented by a Simulation Address record (see 6.2.79).
	 */
	protected SimulationAddress originatingSimulationAddress = new SimulationAddress();

	/** Padding */
	protected int padding1;

	/** Padding */
	protected short padding2;

	/**
	 * This field shall represent the type of the PDU that is being extended or
	 * updated, if applicable. It shall be represented by an 8-bit enumeration.
	 */
	protected short attributeRecordPduType;

	/**
	 * This field shall indicate the Protocol Version associated with the Attribute
	 * Record PDU Type. It shall be represented by an 8-bit enumeration.
	 */
	protected short attributeRecordProtocolVersion;

	/**
	 * This field shall contain the Attribute record type of the Attribute records
	 * in the PDU if they all have the same Attribute record type. It shall be
	 * represented by a 32-bit enumeration.
	 */
	protected long masterAttributeRecordType;

	/**
	 * This field shall identify the action code applicable to this Attribute PDU.
	 * The Action Code shall apply to all Attribute records contained in the PDU. It
	 * shall be represented by an 8-bit enumeration.
	 */
	protected short actionCode;

	/** Padding */
	protected byte padding3;

	/**
	 * This field shall specify the number of Attribute Record Sets that make up the
	 * remainder of the PDU. It shall be represented by a 16-bit unsigned integer.
	 */
	protected int numberAttributeRecordSet;

	/** Constructor */
	public AttributePdu() {
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

		if (!(obj instanceof AttributePdu))
			return false;

		final AttributePdu rhs = (AttributePdu) obj;

		if (!(originatingSimulationAddress.equals(rhs.originatingSimulationAddress)))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(attributeRecordPduType == rhs.attributeRecordPduType))
			ivarsEqual = false;
		if (!(attributeRecordProtocolVersion == rhs.attributeRecordProtocolVersion))
			ivarsEqual = false;
		if (!(masterAttributeRecordType == rhs.masterAttributeRecordType))
			ivarsEqual = false;
		if (!(actionCode == rhs.actionCode))
			ivarsEqual = false;
		if (!(padding3 == rhs.padding3))
			ivarsEqual = false;
		if (!(numberAttributeRecordSet == rhs.numberAttributeRecordSet))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public short getActionCode() {
		return actionCode;
	}

	public short getAttributeRecordPduType() {
		return attributeRecordPduType;
	}

	public short getAttributeRecordProtocolVersion() {
		return attributeRecordProtocolVersion;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + originatingSimulationAddress.getMarshalledSize(); // originatingSimulationAddress
		marshalSize = marshalSize + 4; // padding1
		marshalSize = marshalSize + 2; // padding2
		marshalSize = marshalSize + 1; // attributeRecordPduType
		marshalSize = marshalSize + 1; // attributeRecordProtocolVersion
		marshalSize = marshalSize + 4; // masterAttributeRecordType
		marshalSize = marshalSize + 1; // actionCode
		marshalSize = marshalSize + 1; // padding3
		marshalSize = marshalSize + 2; // numberAttributeRecordSet

		return marshalSize;
	}

	public long getMasterAttributeRecordType() {
		return masterAttributeRecordType;
	}

	public int getNumberAttributeRecordSet() {
		return numberAttributeRecordSet;
	}

	public SimulationAddress getOriginatingSimulationAddress() {
		return originatingSimulationAddress;
	}

	public int getPadding1() {
		return padding1;
	}

	public short getPadding2() {
		return padding2;
	}

	public byte getPadding3() {
		return padding3;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			originatingSimulationAddress.marshal(dos);
			dos.writeInt(padding1);
			dos.writeShort(padding2);
			dos.writeByte((byte) attributeRecordPduType);
			dos.writeByte((byte) attributeRecordProtocolVersion);
			dos.writeInt((int) masterAttributeRecordType);
			dos.writeByte((byte) actionCode);
			dos.writeByte(padding3);
			dos.writeShort((short) numberAttributeRecordSet);
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
		originatingSimulationAddress.marshal(buff);
		buff.putInt(padding1);
		buff.putShort(padding2);
		buff.put((byte) attributeRecordPduType);
		buff.put((byte) attributeRecordProtocolVersion);
		buff.putInt((int) masterAttributeRecordType);
		buff.put((byte) actionCode);
		buff.put(padding3);
		buff.putShort((short) numberAttributeRecordSet);
	} // end of marshal method

	public void setActionCode(final short pActionCode) {
		actionCode = pActionCode;
	}

	public void setAttributeRecordPduType(final short pAttributeRecordPduType) {
		attributeRecordPduType = pAttributeRecordPduType;
	}

	public void setAttributeRecordProtocolVersion(final short pAttributeRecordProtocolVersion) {
		attributeRecordProtocolVersion = pAttributeRecordProtocolVersion;
	}

	public void setMasterAttributeRecordType(final long pMasterAttributeRecordType) {
		masterAttributeRecordType = pMasterAttributeRecordType;
	}

	public void setNumberAttributeRecordSet(final int pNumberAttributeRecordSet) {
		numberAttributeRecordSet = pNumberAttributeRecordSet;
	}

	public void setOriginatingSimulationAddress(final SimulationAddress pOriginatingSimulationAddress) {
		originatingSimulationAddress = pOriginatingSimulationAddress;
	}

	public void setPadding1(final int pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final short pPadding2) {
		padding2 = pPadding2;
	}

	public void setPadding3(final byte pPadding3) {
		padding3 = pPadding3;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			originatingSimulationAddress.unmarshal(dis);
			padding1 = dis.readInt();
			padding2 = dis.readShort();
			attributeRecordPduType = (short) dis.readUnsignedByte();
			attributeRecordProtocolVersion = (short) dis.readUnsignedByte();
			masterAttributeRecordType = dis.readInt();
			actionCode = (short) dis.readUnsignedByte();
			padding3 = dis.readByte();
			numberAttributeRecordSet = dis.readUnsignedShort();
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

		originatingSimulationAddress.unmarshal(buff);
		padding1 = buff.getInt();
		padding2 = buff.getShort();
		attributeRecordPduType = (short) (buff.get() & 0xFF);
		attributeRecordProtocolVersion = (short) (buff.get() & 0xFF);
		masterAttributeRecordType = buff.getInt();
		actionCode = (short) (buff.get() & 0xFF);
		padding3 = buff.get();
		numberAttributeRecordSet = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
