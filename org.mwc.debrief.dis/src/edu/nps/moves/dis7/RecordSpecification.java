package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This record shall specify the number of record sets contained in the Record
 * Specification record and the record details. Section 6.2.73.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class RecordSpecification extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The number of record sets */
	protected long numberOfRecordSets;

	/** variable length list record specifications. */
	protected List<RecordSpecificationElement> recordSets = new ArrayList<RecordSpecificationElement>();

	/** Constructor */
	public RecordSpecification() {
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

		if (!(obj instanceof RecordSpecification))
			return false;

		final RecordSpecification rhs = (RecordSpecification) obj;

		if (!(numberOfRecordSets == rhs.numberOfRecordSets))
			ivarsEqual = false;

		for (int idx = 0; idx < recordSets.size(); idx++) {
			if (!(recordSets.get(idx).equals(rhs.recordSets.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 4; // numberOfRecordSets
		for (int idx = 0; idx < recordSets.size(); idx++) {
			final RecordSpecificationElement listElement = recordSets.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfRecordSets() {
		return recordSets.size();
	}

	public List<RecordSpecificationElement> getRecordSets() {
		return recordSets;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeInt(recordSets.size());

			for (int idx = 0; idx < recordSets.size(); idx++) {
				final RecordSpecificationElement aRecordSpecificationElement = recordSets.get(idx);
				aRecordSpecificationElement.marshal(dos);
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
	public void marshal(final java.nio.ByteBuffer buff) {
		buff.putInt(recordSets.size());

		for (int idx = 0; idx < recordSets.size(); idx++) {
			final RecordSpecificationElement aRecordSpecificationElement = recordSets.get(idx);
			aRecordSpecificationElement.marshal(buff);
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

	public void setRecordSets(final List<RecordSpecificationElement> pRecordSets) {
		recordSets = pRecordSets;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			numberOfRecordSets = dis.readInt();
			for (int idx = 0; idx < numberOfRecordSets; idx++) {
				final RecordSpecificationElement anX = new RecordSpecificationElement();
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
	public void unmarshal(final java.nio.ByteBuffer buff) {
		numberOfRecordSets = buff.getInt();
		for (int idx = 0; idx < numberOfRecordSets; idx++) {
			final RecordSpecificationElement anX = new RecordSpecificationElement();
			anX.unmarshal(buff);
			recordSets.add(anX);
		}

	} // end of unmarshal method
} // end of class
