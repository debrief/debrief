package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 5.2.44: Grid data record, representation 1
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class GridAxisRecordRepresentation2 extends GridAxisRecord implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** number of values */
	protected int numberOfValues;

	/**
	 * variable length list of data parameters ^^^this is wrong--need padding as
	 * well
	 */
	protected List<FourByteChunk> dataValues = new ArrayList<FourByteChunk>();

	/** Constructor */
	public GridAxisRecordRepresentation2() {
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

		if (!(obj instanceof GridAxisRecordRepresentation2))
			return false;

		final GridAxisRecordRepresentation2 rhs = (GridAxisRecordRepresentation2) obj;

		if (!(numberOfValues == rhs.numberOfValues))
			ivarsEqual = false;

		for (int idx = 0; idx < dataValues.size(); idx++) {
			if (!(dataValues.get(idx).equals(rhs.dataValues.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<FourByteChunk> getDataValues() {
		return dataValues;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // numberOfValues
		for (int idx = 0; idx < dataValues.size(); idx++) {
			final FourByteChunk listElement = dataValues.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public int getNumberOfValues() {
		return dataValues.size();
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeShort((short) dataValues.size());

			for (int idx = 0; idx < dataValues.size(); idx++) {
				final FourByteChunk aFourByteChunk = dataValues.get(idx);
				aFourByteChunk.marshal(dos);
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
		buff.putShort((short) dataValues.size());

		for (int idx = 0; idx < dataValues.size(); idx++) {
			final FourByteChunk aFourByteChunk = dataValues.get(idx);
			aFourByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setDataValues(final List<FourByteChunk> pDataValues) {
		dataValues = pDataValues;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfValues
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfValues(final int pNumberOfValues) {
		numberOfValues = pNumberOfValues;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			numberOfValues = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfValues; idx++) {
				final FourByteChunk anX = new FourByteChunk();
				anX.unmarshal(dis);
				dataValues.add(anX);
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

		numberOfValues = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfValues; idx++) {
			final FourByteChunk anX = new FourByteChunk();
			anX.unmarshal(buff);
			dataValues.add(anX);
		}

	} // end of unmarshal method
} // end of class
