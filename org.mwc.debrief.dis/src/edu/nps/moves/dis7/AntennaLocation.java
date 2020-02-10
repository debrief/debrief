package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Location of the radiating portion of the antenna, specified in world
 * coordinates and entity coordinates. Section 6.2.8
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class AntennaLocation extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Location of the radiating portion of the antenna in world coordinates */
	protected Vector3Double antennaLocation = new Vector3Double();

	/** Location of the radiating portion of the antenna in entity coordinates */
	protected Vector3Float relativeAntennaLocation = new Vector3Float();

	/** Constructor */
	public AntennaLocation() {
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

		if (!(obj instanceof AntennaLocation))
			return false;

		final AntennaLocation rhs = (AntennaLocation) obj;

		if (!(antennaLocation.equals(rhs.antennaLocation)))
			ivarsEqual = false;
		if (!(relativeAntennaLocation.equals(rhs.relativeAntennaLocation)))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public Vector3Double getAntennaLocation() {
		return antennaLocation;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + antennaLocation.getMarshalledSize(); // antennaLocation
		marshalSize = marshalSize + relativeAntennaLocation.getMarshalledSize(); // relativeAntennaLocation

		return marshalSize;
	}

	public Vector3Float getRelativeAntennaLocation() {
		return relativeAntennaLocation;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			antennaLocation.marshal(dos);
			relativeAntennaLocation.marshal(dos);
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
		antennaLocation.marshal(buff);
		relativeAntennaLocation.marshal(buff);
	} // end of marshal method

	public void setAntennaLocation(final Vector3Double pAntennaLocation) {
		antennaLocation = pAntennaLocation;
	}

	public void setRelativeAntennaLocation(final Vector3Float pRelativeAntennaLocation) {
		relativeAntennaLocation = pRelativeAntennaLocation;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			antennaLocation.unmarshal(dis);
			relativeAntennaLocation.unmarshal(dis);
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
		antennaLocation.unmarshal(buff);
		relativeAntennaLocation.unmarshal(buff);
	} // end of unmarshal method
} // end of class
