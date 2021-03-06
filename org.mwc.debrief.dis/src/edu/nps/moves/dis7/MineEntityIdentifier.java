package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * The unique designation of a mine contained in the Minefield Data PDU. No
 * espdus are issued for mine entities. Section 6.2.55
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class MineEntityIdentifier extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**  */
	protected SimulationAddress simulationAddress = new SimulationAddress();

	/**  */
	protected int mineEntityNumber;

	/** Constructor */
	public MineEntityIdentifier() {
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

		if (!(obj instanceof MineEntityIdentifier))
			return false;

		final MineEntityIdentifier rhs = (MineEntityIdentifier) obj;

		if (!(simulationAddress.equals(rhs.simulationAddress)))
			ivarsEqual = false;
		if (!(mineEntityNumber == rhs.mineEntityNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + simulationAddress.getMarshalledSize(); // simulationAddress
		marshalSize = marshalSize + 2; // mineEntityNumber

		return marshalSize;
	}

	public int getMineEntityNumber() {
		return mineEntityNumber;
	}

	public SimulationAddress getSimulationAddress() {
		return simulationAddress;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			simulationAddress.marshal(dos);
			dos.writeShort((short) mineEntityNumber);
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
		simulationAddress.marshal(buff);
		buff.putShort((short) mineEntityNumber);
	} // end of marshal method

	public void setMineEntityNumber(final int pMineEntityNumber) {
		mineEntityNumber = pMineEntityNumber;
	}

	public void setSimulationAddress(final SimulationAddress pSimulationAddress) {
		simulationAddress = pSimulationAddress;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			simulationAddress.unmarshal(dis);
			mineEntityNumber = dis.readUnsignedShort();
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
		simulationAddress.unmarshal(buff);
		mineEntityNumber = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
