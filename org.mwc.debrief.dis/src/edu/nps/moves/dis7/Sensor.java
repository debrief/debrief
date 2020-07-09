package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * An entity's sensor information. Section 6.2.77.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Sensor extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** the source of the Sensor Type field */
	protected short sensorTypeSource;

	/** the on/off status of the sensor */
	protected short sensorOnOffStatus;

	/** the sensor type and shall be represented by a 16-bit enumeration. */
	protected int sensorType;

	/**
	 * the station to which the sensor is assigned. A zero value shall indi- cate
	 * that this Sensor record is not associated with any particular station and
	 * represents the total quan- tity of this sensor for this entity. If this field
	 * is non-zero, it shall either reference an attached part or an articulated
	 * part
	 */
	protected long station;

	/** quantity of the sensor */
	protected int quantity;

	/** padding */
	protected int padding = 0;

	/** Constructor */
	public Sensor() {
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

		if (!(obj instanceof Sensor))
			return false;

		final Sensor rhs = (Sensor) obj;

		if (!(sensorTypeSource == rhs.sensorTypeSource))
			ivarsEqual = false;
		if (!(sensorOnOffStatus == rhs.sensorOnOffStatus))
			ivarsEqual = false;
		if (!(sensorType == rhs.sensorType))
			ivarsEqual = false;
		if (!(station == rhs.station))
			ivarsEqual = false;
		if (!(quantity == rhs.quantity))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // sensorTypeSource
		marshalSize = marshalSize + 1; // sensorOnOffStatus
		marshalSize = marshalSize + 2; // sensorType
		marshalSize = marshalSize + 4; // station
		marshalSize = marshalSize + 2; // quantity
		marshalSize = marshalSize + 2; // padding

		return marshalSize;
	}

	public int getPadding() {
		return padding;
	}

	public int getQuantity() {
		return quantity;
	}

	public short getSensorOnOffStatus() {
		return sensorOnOffStatus;
	}

	public int getSensorType() {
		return sensorType;
	}

	public short getSensorTypeSource() {
		return sensorTypeSource;
	}

	public long getStation() {
		return station;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) sensorTypeSource);
			dos.writeByte((byte) sensorOnOffStatus);
			dos.writeShort((short) sensorType);
			dos.writeInt((int) station);
			dos.writeShort((short) quantity);
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
		buff.put((byte) sensorTypeSource);
		buff.put((byte) sensorOnOffStatus);
		buff.putShort((short) sensorType);
		buff.putInt((int) station);
		buff.putShort((short) quantity);
		buff.putShort((short) padding);
	} // end of marshal method

	public void setPadding(final int pPadding) {
		padding = pPadding;
	}

	public void setQuantity(final int pQuantity) {
		quantity = pQuantity;
	}

	public void setSensorOnOffStatus(final short pSensorOnOffStatus) {
		sensorOnOffStatus = pSensorOnOffStatus;
	}

	public void setSensorType(final int pSensorType) {
		sensorType = pSensorType;
	}

	public void setSensorTypeSource(final short pSensorTypeSource) {
		sensorTypeSource = pSensorTypeSource;
	}

	public void setStation(final long pStation) {
		station = pStation;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			sensorTypeSource = (short) dis.readUnsignedByte();
			sensorOnOffStatus = (short) dis.readUnsignedByte();
			sensorType = dis.readUnsignedShort();
			station = dis.readInt();
			quantity = dis.readUnsignedShort();
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
		sensorTypeSource = (short) (buff.get() & 0xFF);
		sensorOnOffStatus = (short) (buff.get() & 0xFF);
		sensorType = buff.getShort() & 0xFFFF;
		station = buff.getInt();
		quantity = buff.getShort() & 0xFFFF;
		padding = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
