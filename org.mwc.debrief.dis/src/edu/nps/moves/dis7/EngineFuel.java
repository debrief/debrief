package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Information about an entity's engine fuel. Section 6.2.24.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EngineFuel extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Fuel quantity, units specified by next field */
	protected long fuelQuantity;

	/** Units in which the fuel is measured */
	protected short fuelMeasurementUnits;

	/** Type of fuel */
	protected short fuelType;

	/** Location of fuel as related to entity. See section 14 of EBV document */
	protected short fuelLocation;

	/** padding */
	protected short padding = (short) 0;

	/** Constructor */
	public EngineFuel() {
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

		if (!(obj instanceof EngineFuel))
			return false;

		final EngineFuel rhs = (EngineFuel) obj;

		if (!(fuelQuantity == rhs.fuelQuantity))
			ivarsEqual = false;
		if (!(fuelMeasurementUnits == rhs.fuelMeasurementUnits))
			ivarsEqual = false;
		if (!(fuelType == rhs.fuelType))
			ivarsEqual = false;
		if (!(fuelLocation == rhs.fuelLocation))
			ivarsEqual = false;
		if (!(padding == rhs.padding))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getFuelLocation() {
		return fuelLocation;
	}

	public short getFuelMeasurementUnits() {
		return fuelMeasurementUnits;
	}

	public long getFuelQuantity() {
		return fuelQuantity;
	}

	public short getFuelType() {
		return fuelType;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // fuelQuantity
		marshalSize = marshalSize + 1; // fuelMeasurementUnits
		marshalSize = marshalSize + 1; // fuelType
		marshalSize = marshalSize + 1; // fuelLocation
		marshalSize = marshalSize + 1; // padding

		return marshalSize;
	}

	public short getPadding() {
		return padding;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) fuelQuantity);
			dos.writeByte((byte) fuelMeasurementUnits);
			dos.writeByte((byte) fuelType);
			dos.writeByte((byte) fuelLocation);
			dos.writeByte((byte) padding);
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
		buff.putInt((int) fuelQuantity);
		buff.put((byte) fuelMeasurementUnits);
		buff.put((byte) fuelType);
		buff.put((byte) fuelLocation);
		buff.put((byte) padding);
	} // end of marshal method

	public void setFuelLocation(final short pFuelLocation) {
		fuelLocation = pFuelLocation;
	}

	public void setFuelMeasurementUnits(final short pFuelMeasurementUnits) {
		fuelMeasurementUnits = pFuelMeasurementUnits;
	}

	public void setFuelQuantity(final long pFuelQuantity) {
		fuelQuantity = pFuelQuantity;
	}

	public void setFuelType(final short pFuelType) {
		fuelType = pFuelType;
	}

	public void setPadding(final short pPadding) {
		padding = pPadding;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			fuelQuantity = dis.readInt();
			fuelMeasurementUnits = (short) dis.readUnsignedByte();
			fuelType = (short) dis.readUnsignedByte();
			fuelLocation = (short) dis.readUnsignedByte();
			padding = (short) dis.readUnsignedByte();
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
		fuelQuantity = buff.getInt();
		fuelMeasurementUnits = (short) (buff.get() & 0xFF);
		fuelType = (short) (buff.get() & 0xFF);
		fuelLocation = (short) (buff.get() & 0xFF);
		padding = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
