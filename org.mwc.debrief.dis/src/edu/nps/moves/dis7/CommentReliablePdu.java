package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.12.12: Arbitrary messages. Only reliable this time. Neds manual
 * intervention to fix padding in variable datums. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class CommentReliablePdu extends SimulationManagementWithReliabilityFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Fixed datum record count */
	protected long numberOfFixedDatumRecords;

	/** variable datum record count */
	protected long numberOfVariableDatumRecords;

	/** Fixed datum records */
	protected List<FixedDatum> fixedDatumRecords = new ArrayList<FixedDatum>();
	/** Variable datum records */
	protected List<VariableDatum> variableDatumRecords = new ArrayList<VariableDatum>();

	/** Constructor */
	public CommentReliablePdu() {
		setPduType((short) 62);
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

		if (!(obj instanceof CommentReliablePdu))
			return false;

		final CommentReliablePdu rhs = (CommentReliablePdu) obj;

		if (!(numberOfFixedDatumRecords == rhs.numberOfFixedDatumRecords))
			ivarsEqual = false;
		if (!(numberOfVariableDatumRecords == rhs.numberOfVariableDatumRecords))
			ivarsEqual = false;

		for (int idx = 0; idx < fixedDatumRecords.size(); idx++) {
			if (!(fixedDatumRecords.get(idx).equals(rhs.fixedDatumRecords.get(idx))))
				ivarsEqual = false;
		}

		for (int idx = 0; idx < variableDatumRecords.size(); idx++) {
			if (!(variableDatumRecords.get(idx).equals(rhs.variableDatumRecords.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<FixedDatum> getFixedDatumRecords() {
		return fixedDatumRecords;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 4; // numberOfFixedDatumRecords
		marshalSize = marshalSize + 4; // numberOfVariableDatumRecords
		for (int idx = 0; idx < fixedDatumRecords.size(); idx++) {
			final FixedDatum listElement = fixedDatumRecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		for (int idx = 0; idx < variableDatumRecords.size(); idx++) {
			final VariableDatum listElement = variableDatumRecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfFixedDatumRecords() {
		return fixedDatumRecords.size();
	}

	public long getNumberOfVariableDatumRecords() {
		return variableDatumRecords.size();
	}

	public List<VariableDatum> getVariableDatumRecords() {
		return variableDatumRecords;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeInt(fixedDatumRecords.size());
			dos.writeInt(variableDatumRecords.size());

			for (int idx = 0; idx < fixedDatumRecords.size(); idx++) {
				final FixedDatum aFixedDatum = fixedDatumRecords.get(idx);
				aFixedDatum.marshal(dos);
			} // end of list marshalling

			for (int idx = 0; idx < variableDatumRecords.size(); idx++) {
				final VariableDatum aVariableDatum = variableDatumRecords.get(idx);
				aVariableDatum.marshal(dos);
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
		buff.putInt(fixedDatumRecords.size());
		buff.putInt(variableDatumRecords.size());

		for (int idx = 0; idx < fixedDatumRecords.size(); idx++) {
			final FixedDatum aFixedDatum = fixedDatumRecords.get(idx);
			aFixedDatum.marshal(buff);
		} // end of list marshalling

		for (int idx = 0; idx < variableDatumRecords.size(); idx++) {
			final VariableDatum aVariableDatum = variableDatumRecords.get(idx);
			aVariableDatum.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setFixedDatumRecords(final List<FixedDatum> pFixedDatumRecords) {
		fixedDatumRecords = pFixedDatumRecords;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfFixedDatumRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfFixedDatumRecords(final long pNumberOfFixedDatumRecords) {
		numberOfFixedDatumRecords = pNumberOfFixedDatumRecords;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfVariableDatumRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfVariableDatumRecords(final long pNumberOfVariableDatumRecords) {
		numberOfVariableDatumRecords = pNumberOfVariableDatumRecords;
	}

	public void setVariableDatumRecords(final List<VariableDatum> pVariableDatumRecords) {
		variableDatumRecords = pVariableDatumRecords;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			numberOfFixedDatumRecords = dis.readInt();
			numberOfVariableDatumRecords = dis.readInt();
			for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
				final FixedDatum anX = new FixedDatum();
				anX.unmarshal(dis);
				fixedDatumRecords.add(anX);
			}

			for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
				final VariableDatum anX = new VariableDatum();
				anX.unmarshal(dis);
				variableDatumRecords.add(anX);
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

		numberOfFixedDatumRecords = buff.getInt();
		numberOfVariableDatumRecords = buff.getInt();
		for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
			final FixedDatum anX = new FixedDatum();
			anX.unmarshal(buff);
			fixedDatumRecords.add(anX);
		}

		for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
			final VariableDatum anX = new VariableDatum();
			anX.unmarshal(buff);
			variableDatumRecords.add(anX);
		}

	} // end of unmarshal method
} // end of class
