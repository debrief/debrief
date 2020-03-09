package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.6.9. Change state information with the data contained in this.
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SetDataPdu extends SimulationManagementFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of request */
	protected long requestID;

	/** padding */
	protected long padding1 = 0;

	/** Number of fixed datum records */
	protected long numberOfFixedDatumRecords;

	/** Number of variable datum records */
	protected long numberOfVariableDatumRecords;

	/** variable length list of fixed datums */
	protected List<UnsignedIntegerWrapper> fixedDatums = new ArrayList<UnsignedIntegerWrapper>();
	/** variable length list of variable length datums */
	protected List<UnsignedIntegerWrapper> variableDatums = new ArrayList<UnsignedIntegerWrapper>();

	/** Constructor */
	public SetDataPdu() {
		setPduType((short) 19);
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

		if (!(obj instanceof SetDataPdu))
			return false;

		final SetDataPdu rhs = (SetDataPdu) obj;

		if (!(requestID == rhs.requestID))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(numberOfFixedDatumRecords == rhs.numberOfFixedDatumRecords))
			ivarsEqual = false;
		if (!(numberOfVariableDatumRecords == rhs.numberOfVariableDatumRecords))
			ivarsEqual = false;

		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			if (!(fixedDatums.get(idx).equals(rhs.fixedDatums.get(idx))))
				ivarsEqual = false;
		}

		for (int idx = 0; idx < variableDatums.size(); idx++) {
			if (!(variableDatums.get(idx).equals(rhs.variableDatums.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public List<UnsignedIntegerWrapper> getFixedDatums() {
		return fixedDatums;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + 4; // requestID
		marshalSize = marshalSize + 4; // padding1
		marshalSize = marshalSize + 4; // numberOfFixedDatumRecords
		marshalSize = marshalSize + 4; // numberOfVariableDatumRecords
		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			final UnsignedIntegerWrapper listElement = fixedDatums.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}
		for (int idx = 0; idx < variableDatums.size(); idx++) {
			final UnsignedIntegerWrapper listElement = variableDatums.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public long getNumberOfFixedDatumRecords() {
		return fixedDatums.size();
	}

	public long getNumberOfVariableDatumRecords() {
		return variableDatums.size();
	}

	public long getPadding1() {
		return padding1;
	}

	public long getRequestID() {
		return requestID;
	}

	public List<UnsignedIntegerWrapper> getVariableDatums() {
		return variableDatums;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			dos.writeInt((int) requestID);
			dos.writeInt((int) padding1);
			dos.writeInt(fixedDatums.size());
			dos.writeInt(variableDatums.size());

			for (int idx = 0; idx < fixedDatums.size(); idx++) {
				final UnsignedIntegerWrapper aUnsignedIntegerWrapper = fixedDatums.get(idx);
				aUnsignedIntegerWrapper.marshal(dos);
			} // end of list marshalling

			for (int idx = 0; idx < variableDatums.size(); idx++) {
				final UnsignedIntegerWrapper aUnsignedIntegerWrapper = variableDatums.get(idx);
				aUnsignedIntegerWrapper.marshal(dos);
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
		buff.putInt((int) requestID);
		buff.putInt((int) padding1);
		buff.putInt(fixedDatums.size());
		buff.putInt(variableDatums.size());

		for (int idx = 0; idx < fixedDatums.size(); idx++) {
			final UnsignedIntegerWrapper aUnsignedIntegerWrapper = fixedDatums.get(idx);
			aUnsignedIntegerWrapper.marshal(buff);
		} // end of list marshalling

		for (int idx = 0; idx < variableDatums.size(); idx++) {
			final UnsignedIntegerWrapper aUnsignedIntegerWrapper = variableDatums.get(idx);
			aUnsignedIntegerWrapper.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setFixedDatums(final List<UnsignedIntegerWrapper> pFixedDatums) {
		fixedDatums = pFixedDatums;
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

	public void setPadding1(final long pPadding1) {
		padding1 = pPadding1;
	}

	public void setRequestID(final long pRequestID) {
		requestID = pRequestID;
	}

	public void setVariableDatums(final List<UnsignedIntegerWrapper> pVariableDatums) {
		variableDatums = pVariableDatums;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			requestID = dis.readInt();
			padding1 = dis.readInt();
			numberOfFixedDatumRecords = dis.readInt();
			numberOfVariableDatumRecords = dis.readInt();
			for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
				final UnsignedIntegerWrapper anX = new UnsignedIntegerWrapper();
				anX.unmarshal(dis);
				fixedDatums.add(anX);
			}

			for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
				final UnsignedIntegerWrapper anX = new UnsignedIntegerWrapper();
				anX.unmarshal(dis);
				variableDatums.add(anX);
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

		requestID = buff.getInt();
		padding1 = buff.getInt();
		numberOfFixedDatumRecords = buff.getInt();
		numberOfVariableDatumRecords = buff.getInt();
		for (int idx = 0; idx < numberOfFixedDatumRecords; idx++) {
			final UnsignedIntegerWrapper anX = new UnsignedIntegerWrapper();
			anX.unmarshal(buff);
			fixedDatums.add(anX);
		}

		for (int idx = 0; idx < numberOfVariableDatumRecords; idx++) {
			final UnsignedIntegerWrapper anX = new UnsignedIntegerWrapper();
			anX.unmarshal(buff);
			variableDatums.add(anX);
		}

	} // end of unmarshal method
} // end of class
