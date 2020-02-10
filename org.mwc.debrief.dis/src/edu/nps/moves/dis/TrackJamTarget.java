package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * One track/jam target
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class TrackJamTarget extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** track/jam target */
	protected EntityID trackJam = new EntityID();

	/** Emitter ID */
	protected short emitterID;

	/** beam ID */
	protected short beamID;

	/** Constructor */
	public TrackJamTarget() {
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

		if (!(obj instanceof TrackJamTarget))
			return false;

		final TrackJamTarget rhs = (TrackJamTarget) obj;

		if (!(trackJam.equals(rhs.trackJam)))
			ivarsEqual = false;
		if (!(emitterID == rhs.emitterID))
			ivarsEqual = false;
		if (!(beamID == rhs.beamID))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getBeamID() {
		return beamID;
	}

	public short getEmitterID() {
		return emitterID;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + trackJam.getMarshalledSize(); // trackJam
		marshalSize = marshalSize + 1; // emitterID
		marshalSize = marshalSize + 1; // beamID

		return marshalSize;
	}

	public EntityID getTrackJam() {
		return trackJam;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			trackJam.marshal(dos);
			dos.writeByte((byte) emitterID);
			dos.writeByte((byte) beamID);
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
		trackJam.marshal(buff);
		buff.put((byte) emitterID);
		buff.put((byte) beamID);
	} // end of marshal method

	public void setBeamID(final short pBeamID) {
		beamID = pBeamID;
	}

	public void setEmitterID(final short pEmitterID) {
		emitterID = pEmitterID;
	}

	public void setTrackJam(final EntityID pTrackJam) {
		trackJam = pTrackJam;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			trackJam.unmarshal(dis);
			emitterID = (short) dis.readUnsignedByte();
			beamID = (short) dis.readUnsignedByte();
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
		trackJam.unmarshal(buff);
		emitterID = (short) (buff.get() & 0xFF);
		beamID = (short) (buff.get() & 0xFF);
	} // end of unmarshal method
} // end of class
