package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * List of fixed and variable datum records. Section 6.2.18
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DatumSpecification extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Number of fixed datums */
	protected long numberOfFixedDatums;

	/** Number of variable datums */
	protected long numberOfVariableDatums;

	/** variable length list fixed datums */
	protected FixedDatum fixedDatumList = new FixedDatum();

	/** variable length list variable datums. See 6.2.93 */
	protected VariableDatum variableDatumList = new VariableDatum();

	/** Constructor */
	public DatumSpecification() {
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

		if (!(obj instanceof DatumSpecification))
			return false;

		final DatumSpecification rhs = (DatumSpecification) obj;

		if (!(numberOfFixedDatums == rhs.numberOfFixedDatums))
			ivarsEqual = false;
		if (!(numberOfVariableDatums == rhs.numberOfVariableDatums))
			ivarsEqual = false;
		if (!(fixedDatumList.equals(rhs.fixedDatumList)))
			ivarsEqual = false;
		if (!(variableDatumList.equals(rhs.variableDatumList)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public FixedDatum getFixedDatumList() {
		return fixedDatumList;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // numberOfFixedDatums
		marshalSize = marshalSize + 4; // numberOfVariableDatums
		marshalSize = marshalSize + fixedDatumList.getMarshalledSize(); // fixedDatumList
		marshalSize = marshalSize + variableDatumList.getMarshalledSize(); // variableDatumList

		return marshalSize;
	}

	public long getNumberOfFixedDatums() {
		return numberOfFixedDatums;
	}

	public long getNumberOfVariableDatums() {
		return numberOfVariableDatums;
	}

	public VariableDatum getVariableDatumList() {
		return variableDatumList;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt((int) numberOfFixedDatums);
			dos.writeInt((int) numberOfVariableDatums);
			fixedDatumList.marshal(dos);
			variableDatumList.marshal(dos);
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
		fixedDatumList.marshal(buff);
		variableDatumList.marshal(buff);
	} // end of marshal method

	public void setFixedDatumList(final FixedDatum pFixedDatumList) {
		fixedDatumList = pFixedDatumList;
	}

	public void setNumberOfFixedDatums(final long pNumberOfFixedDatums) {
		numberOfFixedDatums = pNumberOfFixedDatums;
	}

	public void setNumberOfVariableDatums(final long pNumberOfVariableDatums) {
		numberOfVariableDatums = pNumberOfVariableDatums;
	}

	public void setVariableDatumList(final VariableDatum pVariableDatumList) {
		variableDatumList = pVariableDatumList;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			numberOfFixedDatums = dis.readInt();
			numberOfVariableDatums = dis.readInt();
			fixedDatumList.unmarshal(dis);
			variableDatumList.unmarshal(dis);
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
		fixedDatumList.unmarshal(buff);
		variableDatumList.unmarshal(buff);
	} // end of unmarshal method
} // end of class
