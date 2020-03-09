package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Unique designation of an attached or unattached intercom in an event or
 * exercirse. Section 6.2.48
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IntercomIdentifier extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected int siteNumber;

	protected int applicationNumber;

	protected int referenceNumber;

	protected int intercomNumber;

	/** Constructor */
	public IntercomIdentifier() {
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

		if (!(obj instanceof IntercomIdentifier))
			return false;

		final IntercomIdentifier rhs = (IntercomIdentifier) obj;

		if (!(siteNumber == rhs.siteNumber))
			ivarsEqual = false;
		if (!(applicationNumber == rhs.applicationNumber))
			ivarsEqual = false;
		if (!(referenceNumber == rhs.referenceNumber))
			ivarsEqual = false;
		if (!(intercomNumber == rhs.intercomNumber))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getApplicationNumber() {
		return applicationNumber;
	}

	public int getIntercomNumber() {
		return intercomNumber;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // siteNumber
		marshalSize = marshalSize + 2; // applicationNumber
		marshalSize = marshalSize + 2; // referenceNumber
		marshalSize = marshalSize + 2; // intercomNumber

		return marshalSize;
	}

	public int getReferenceNumber() {
		return referenceNumber;
	}

	public int getSiteNumber() {
		return siteNumber;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) siteNumber);
			dos.writeShort((short) applicationNumber);
			dos.writeShort((short) referenceNumber);
			dos.writeShort((short) intercomNumber);
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
		buff.putShort((short) siteNumber);
		buff.putShort((short) applicationNumber);
		buff.putShort((short) referenceNumber);
		buff.putShort((short) intercomNumber);
	} // end of marshal method

	public void setApplicationNumber(final int pApplicationNumber) {
		applicationNumber = pApplicationNumber;
	}

	public void setIntercomNumber(final int pIntercomNumber) {
		intercomNumber = pIntercomNumber;
	}

	public void setReferenceNumber(final int pReferenceNumber) {
		referenceNumber = pReferenceNumber;
	}

	public void setSiteNumber(final int pSiteNumber) {
		siteNumber = pSiteNumber;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			siteNumber = dis.readUnsignedShort();
			applicationNumber = dis.readUnsignedShort();
			referenceNumber = dis.readUnsignedShort();
			intercomNumber = dis.readUnsignedShort();
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
		siteNumber = buff.getShort() & 0xFFFF;
		applicationNumber = buff.getShort() & 0xFFFF;
		referenceNumber = buff.getShort() & 0xFFFF;
		intercomNumber = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
