package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 5.2.44: Grid data record, representation 0
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class GridAxisRecordRepresentation0 extends GridAxisRecord implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** number of bytes of environmental state data */
	protected int numberOfBytes;

	/**
	 * variable length variablelist of data parameters ^^^this is wrong--need
	 * padding as well
	 */
	protected List<OneByteChunk> dataValues = new ArrayList<OneByteChunk>();

	/** Constructor */
	public GridAxisRecordRepresentation0() {
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

		if (!(obj instanceof GridAxisRecordRepresentation0))
			return false;

		final GridAxisRecordRepresentation0 rhs = (GridAxisRecordRepresentation0) obj;

		if (!(numberOfBytes == rhs.numberOfBytes))
			ivarsEqual = false;

		for (int idx = 0; idx < dataValues.size(); idx++) {
			if (!(dataValues.get(idx).equals(rhs.dataValues.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<OneByteChunk> getDataValues() {
		return dataValues;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 2; // numberOfBytes
		for (int idx = 0; idx < dataValues.size(); idx++) {
			final OneByteChunk listElement = dataValues.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public int getNumberOfBytes() {
		return dataValues.size();
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeShort((short) dataValues.size());

			for (int idx = 0; idx < dataValues.size(); idx++) {
				final OneByteChunk aOneByteChunk = dataValues.get(idx);
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
		buff.putShort((short) dataValues.size());

		for (int idx = 0; idx < dataValues.size(); idx++) {
			final OneByteChunk aOneByteChunk = dataValues.get(idx);
			aOneByteChunk.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setDataValues(final List<OneByteChunk> pDataValues) {
		dataValues = pDataValues;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfBytes
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfBytes(final int pNumberOfBytes) {
		numberOfBytes = pNumberOfBytes;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			numberOfBytes = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfBytes; idx++) {
				final OneByteChunk anX = new OneByteChunk();
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

		numberOfBytes = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfBytes; idx++) {
			final OneByteChunk anX = new OneByteChunk();
			anX.unmarshal(buff);
			dataValues.add(anX);
		}

	} // end of unmarshal method
} // end of class
