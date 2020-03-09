package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Effect of IO on an entity. Section 6.2.49.3
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IOEffect extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected long recordType = 5500;

	protected int recordLength = 16;

	protected short ioStatus;

	protected short ioLinkType;

	protected EntityID ioEffect = new EntityID();

	protected short ioEffectDutyCycle;

	protected int ioEffectDuration;

	protected int ioProcess;

	protected int padding = 0;

	/** Constructor */
	public IOEffect() {
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

		if (!(obj instanceof IOEffect))
			return false;

		final IOEffect rhs = (IOEffect) obj;

		if (!(recordType == rhs.recordType))
			ivarsEqual = false;
		if (!(recordLength == rhs.recordLength))
			ivarsEqual = false;
		if (!(ioStatus == rhs.ioStatus))
			ivarsEqual = false;
		if (!(ioLinkType == rhs.ioLinkType))
			ivarsEqual = false;
		if (!(ioEffect.equals(rhs.ioEffect)))
			ivarsEqual = false;
		if (!(ioEffectDutyCycle == rhs.ioEffectDutyCycle))
			ivarsEqual = false;
		if (!(ioEffectDuration == rhs.ioEffectDuration))
			ivarsEqual = false;
		if (!(ioProcess == rhs.ioProcess))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public EntityID getIoEffect() {
		return ioEffect;
	}

	public int getIoEffectDuration() {
		return ioEffectDuration;
	}

	public short getIoEffectDutyCycle() {
		return ioEffectDutyCycle;
	}

	public short getIoLinkType() {
		return ioLinkType;
	}

	public int getIoProcess() {
		return ioProcess;
	}

	public short getIoStatus() {
		return ioStatus;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // recordType
		marshalSize = marshalSize + 2; // recordLength
		marshalSize = marshalSize + 1; // ioStatus
		marshalSize = marshalSize + 1; // ioLinkType
		marshalSize = marshalSize + ioEffect.getMarshalledSize(); // ioEffect
		marshalSize = marshalSize + 1; // ioEffectDutyCycle
		marshalSize = marshalSize + 2; // ioEffectDuration
		marshalSize = marshalSize + 2; // ioProcess
		marshalSize = marshalSize + 2; // padding

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public int getRecordLength() {
		return recordLength;
	}

	public long getRecordType() {
		return recordType;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) recordType);
			dos.writeShort((short) recordLength);
			dos.writeByte((byte) ioStatus);
			dos.writeByte((byte) ioLinkType);
			ioEffect.marshal(dos);
			dos.writeByte((byte) ioEffectDutyCycle);
			dos.writeShort((short) ioEffectDuration);
			dos.writeShort((short) ioProcess);
			dos.writeShort((short) padding);
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
		buff.putInt((int) recordType);
		buff.putShort((short) recordLength);
		buff.put((byte) ioStatus);
		buff.put((byte) ioLinkType);
		ioEffect.marshal(buff);
		buff.put((byte) ioEffectDutyCycle);
		buff.putShort((short) ioEffectDuration);
		buff.putShort((short) ioProcess);
		buff.putShort((short) padding);
	} // end of marshal method

	public void setIoEffect(final EntityID pIoEffect) {
		ioEffect = pIoEffect;
	}

	public void setIoEffectDuration(final int pIoEffectDuration) {
		ioEffectDuration = pIoEffectDuration;
	}

	public void setIoEffectDutyCycle(final short pIoEffectDutyCycle) {
		ioEffectDutyCycle = pIoEffectDutyCycle;
	}

	public void setIoLinkType(final short pIoLinkType) {
		ioLinkType = pIoLinkType;
	}

	public void setIoProcess(final int pIoProcess) {
		ioProcess = pIoProcess;
	}

	public void setIoStatus(final short pIoStatus) {
		ioStatus = pIoStatus;
	}

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void setRecordLength(final int pRecordLength) {
		recordLength = pRecordLength;
	}

	public void setRecordType(final long pRecordType) {
		recordType = pRecordType;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			recordType = dis.readInt();
			recordLength = dis.readUnsignedShort();
			ioStatus = (short) dis.readUnsignedByte();
			ioLinkType = (short) dis.readUnsignedByte();
			ioEffect.unmarshal(dis);
			ioEffectDutyCycle = (short) dis.readUnsignedByte();
			ioEffectDuration = dis.readUnsignedShort();
			ioProcess = dis.readUnsignedShort();
			padding = dis.readUnsignedShort();
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
		recordType = buff.getInt();
		recordLength = buff.getShort() & 0xFFFF;
		ioStatus = (short) (buff.get() & 0xFF);
		ioLinkType = (short) (buff.get() & 0xFF);
		ioEffect.unmarshal(buff);
		ioEffectDutyCycle = (short) (buff.get() & 0xFF);
		ioEffectDuration = buff.getShort() & 0xFFFF;
		ioProcess = buff.getShort() & 0xFFFF;
		padding = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
