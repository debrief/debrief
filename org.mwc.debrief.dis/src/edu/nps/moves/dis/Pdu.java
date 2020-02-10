package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

import edu.nps.moves.disenum.PduType;
import edu.nps.moves.disutil.DisTime;

/**
 * The superclass for all PDUs. This incorporates the PduHeader record, section
 * 5.2.29.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Pdu extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The version of the protocol. 5=DIS-1995, 6=DIS-1998. */
	protected short protocolVersion = (short) 6;

	/** Exercise ID */
	protected short exerciseID = (short) 0;

	/** Type of pdu, unique for each PDU class */
	protected short pduType;

	/** value that refers to the protocol family, eg SimulationManagement, et */
	protected short protocolFamily;

	/** Timestamp value */
	protected long timestamp;

	/**
	 * Length, in bytes, of the PDU. Changed name from length to avoid use of
	 * Hibernate QL reserved word
	 */
	protected int pduLength;

	/** zero-filled array of padding */
	protected short padding = (short) 0;

	/** Constructor */
	public Pdu() {
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

		if (!(obj instanceof Pdu))
			return false;

		final Pdu rhs = (Pdu) obj;

		if (!(protocolVersion == rhs.protocolVersion))
			ivarsEqual = false;
		if (!(exerciseID == rhs.exerciseID))
			ivarsEqual = false;
		if (!(pduType == rhs.pduType))
			ivarsEqual = false;
		if (!(protocolFamily == rhs.protocolFamily))
			ivarsEqual = false;
		if (!(timestamp == rhs.timestamp))
			ivarsEqual = false;
		if (!(pduLength == rhs.pduLength))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getExerciseID() {
		return exerciseID;
	}

	public int getLength() {
		return this.getMarshalledSize();
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // protocolVersion
		marshalSize = marshalSize + 1; // exerciseID
		marshalSize = marshalSize + 1; // pduType
		marshalSize = marshalSize + 1; // protocolFamily
		marshalSize = marshalSize + 4; // timestamp
		marshalSize = marshalSize + 2; // pduLength
		marshalSize = marshalSize + 2; // padding

		return marshalSize;
	}

	public short getPadding() {
		return padding;
	}

	public int getPduLength() {
		return pduLength;
	}

	public short getPduType() {
		return pduType;
	}

	/**
	 * Returns the PduType, an enumeration from the disenum jar file. This is an
	 * enumerated java type, rather than a simple short integer. This should NOT be
	 * marshalled to DIS or XML.
	 *
	 * @return this Pdu's type enumeration
	 */
	public PduType getPduTypeEnum() {
		return PduType.lookup[pduType];
	}

	public short getProtocolFamily() {
		return protocolFamily;
	}

	public short getProtocolVersion() {
		return protocolVersion;
	}

	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * A convenience method for marshalling to a byte array. This is not as
	 * efficient as reusing a ByteBuffer, but it <em>is</em> easy.
	 *
	 * @return a byte array with the marshalled {@link Pdu}
	 * @since ??
	 */
	public byte[] marshal() {
		final byte[] data = new byte[getMarshalledSize()];
		final java.nio.ByteBuffer buff = java.nio.ByteBuffer.wrap(data);
		marshal(buff);
		return data;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) protocolVersion);
			dos.writeByte((byte) exerciseID);
			dos.writeByte((byte) pduType);
			dos.writeByte((byte) protocolFamily);
			dos.writeInt((int) timestamp);
			dos.writeShort(this.getLength());
			dos.writeShort(padding);
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
		buff.put((byte) protocolVersion);
		buff.put((byte) exerciseID);
		buff.put((byte) pduType);
		buff.put((byte) protocolFamily);
		buff.putInt((int) timestamp);
		buff.putShort((short) this.getLength());
		buff.putShort(padding);
	} // end of marshal method

	/**
	 * A convieneince method to marshal to a byte array with the timestamp set to
	 * the DIS standard for absolute timestamps (which works only if the host is
	 * slaved to NTP). This means the timestamp will roll over every hour.
	 *
	 * @return IEEE format byte array, with the timestamp set to the current DIS
	 *         time
	 */
	public byte[] marshalWithDisAbsoluteTimestamp() {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getDisAbsoluteTimestamp());
		return this.marshal();
	}

	public void marshalWithDisAbsoluteTimestamp(final java.nio.ByteBuffer buff) {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getDisAbsoluteTimestamp());
		this.marshal(buff);
	}

	/**
	 * A convieneince method to marshal to a byte array with the timestamp set to
	 * the DIS standard for relative timestamps. The timestamp will roll over every
	 * hour
	 *
	 * @return IEEE format byte array, with the timestamp set to relative DIS time
	 */
	public byte[] marshalWithDisRelativeTimestamp() {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getDisRelativeTimestamp());
		return this.marshal();
	}

	public void marshalWithDisRelativeTimestamp(final java.nio.ByteBuffer buff) {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getDisRelativeTimestamp());
		this.marshal(buff);
	}

	/**
	 * A convienience method to marshal a PDU using the NPS-specific format for
	 * timestamps, which is hundredths of a second since the start of the year. This
	 * effectively eliminates the rollover issues from a practical standpoint.
	 *
	 * @return IEEE format byte array, with the timestamp set to hundredths of a
	 *         second since the start of the year
	 */
	public byte[] marshalWithNpsTimestamp() {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getNpsTimestamp());
		return this.marshal();
	}

	public void marshalWithNpsTimestamp(final java.nio.ByteBuffer buff) {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getNpsTimestamp());
		this.marshal(buff);
	}

	/**
	 * Another option for marshalling with the timestamp field set automatically.
	 * The UNIX time is conventionally seconds since January 1, 1970. UTC time is
	 * used, and leap seconds are excluded. This approach is popular in the wild,
	 * but the time resolution is not very good for high frequency updates, such as
	 * aircraft. An entity updating at 30 PDUs/second would see 30 PDUs sent out
	 * with the same timestamp, and have 29 of them discarded as duplicate packets.
	 *
	 * Note that there are other "Unix times", such milliseconds since 1/1/1970,
	 * saved in a long. This cannot be used, since the value is saved in a long.
	 * Java's System.getCurrentTimeMillis() uses this value.
	 *
	 * @return IEEE format byte array, with the timetamp set to seconds since 1970
	 */
	public byte[] marshalWithUnixTimestamp() {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getUnixTimestamp());
		return this.marshal();
	}

	public void marshalWithUnixTimestamp(final java.nio.ByteBuffer buff) {
		final DisTime disTime = DisTime.getInstance();
		this.setTimestamp(disTime.getUnixTimestamp());
		this.marshal(buff);
	}

	public long readUnsignedInt(final DataInputStream dis) {
		final int fourBytes[] = new int[4];

		try {
			fourBytes[0] = dis.readUnsignedByte();
			fourBytes[1] = dis.readUnsignedByte();
			fourBytes[2] = dis.readUnsignedByte();
			fourBytes[3] = dis.readUnsignedByte();
		} catch (final Exception e) {
			System.out.println(e);
		}

		return this.shiftBytes(fourBytes);
	}

	public long readUnsignedInt(final java.nio.ByteBuffer buff) {
		final int fourBytes[] = new int[4];

		try {
			fourBytes[0] = (buff.get()) & 0xff;
			fourBytes[1] = (buff.get()) & 0xff;
			fourBytes[2] = (buff.get()) & 0xff;
			fourBytes[3] = (buff.get()) & 0xff;
		} catch (final Exception e) {
			System.out.println(e);
		}

		return this.shiftBytes(fourBytes);
	}

	public void setExerciseID(final short pExerciseID) {
		exerciseID = pExerciseID;
	}

	public void setPadding(final short pPadding) {
		padding = pPadding;
	}

	public void setPduLength(final int pPduLength) {
		pduLength = pPduLength;
	}

	public void setPduType(final short pPduType) {
		pduType = pPduType;
	}

	public void setProtocolFamily(final short pProtocolFamily) {
		protocolFamily = pProtocolFamily;
	}

	public void setProtocolVersion(final short pProtocolVersion) {
		protocolVersion = pProtocolVersion;
	}

	public void setTimestamp(final long pTimestamp) {
		timestamp = pTimestamp;
	}

	private long shiftBytes(final int[] fourBytes) {
		long value = 0;
		value = (fourBytes[0] << 24 | fourBytes[1] << 16 | fourBytes[2] << 8 | fourBytes[3]) & 0xFFFFFFFFL;

		return value;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			protocolVersion = (short) dis.readUnsignedByte();
			exerciseID = (short) dis.readUnsignedByte();
			pduType = (short) dis.readUnsignedByte();
			protocolFamily = (short) dis.readUnsignedByte();
			timestamp = dis.readInt();
			pduLength = dis.readUnsignedShort();
			padding = dis.readShort();
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
		protocolVersion = (short) (buff.get() & 0xFF);
		exerciseID = (short) (buff.get() & 0xFF);
		pduType = (short) (buff.get() & 0xFF);
		protocolFamily = (short) (buff.get() & 0xFF);
		timestamp = buff.getInt();
		pduLength = buff.getShort() & 0xFFFF;
		padding = buff.getShort();
	} // end of unmarshal method
} // end of class
