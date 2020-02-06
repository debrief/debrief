package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.2.14.1. A Simulation Address record shall consist of the Site
 * Identification number and the Application Identification number.
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class SimulationAddress extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The site ID */
	protected int site;

	/** The application ID */
	protected int application;

	/** Constructor */
	public SimulationAddress() {
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

		if (!(obj instanceof SimulationAddress))
			return false;

		final SimulationAddress rhs = (SimulationAddress) obj;

		if (!(site == rhs.site))
			ivarsEqual = false;
		if (!(application == rhs.application))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public int getApplication() {
		return application;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 2; // site
		marshalSize = marshalSize + 2; // application

		return marshalSize;
	}

	public int getSite() {
		return site;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeShort((short) site);
			dos.writeShort((short) application);
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
		buff.putShort((short) site);
		buff.putShort((short) application);
	} // end of marshal method

	public void setApplication(final int pApplication) {
		application = pApplication;
	}

	public void setSite(final int pSite) {
		site = pSite;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			site = dis.readUnsignedShort();
			application = dis.readUnsignedShort();
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
		site = buff.getShort() & 0xFFFF;
		application = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
