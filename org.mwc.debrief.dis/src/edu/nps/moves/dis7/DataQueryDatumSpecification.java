package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * List of fixed and variable datum ID records. Section 6.2.17
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DataQueryDatumSpecification extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Number of fixed datum IDs */
	protected long numberOfFixedDatums;

	/** Number of variable datum IDs */
	protected long numberOfVariableDatums;

	/** variable length list fixed datum IDs */
	protected UnsignedDISInteger fixedDatumIDList = new UnsignedDISInteger();

	/** variable length list variable datum IDs */
	protected UnsignedDISInteger variableDatumIDList = new UnsignedDISInteger();

	/** Constructor */
	public DataQueryDatumSpecification() {
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

		if (!(obj instanceof DataQueryDatumSpecification))
			return false;

		final DataQueryDatumSpecification rhs = (DataQueryDatumSpecification) obj;

		if (!(numberOfFixedDatums == rhs.numberOfFixedDatums))
			ivarsEqual = false;
		if (!(numberOfVariableDatums == rhs.numberOfVariableDatums))
			ivarsEqual = false;
		if (!(fixedDatumIDList.equals(rhs.fixedDatumIDList)))
			ivarsEqual = false;
		if (!(variableDatumIDList.equals(rhs.variableDatumIDList)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public UnsignedDISInteger getFixedDatumIDList() {
		return fixedDatumIDList;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // numberOfFixedDatums
		marshalSize = marshalSize + 4; // numberOfVariableDatums
		marshalSize = marshalSize + fixedDatumIDList.getMarshalledSize(); // fixedDatumIDList
		marshalSize = marshalSize + variableDatumIDList.getMarshalledSize(); // variableDatumIDList

		return marshalSize;
	}

	public long getNumberOfFixedDatums() {
		return numberOfFixedDatums;
	}

	public long getNumberOfVariableDatums() {
		return numberOfVariableDatums;
	}

	public UnsignedDISInteger getVariableDatumIDList() {
		return variableDatumIDList;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) numberOfFixedDatums);
			dos.writeInt((int) numberOfVariableDatums);
			fixedDatumIDList.marshal(dos);
			variableDatumIDList.marshal(dos);
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
		buff.putInt((int) numberOfFixedDatums);
		buff.putInt((int) numberOfVariableDatums);
		fixedDatumIDList.marshal(buff);
		variableDatumIDList.marshal(buff);
	} // end of marshal method

	public void setFixedDatumIDList(final UnsignedDISInteger pFixedDatumIDList) {
		fixedDatumIDList = pFixedDatumIDList;
	}

	public void setNumberOfFixedDatums(final long pNumberOfFixedDatums) {
		numberOfFixedDatums = pNumberOfFixedDatums;
	}

	public void setNumberOfVariableDatums(final long pNumberOfVariableDatums) {
		numberOfVariableDatums = pNumberOfVariableDatums;
	}

	public void setVariableDatumIDList(final UnsignedDISInteger pVariableDatumIDList) {
		variableDatumIDList = pVariableDatumIDList;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			numberOfFixedDatums = dis.readInt();
			numberOfVariableDatums = dis.readInt();
			fixedDatumIDList.unmarshal(dis);
			variableDatumIDList.unmarshal(dis);
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
		numberOfFixedDatums = buff.getInt();
		numberOfVariableDatums = buff.getInt();
		fixedDatumIDList.unmarshal(buff);
		variableDatumIDList.unmarshal(buff);
	} // end of unmarshal method
} // end of class
