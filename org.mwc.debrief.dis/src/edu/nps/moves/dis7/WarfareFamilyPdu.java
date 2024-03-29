package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * abstract superclass for fire and detonation pdus that have shared
 * information. Section 7.3 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class WarfareFamilyPdu extends Pdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the entity that shot */
	protected EntityID firingEntityID = new EntityID();

	/** ID of the entity that is being shot at */
	protected EntityID targetEntityID = new EntityID();

	/** Constructor */
	public WarfareFamilyPdu() {
		setProtocolFamily((short) 2);
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

		if (!(obj instanceof WarfareFamilyPdu))
			return false;

		final WarfareFamilyPdu rhs = (WarfareFamilyPdu) obj;

		if (!(firingEntityID.equals(rhs.firingEntityID)))
			ivarsEqual = false;
		if (!(targetEntityID.equals(rhs.targetEntityID)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getFiringEntityID() {
		return firingEntityID;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + firingEntityID.getMarshalledSize(); // firingEntityID
		marshalSize = marshalSize + targetEntityID.getMarshalledSize(); // targetEntityID

		return marshalSize;
	}

	public EntityID getTargetEntityID() {
		return targetEntityID;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			firingEntityID.marshal(dos);
			targetEntityID.marshal(dos);
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
		firingEntityID.marshal(buff);
		targetEntityID.marshal(buff);
	} // end of marshal method

	public void setFiringEntityID(final EntityID pFiringEntityID) {
		firingEntityID = pFiringEntityID;
	}

	public void setTargetEntityID(final EntityID pTargetEntityID) {
		targetEntityID = pTargetEntityID;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			firingEntityID.unmarshal(dis);
			targetEntityID.unmarshal(dis);
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

		firingEntityID.unmarshal(buff);
		targetEntityID.unmarshal(buff);
	} // end of unmarshal method
} // end of class
